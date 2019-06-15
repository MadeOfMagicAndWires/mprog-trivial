package online.madeofmagicandwires.trivial;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

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
import java.util.HashMap;

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
         * called when a TriviaDB session request has been successfully requeste
         * @param token the session token retrieved
         */
        void OnTokenRequestSuccess(String token);
        void OnTokenResetSuccess(String token);
    }

    /**
     * Listener interface for responding to question requests by the TriviaRequestHelper
     *
     * @see #requestQuestions(int, QuestionRequestListener)
     */
    public interface QuestionRequestListener extends TriviaRequestListener {
        void OnQuestionsRequestSuccess(JSONArray questions);

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

    /**
     * Standard constructor
     * @param appContext the application context, needed for the Volley Request Queue
     */
    private TriviaRequestHelper(@NonNull Context appContext){
        super(appContext);
    }

    /**
     * Used to instantiate the TriviaRequestsHelper class
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
     * Gets the session token, if initialized
     * @return the session token in string format, or null if one isn't requested yet
     * @see #requestSessionToken(SessionTokenRequestListener)
     */
    public String getSessionToken() {
        return sessionToken;
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
                    JSONArray questions = response.optJSONArray("results");
                    if(listener instanceof QuestionRequestListener) {
                        ((QuestionRequestListener) listener).OnQuestionsRequestSuccess(questions);
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
                listener.OnResponseError(
                        lastRequest,
                        "Could not parse JSON object: " + response.toString()
                );

        }

    }

    /**
     * Gets the event listener set to resolve API requests
     * @return an object implementing the TriviaRequestListener interface
     */
    public TriviaRequestListener getListener() {
        return listener;
    }

    /**
     * Handles pre-request necessities.
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
     * Requests a session token from the OpenTriviaDB
     * @param listener listener listener for this TriviaRequest instance
     * @see SessionTokenRequestListener for the listener interface
     */
    public void requestSessionToken(SessionTokenRequestListener listener) {
        // pre-request
        prepareForRequest(EndPoint.SESSION, listener);

        makeRequest(Request.Method.GET, API_URL + EndPoint.SESSION, "command=request");
    }

    /**
     * Request to reset a session token from the OpenTriviaDB
     * @param listener listener listener for this TriviaRequest instance
     * @see SessionTokenRequestListener for the listener interface
     */
    public void resetSessionToken(SessionTokenRequestListener listener) {
        // pre-request
        prepareForRequest(EndPoint.SESSION, listener);

        // session token exists
        if(this.sessionToken != null && !this.sessionToken.isEmpty()) {

            // set the query paramaters and make the request
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
     * Requests a set amount of questions from the OpenTriviaDB
     * @param amount the amount of questions to request
     * @param listener the event listener interface that be called to resolve the response
     * @see QuestionRequestListener for the listener interface
     */
    public void requestQuestions(int amount, QuestionRequestListener listener) {
        //pre-request
        prepareForRequest(EndPoint.TRIVIA, listener);

        //set the query parameters: amount of questions, encoding, and session token if available
        HashMap<String, String> params = new HashMap<>();
        params.put("amount", Integer.toString(amount));
        params.put("encoding", "url3986");
        if(sessionToken != null && !sessionToken.isEmpty()) {
            params.put("token", sessionToken);
        }

        makeRequest(Request.Method.GET, API_URL + EndPoint.TRIVIA, params);

    }

    /**
     * Parses a JSONObject for any trivia questions provided by the OpenTriviaDB
     *
     * @param question the JSONObject to parse for any question
     * @return a TriviaQuestion object containing the data parsed from the JSONObject
     */
    public TriviaQuestion parseQuestionJSON(JSONObject question) throws JSONException {
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
                // TODO parse multiple choice questions
                return null;

                default:
                    return null;

        }

    }

    /**
     * Returns a URL encoded response to a human-readable UTF-8 String
     *
     * @param encoded the string to be decoded
     * @return the decoded string
     */
    public String decodeResponseStr(String encoded) {
        try {
            return URLDecoder.decode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
