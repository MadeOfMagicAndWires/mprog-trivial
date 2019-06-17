package online.madeofmagicandwires.trivial;

import android.content.Context;
import android.net.Uri;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A Helper class for the making and interpreting requests to the OpenTriviaDB API.
 */
public class TriviaRequestHelper extends VolleyRequestsHelper {

    /**
     * Listener interface superclass for responding to requests made by the TriviaRequestHelper class
     * Only handles the response errors and is not to be implemented directly
     */
    private interface TriviaRequestListener {
        /**
         * called when an error occurs during a request to the OpenTrivia API
         * @param lastRequest the endpoint of the request;
         *                    note that this might not be entirely accurate due to async requests
         * @param errorMsg the error message included.
         */
        void OnResponseError(@TriviaRequestHelper.EndPoint String lastRequest, @Nullable String errorMsg);
    }

    /**
     * Listener interface for responding to session token requests by the TriviaRequestHelper class
     *
     * @see TriviaRequestHelper#requestSessionToken(SessionTokenRequestListener)
     * @see TriviaRequestListener#resetSessionToken(SessionTokenRequestListener)
     */
    public interface SessionTokenRequestListener extends TriviaRequestListener {
        /**
         * Called when a TriviaDB session token request has been successfully requested
         *
         * @param token the session token retrieved
         * @see TriviaRequestHelper#requestSessionToken(TriviaRequestHelper.SessionTokenRequestListener)
         */
        void OnTokenRequestSuccess(String token);

        /**
         * Called when a TriviaDB session token has been succesfully reset,
         * meaning all previously asked questions can be asked again
         *
         * @param token the session token that has been reset
         * @see TriviaRequestHelper#resetSessionToken(TriviaRequestHelper.SessionTokenRequestListener)
         */
        void OnTokenResetSuccess(String token);
    }

    /**
     * Listener interface for responding to question requests by the TriviaRequestHelper
     *
     * @see #requestQuestions(int, QuestionRequestListener)
     */
    public interface QuestionRequestListener extends TriviaRequestListener {
        /**
         * Called when a TriviaDB question request successfully resolved
         * @param questions a {@link List} of {@link TriviaQuestion} objects
         *                  representing the trivia questions that were retrieved from the TriviaDB
         * @see TriviaRequestHelper#requestQuestions(int, TriviaRequestHelper.QuestionRequestListener)
         */
        void OnQuestionsRequestSuccess(List<TriviaQuestion> questions);

    }

    /**
     * Listener interface for responding to a category requests
     */
    public interface CategoriesRequestListener extends TriviaRequestListener {
        /**
         * Called when a TriviaDB categories request has successfully resolved
         * @param categories a JSONObject containing the categories under the "trivia_categories" key
         */
        void OnCategoriesRequestSuccess(SparseArray<String> categories);
    }


    /** root url for the API **/
    private final static String API_URL = "https://opentdb.com/";

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


    private static TriviaRequestHelper instance;

    private @EndPoint String lastRequest;
    private TriviaRequestListener listener;
    private String sessionToken;
    private Integer category;
    private @TriviaGame.Difficulty String difficulty;

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
     * Used to retrieve the TriviaRequestHelper singloton and sets and sets
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
        instance.setCategoryId(game.getGameCategoryId());
        instance.setDifficulty(game.getGameDifficulty());
        return instance;
    }

    /**
     * Gets the session token, if initialized
     * @return the session token in string format, or null if one isn't requested yet
     * @see #requestSessionToken(SessionTokenRequestListener)
     */
    public String getSessionToken() {
        return sessionToken;
    }


    /**
     * Gets the difficulty of this session
     * @return
     */
    public @TriviaGame.Difficulty String getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the difficulty of this session
     * @param difficulty the difficulty of questions to request from the TriviaDB,
     *                   must be one of {@link TriviaGame.Difficulty}
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
     * @see SessionTokenRequestListener for the listener interface
     */
    public void requestSessionToken(SessionTokenRequestListener listener) {
        // pre-request
        prepareForRequest(EndPoint.SESSION, listener);

        makeRequest(Request.Method.GET, API_URL + EndPoint.SESSION, "command=request");
    }

    /**
     * Request to reset a session token from the OpenTriviaDB
     * @param listener the event listener interface to be called after requests have been resolved
     * @see SessionTokenRequestListener for the listener interface
     */
    public void resetSessionToken(SessionTokenRequestListener listener) {
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
     * @param listener the event listener interface to be called after requests have been resolved
     */
    public void requestCategories(CategoriesRequestListener listener) {
        prepareForRequest(EndPoint.CATEGORY, listener);
        makeRequest(API_URL + EndPoint.CATEGORY);
    }

    /**
     * Requests a set amount of questions from the OpenTriviaDB
     * @param amount the amount of questions to request
     * @param listener the event listener interface to be called after requests have been resolved
     * @see QuestionRequestListener for the listener interface
     */
    public void requestQuestions(int amount, QuestionRequestListener listener) {
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
        if(category != null && category > 50) {
            params.put("category", category.toString());
        }

        makeRequest(Request.Method.GET, API_URL + EndPoint.TRIVIA, params);

    }

    /**
     * Called when a response is received in JSON format.
     *
     * Handles the various types of responses and calls the right event listener when the request
     * has been resolved
     *
     * @param response response object
     */
    @Override
    public void onResponse(JSONObject response) {
        switch (response.optInt("response_code", -1)) {
            case 0:
                // Session Token parsing
                if(response.has("token")) {
                    sessionToken = response.optString("token", "");
                    if(listener instanceof SessionTokenRequestListener) {
                        if(response.has("response_message")) { // initial token request
                            ((SessionTokenRequestListener) listener).OnTokenRequestSuccess(sessionToken);
                        } else { // token reset request
                            ((SessionTokenRequestListener) listener).OnTokenResetSuccess(sessionToken);
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

                    // call event listener if available and pass on the results
                    if(listener instanceof QuestionRequestListener) {
                        ((QuestionRequestListener) listener).OnQuestionsRequestSuccess(results);
                    }
                }

                break;
            case 1:
                listener.OnResponseError(lastRequest, "no results");
                break;
            case 2:
                listener.OnResponseError(lastRequest, "contained an invalid parameter");
                break;
            case 3:
                listener.OnResponseError(lastRequest, "Token Not Found");
                break;
            case 4:
                listener.OnResponseError(lastRequest, "no remaining questions");
                break;

            default:
                // Handle Trivia Categories request
                if(response.has("trivia_categories")) {
                    if(listener instanceof CategoriesRequestListener) {
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
                        if(listener instanceof CategoriesRequestListener) {
                            ((CategoriesRequestListener) listener)
                                    .OnCategoriesRequestSuccess(categories);
                        }
                    }
                }

                // Otherwise, we don't know what this is, error out
                else {
                    listener.OnResponseError(
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
            listener.OnResponseError(lastRequest, error.getLocalizedMessage());
        }
    }

    /**
     * Handles pre-request necessities
     *
     * @param endPoint sets the last request's that can be used for error handling
     * @param listener sets the event listener for the current request
     */
    private void prepareForRequest(@EndPoint String endPoint, TriviaRequestListener listener) {
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
