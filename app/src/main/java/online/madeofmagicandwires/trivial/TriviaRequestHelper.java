package online.madeofmagicandwires.trivial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

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
     * Listener interface responding to requests made by the TriviaRequestHelper class
     */
    public interface TriviaRequestListener {
        /**
         * called when an error occurs during a request to the OpenTrivia API
         * @param lastRequest the endpoint of the request
         * @param errorMsg the error message included.
         */
        void OnResponseError(@TriviaRequestHelper.EndPoint String lastRequest, @Nullable String errorMsg);
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
    private static @EndPoint String lastRequest;
    private static TriviaRequestListener Listener;

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

    }
}
