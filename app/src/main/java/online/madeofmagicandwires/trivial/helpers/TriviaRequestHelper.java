package online.madeofmagicandwires.trivial.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;
import android.util.SparseArray;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import online.madeofmagicandwires.trivial.models.MultipleChoiceQuestion;
import online.madeofmagicandwires.trivial.models.TriviaGame;
import online.madeofmagicandwires.trivial.models.TriviaQuestion;
import online.madeofmagicandwires.trivial.models.TrueFalseQuestion;

/**
 * A Helper class for the making and interpreting requests to the OpenTriviaDB API.
 */
public class TriviaRequestHelper extends VolleyRequestsHelper {

    /**
     * Listener interface superclass for responding to requests made by the TriviaRequestHelper class
     * Only handles the response errors and is not to be implemented directly
     */
    private interface ErrorResponseListener {
        /**
         * called when an error occurs during a request to the OpenTrivia API
         * @param lastRequest the endpoint of the request;
         *                    note that this might not be entirely accurate due to async requests
         * @param errorMsg the error message included.
         */
        void OnErrorResponse(@TriviaRequestHelper.EndPoint String lastRequest, @Nullable String errorMsg);
    }

    /**
     * Listener interface for responding to a category requests
     */
    public interface CategoriesResponseListener extends ErrorResponseListener {

        /**
         * Called when a TriviaDB categories request has successfully resolved
         * @param categories a JSONObject containing the categories under the "trivia_categories" key
         */
        void OnCategoriesResponse(SparseArray<String> categories);

    }
    /**
     * Listener interface for responding to question requests by the TriviaRequestHelper
     *
     * @see #requestQuestions(int, QuestionResponseListener)
     */
    public interface QuestionResponseListener extends ErrorResponseListener {
        /**
         * Called when a TriviaDB question request successfully resolved
         * @param questions a {@link List} of {@link TriviaQuestion} objects
         *                  representing the trivia questions that were retrieved from the TriviaDB
         * @see TriviaRequestHelper#requestQuestions(int, QuestionResponseListener)
         */
        void OnQuestionsResponse(List<TriviaQuestion> questions);


    }

    /**
     * Event listener interface for responding to question count requests
     */
    public interface QuestionCountResponseListener extends ErrorResponseListener {
        /**
         * Called when a global question count request is resolved
         * @param total the global amount of (verified) questions in the TriviaDB,
         *              or -1 if the value could not successfully be retrieved
         * @param categoryCount the total count of questions belonging to the category
         *                      set for this session;
         *                      will be the same value as the global question count
         *                      if this session's category is set to 'any',
         *                      or -1 if the value could not be retrieved
         */
        void OnQuestionCountResponse(int total, int categoryCount);

        /**
         * Called when a specific category's question count request is resolved
         * @param total the total amount of (verified) questions belonging to this category,
         *              or -1 if the value could not successfully be retrieved
         * @param difficultyCount the total count of questions in this category of the difficulty
         *                           set for this session;
         *                           will be the same value as the category's total if the difficulty
         *                           is set to null,
         *                           or -1 if the number could not successfully be retrieved
         */
        void OnCategoryQuestionCountResponse(int total, int difficultyCount);

    }

    /**
     * Listener interface for responding to session token requests by the TriviaRequestHelper class
     *
     * @see TriviaRequestHelper#requestSessionToken(SessionTokenResponseListener)
     * @see ErrorResponseListener#resetSessionToken(SessionTokenResponseListener)
     */
    public interface SessionTokenResponseListener extends ErrorResponseListener {

        /**
         * Called when a TriviaDB session token request has been successfully requested
         *
         * @param token the session token retrieved
         * @see TriviaRequestHelper#requestSessionToken(SessionTokenResponseListener)
         */
        void OnRequestTokenResponse(String token);
        /**
         * Called when a TriviaDB session token has been succesfully reset,
         * meaning all previously asked questions can be asked again
         *
         * @param token the session token that has been reset
         * @see TriviaRequestHelper#resetSessionToken(SessionTokenResponseListener)
         */
        void OnResetTokenResponse(String token);


    }

