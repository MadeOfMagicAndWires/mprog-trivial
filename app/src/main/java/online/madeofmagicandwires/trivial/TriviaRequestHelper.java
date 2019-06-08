package online.madeofmagicandwires.trivial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A Helper class for the making and interpreting requests to the OpenTriviaDB API.
 */
public class TriviaRequestHelper extends VolleyRequestsHelper {

    /**
     * Listener interface superclass for responding to requests made by the TriviaRequestHelper class
     */
    public interface TriviaRequestListener {
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
     */
    public interface SessionTokenRequestListener extends TriviaRequestListener {
        /**
         * called when a TriviaDB session request has been successfully requeste
         * @param token the session token retrieved
         */
        void OnTokenRequestSuccess(String token);
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
     * Parses the JSON Response into an array of Strings representing each category.
     *
     * @param response response object
     */
    @Override
    public void onResponse(JSONObject response) {
        switch (response.optInt("response_code", -1)) {
            case 0:
                if(response.has("token")) { // session token parsing
                    this.sessionToken = response.optString("token", "");
                    if(listener instanceof SessionTokenRequestListener) {
                        ((SessionTokenRequestListener) listener)
                                .OnTokenRequestSuccess(this.sessionToken);
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
     * Sets the API Endpoint the last request was made to
     * @param lastRequest the API Endpoint to be set
     */
    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }

    /**
     * Gets the event listener set to resolve API requests
     * @return a object implementing a TriviaRequestListener
     */
    public TriviaRequestListener getListener() {
        return listener;
    }

    /**
     * Sets the Event listeners for the resolving of requests
     * @param listener
     */
    public void setListener(TriviaRequestListener listener) {
        if(listener != this.listener) {
            this.listener = listener;
        }
    }

    /**
     * Requests a session token from the OpenTriviaDB
     * @param callback callback listener for this TriviaRequest instance
     */
    public void requestSessionToken(SessionTokenRequestListener callback) {
        // pre-request
        setLastRequest(EndPoint.SESSION);
        setListener(callback);

        makeRequest(Request.Method.GET, API_URL + EndPoint.SESSION, "command=request");
    }

    /**
     * Request to reset a session token from the OpenTriviaDB
     * @param callback callback listener for this TriviaRequest instance
     */
    public void resetSessionToken(SessionTokenRequestListener callback) {
        // session token exists
        if(sessionToken != null && !sessionToken.isEmpty()) {
            // pre-request
            setLastRequest(EndPoint.SESSION);
            setListener(callback);
            makeRequest(
                    Request.Method.GET,
                    API_URL + EndPoint.SESSION,
                    new String[]{"command=reset", "token=" + this.sessionToken}
            );
        } else {
            requestSessionToken(callback);
        }

    }
}