    /**
     * Event Listener interface for responding to <b>all</b> TriviaDB related requests possible
     * See the individual response interfaces for more fine-tuning but use this
     * if you are unsure which to use or lazy
     */
    public interface TriviaResponseListener extends
            CategoriesResponseListener,
            QuestionResponseListener,
            QuestionCountResponseListener,
            SessionTokenResponseListener {

    }
    /**
     * Android-efficient enum representing the various endpoints of the API
     *
     */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            EndPoint.SESSION,        // requesting a sessionID
            EndPoint.TRIVIA,         // requesting questions
            EndPoint.CATEGORY,       // requesting a list of available categories
            EndPoint.CATEGORY_COUNT, // requesting the total amount of questions in a category
            EndPoint.COUNT           // requesting the total amount of questions in the database
    })
    @interface EndPoint {


        String SESSION = "api_token.php";
        String TRIVIA = "api.php";
        String CATEGORY = "api_category.php";
        String CATEGORY_COUNT = "api_count.php";
        String COUNT = "api_count_global.php";
    }

    /** root url for the API **/
    private final static String API_URL = "https://opentdb.com/";

    private static TriviaRequestHelper instance;

    private @EndPoint String lastRequest;
    private ErrorResponseListener listener;
    private String sessionToken;
    private Integer category;
    private @TriviaGame.Difficulty
    String difficulty;

    /**
     * Standard constructor
     *
     * @param appContext the application context, needed for the Volley Request Queue
     */
    private TriviaRequestHelper(@NonNull Context appContext){
        super(appContext);
    }

    /**
     * Used to retrieve the TriviaRequestsHelper singleton
     *
     * @param appContext the application context, needed for the Volley Request Queue
     * @return the singleton TriviaRequestHelper instance
     */
    public static TriviaRequestHelper getInstance(@NonNull Context appContext){
        if(instance==null) {
            instance = new TriviaRequestHelper(appContext);
        }
        return instance;
    }

    /**
     * Used to retrieve the TriviaRequestHelper singleton and sets
     * the difficulty and category according to the values of the {@link TriviaGame} object
     *
     * @param appContext the application context, needed for the Volley Request Queue
     * @param game the TriviaGame to retrieve difficulty and categoryId from
     * @return the singleton TriviaRequestHelper instance with the provided settings
     */
    public static TriviaRequestHelper getInstance(
            @NonNull Context appContext,
            TriviaGame game) {
        instance = getInstance(appContext);
        if(game != null) {
            instance.setCategoryId(game.getGameCategoryId());
            instance.setDifficulty(game.getGameDifficulty());
        }
        return instance;
    }

    /**
     * Gets the session token, if initialized
     * @return the session token in string format, or null if one isn't requested yet
     * @see #requestSessionToken(SessionTokenResponseListener)
     */
    public String getSessionToken() {
        return sessionToken;
    }


    /**
     * Gets the difficulty of this session
     * @return the difficulty of this session;
     *         when set to {@link TriviaGame.Difficulty#ANY} questions of all difficulties
     *         will be retrieved
     */
    public @TriviaGame.Difficulty String getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the difficulty of this session
     * @param difficulty the difficulty of questions to request from the TriviaDB,
     *                   must be one of {@link TriviaGame.Difficulty}
     *                   when set to {@link TriviaGame.Difficulty#ANY} questions of
     *                   all difficulties will be retrieved
     */
    public void setDifficulty(@TriviaGame.Difficulty String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Gets the category of this session
     *
     * @return the category of questions to request from the TriviaDB,
     *         if set to null questions of all categories will be requested
     */
    public Integer getCategoryId() {
        return category;
    }

    /**
     * Sets the category of this session
     *
     * @param category the category questions requested this session should belong to,
     *                 if set to null questions of all categories will be requested
     */
    public void setCategoryId(Integer category) {
        this.category = category;
    }

    /**
     * Requests a session token from the OpenTriviaDB
     * @param listener the event listener interface to be called after requests have been resolved
     * @see TriviaRequestHelper.SessionTokenResponseListener for the listener interface
     */
    public void requestSessionToken(SessionTokenResponseListener listener) {
        // pre-request
        prepareForRequest(EndPoint.SESSION, listener);

        makeRequest(Request.Method.GET, API_URL + EndPoint.SESSION, "command=request");
    }

    /**
     * Request to reset a session token from the OpenTriviaDB
     * @param listener the event listener interface to be called after requests have been resolved
     * @see TriviaRequestHelper.SessionTokenResponseListener for the listener interface
     */
    public void resetSessionToken(SessionTokenResponseListener listener) {
        // pre-request
        prepareForRequest(EndPoint.SESSION, listener);

        // session token exists
        if(this.sessionToken != null && !this.sessionToken.isEmpty()) {

            // set the query parameters and make the request
            HashMap<String, String> params = new HashMap<>();
            params.put("command", "reset");
            params.put("token", this.sessionToken);
            makeRequest(
                    Request.Method.GET,
                    API_URL + EndPoint.SESSION,
                    params
            );
        } else { // just request a new token if none exists already
            requestSessionToken(listener);
        }

    }

    /**
     * Requests the available Trivia categories from the TriviaDB
     *
     * @param listener the event listener interface to be called after requests have been resolved
     * @see TriviaRequestHelper.CategoriesResponseListener for the event listener interface
     */
    public void requestCategories(CategoriesResponseListener listener) {
        prepareForRequest(EndPoint.CATEGORY, listener);
        makeRequest(API_URL + EndPoint.CATEGORY);
    }

    /**
     * Requests a set amount of questions from the OpenTriviaDB
     * @param amount the amount of questions to request, up to a maximum of 50
     * @param listener the event listener interface to be called after requests have been resolved
     * @see TriviaRequestHelper.QuestionResponseListener for the listener interface
     */
    public void requestQuestions(int amount, QuestionResponseListener listener) {
        //pre-request
        prepareForRequest(EndPoint.TRIVIA, listener);

        //set the query parameters: amount of questions, encoding, and session token if available
        HashMap<String, String> params = new HashMap<>();
        // pass on the amount of questions to be requested, maximum of 50.
        params.put("amount", (amount < 50) ? String.valueOf(amount) : String.valueOf(50));
        // set value encoding to ISO 3986
        params.put("encode", "url3986");
        if(sessionToken != null && !sessionToken.isEmpty()) {
            params.put("token", sessionToken);
        }
        if(difficulty != null) {
            params.put("difficulty", difficulty);
        }
        if(category != null && category > 0) {
            params.put("category", category.toString());
        }

        makeRequest(Request.Method.GET, API_URL + EndPoint.TRIVIA, params);

    }



    /**
     * Request the total amount of (verified) questions in the TriviaDB database
     *
     * @param listener the event listener to be called once the request has been resolved
     * @param categoryId the id of a specific category to check,
     *                   if set to null it will query the count of all databases
     * @see TriviaRequestHelper.QuestionCountResponseListener for the event listener interface
     */
    public void requestQuestionCount(
            QuestionCountResponseListener listener,
            @Nullable Integer categoryId) {
        // check specific category
        if(categoryId != null && categoryId != -1) {
            prepareForRequest(EndPoint.CATEGORY_COUNT, listener);
            makeRequest(
                    Request.Method.GET,
                    API_URL + EndPoint.CATEGORY_COUNT,
                    "category=" + categoryId
            );
        }

        else {
            prepareForRequest(EndPoint.COUNT, listener);
            makeRequest(API_URL + EndPoint.COUNT);
        }

    }


    /**
     * Requests the global total amount of (verified) questions
     * @param listener the event listener to be called once the request has been resolved
     */
    public void requestQuestionCount(QuestionCountResponseListener listener) {
        requestQuestionCount(listener, null);
    }

    /**
     * Called when a text/json response is received.
     *
     * Handles the various types of responses and calls the right event listener when the request
     * has been resolved
     *
     * @param response response to the request in JSON format
     *
     * @see CategoriesResponseListener interface called when the response contains  trivia categories
     * @see QuestionResponseListener interface called when the response contains trivia questions
     * @see SessionTokenResponseListener interface called when the response contains a session token
     *
     */
    @Override
    public void onResponse(JSONObject response) {
        switch (response.optInt("response_code", -1)) {
            case 0:
                // Session Token parsing
                if(response.has("token")) {
                    sessionToken = response.optString("token", "");

                    // notify listener if available
                    if(listener instanceof SessionTokenResponseListener) {

                        if(response.has("response_message")) { // initial token request
                            ((SessionTokenResponseListener) listener).OnRequestTokenResponse(sessionToken);
                        } else { // token reset request
                            ((SessionTokenResponseListener) listener).OnResetTokenResponse(sessionToken);
                        }
                    }

                }
                // Trivia questions parsing
                if(response.has("results")) {
                    // parse each question and add them to a list
                    JSONArray questions = response.optJSONArray("results");
                    List<TriviaQuestion> results = new ArrayList<>();
                    for(int i=0;i<questions.length();i++) {
                        try {
                            TriviaQuestion question = parseQuestionJSON(questions.getJSONObject(i));
                            results.add(question);
                        } catch (JSONException e) {
                            Log.e("TriviaRequestHelper", e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    // notify listener if available
                    if(listener instanceof QuestionResponseListener) {
                        ((QuestionResponseListener) listener).OnQuestionsResponse(results);
                    }
                }

                break;
            case 1:
                listener.OnErrorResponse(lastRequest, "no results");
                break;
            case 2:
                listener.OnErrorResponse(lastRequest, "contained an invalid parameter");
                break;
            case 3:
                listener.OnErrorResponse(lastRequest, "Token Not Found");
                break;
            case 4:
                listener.OnErrorResponse(lastRequest, "no remaining questions");
                break;

            default:
                // Handle Trivia Categories request
                if(response.has("trivia_categories")) {
                    if(listener instanceof CategoriesResponseListener) {
                        SparseArray<String> categories = new SparseArray<>();
                        JSONArray categoryArr = response.optJSONArray("trivia_categories");
                        for(int i=0;i<categoryArr.length();i++){
                            JSONObject entry = categoryArr.optJSONObject(i);
                            if(entry != null) {
                                categories.append(
                                        entry.optInt("id", -1),
                                        entry.optString("name", "Unknown")
                                );
                            }

                        }

                        // notify listener if available
                        if(listener instanceof CategoriesResponseListener) {
                            ((CategoriesResponseListener) listener)
                                    .OnCategoriesResponse(categories);
                        }
                    }
                }

                // Handle Global Trivia Question count requests
                else if(response.has("overall")) {
                    int overallQuestionCount;
                    int categoryQuestionCount;
                    overallQuestionCount = response.optJSONObject("overall").optInt("total_num_of_verified_questions", -1);

                    // if a session category is set, set categoryQuestion to respective value from response
                    if(getCategoryId() != null && getCategoryId() != -1 && response.has("categories")) {
                        JSONObject category = response.optJSONObject("categories").optJSONObject(String.valueOf(getCategoryId()));
                        if(category != null) {
                            categoryQuestionCount = category.optInt("total_num_of_verified_questions", -1);
                        } else {
                            categoryQuestionCount = -1;
                        }

                    }
                    // otherwise set it equal to overAllQuestionCount
                    else {
                        categoryQuestionCount = overallQuestionCount;
                    }

                    // notify listener if available
                    if(listener instanceof QuestionCountResponseListener) {
                        ((QuestionCountResponseListener) listener).OnQuestionCountResponse(overallQuestionCount, categoryQuestionCount);
                    }

                }

                // Handle Category specific question count requests
                else if(response.has("category_id")) {
                    int totalCount;
                    int difficultyCount;
                    JSONObject category = response.optJSONObject("category_question_count");
                    totalCount = category.optInt("total_question_count", -1);

                    // if session difficulty is set difficultyCount to respective value from response
                    if(getDifficulty() != null) {
                        switch (getDifficulty()) {
                            case TriviaGame.Difficulty.EASY:
                                difficultyCount = category
                                        .optInt("total_easy_question_count", -1);
                                break;
                            case TriviaGame.Difficulty.MEDIUM:
                                difficultyCount = category
                                        .optInt("total_medium_question_count", -1);
                                break;
                            case TriviaGame.Difficulty.HARD:
                                difficultyCount = category
                                        .optInt("total_hard_question_count", -1);
                                break;
                            default:
                                difficultyCount = totalCount;
                                break;
                        }

                    }
                    // otherwise set it equal to totalCount
                    else {
                        difficultyCount = totalCount;
                    }

                    // notify listener if available
                    if(listener instanceof QuestionCountResponseListener) {
                        ((QuestionCountResponseListener) listener)
                                .OnCategoryQuestionCountResponse(totalCount, difficultyCount);
                    }

                }

                // Otherwise, we don't know what this is, error out
                else {
                    listener.OnErrorResponse(
                            lastRequest,
                            "Could not parse retrieved JSON object: " + response.toString()
                    );
                }



        }

    }

    /**
     * Callback method that an error has been occurred with the provided error code and optional
     * user-readable message.
     *
     * @param error exception containing information on what went wrong
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        VolleyLog.e("TriviaRequestHelper", error.getLocalizedMessage());
        if(listener != null) {
            listener.OnErrorResponse(lastRequest, error.getLocalizedMessage());
        }
    }

    /**
     * Handles pre-request necessities
     *
     * @param endPoint sets the last request's that can be used for error handling
     * @param listener sets the event listener for the current request
     */
    private void prepareForRequest(@EndPoint String endPoint, ErrorResponseListener listener) {
        this.lastRequest = endPoint;
        if(listener != this.listener) {
            this.listener = listener;
        }
    }



    /**
     * Returns a URL encoded response to a human-readable UTF-8 String
     *
     * @param encoded the string to be decoded
     * @return the decoded string
     */
    private String decodeResponseStr(String encoded) {
        try {
            return URLDecoder.decode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Parses a JSONObject for any trivia questions provided by the OpenTriviaDB
     *
     * @param question the JSONObject to parse for any question
     * @return a TriviaQuestion object containing the data parsed from the JSONObject
     */
    private TriviaQuestion parseQuestionJSON(JSONObject question) throws JSONException {
        @TriviaGame.QuestionType String qType = question.getString("type");
        switch (qType) {
            case TriviaGame.QuestionType.BOOLEAN:
                return new TrueFalseQuestion(
                        decodeResponseStr(question.getString("question")),
                        Boolean.parseBoolean(question.getString("correct_answer")),
                        question.getString("difficulty"),
                        decodeResponseStr(question.getString("category"))
                );
            case TriviaGame.QuestionType.MULTIPLE:
                // Parse incorrect_answers array
                JSONArray wrongAnswersArr = question.getJSONArray("incorrect_answers");
                String[] wrongAnswers = new String[wrongAnswersArr.length()];
                for(int i=0;i<wrongAnswersArr.length();i++){
                    wrongAnswers[i] = decodeResponseStr(wrongAnswersArr.getString(i));
                }

                return new MultipleChoiceQuestion(
                        decodeResponseStr(question.getString("question")),
                        decodeResponseStr("correct_answer"),
                        wrongAnswers,
                        question.getString("difficulty"),
                        decodeResponseStr(question.getString("category"))
                );


                default:
                    throw new JSONException("Could not find trivia question from JSON Object");

        }
    }
}
