package online.madeofmagicandwires.trivial.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import online.madeofmagicandwires.trivial.R;
import online.madeofmagicandwires.trivial.models.HighscoreList;

public class HighscoreRequestHelper extends VolleyRequestsHelper {

    /**
     *
     */
    public interface OnHighscoreListResponseListener extends ErrorResponseListener {
        void OnHighscoreListResponse(List<HighscoreList.Score> scores);
    }

    public static final String DEFAULT_API_URL = "http://trivial.madeofmagicandwires.online";
    public static final int DEFAULT_API_PORT = 8080;
    public static final String DEFAULT_ENDPOINT = "list";

    private static HighscoreRequestHelper instance;
    private HighscoreList list;
    private String apiUrl;
    private int apiPort;

    private ErrorResponseListener listener;

    /**
     * Standard Constructor; not to be used directly.
     *
     * @param context application context used to create a requestqueue
     * @see #getInstance(Context, HighscoreList)
     * @see #getInstance(Context)
     */
    private HighscoreRequestHelper(@NonNull Context context, HighscoreList list) {
        super(context);
        this.list = list;
        SharedPreferences prefs = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_file),
                Context.MODE_PRIVATE
        );
        this.apiUrl = prefs.getString(
                context.getString(R.string.highscore_api_url_pref_key),
                DEFAULT_API_URL
        );
        this.apiPort = prefs.getInt(context.getString(
                R.string.highscore_api_port_pref_key),
                DEFAULT_API_PORT
        );
    }

    /**
     * Used instead of a constructor; returns the singleton instance of this helper,
     * which can then be used normally
     * @param context
     * @param list
     * @return
     */
    public static HighscoreRequestHelper getInstance(@NonNull Context context, HighscoreList list) {
        if(instance == null) {
            instance = new HighscoreRequestHelper(context, list);
        }
        return instance;
    }

    public static HighscoreRequestHelper getInstance(@NonNull Context context) {
        return getInstance(context, null);
    }

    private void setListener(ErrorResponseListener listener) {
        if(this.listener != listener) {
            this.listener = listener;
        }
    }

    public Uri getFullUrl(String endPoint) {
        Uri.Builder bob = Uri.parse(apiUrl + ":" + apiPort).buildUpon();
        bob.appendPath(endPoint);
        return bob.build();
    }


    /**
     * Requests the full highscore list from the Highscore API
     */
    public void requestHighscores(ErrorResponseListener listener) {
        setListener(listener);
        makeRequest(Request.Method.GET, getFullUrl(DEFAULT_ENDPOINT), null);
    }

    /**
     * Requests the highscores from the Highscore API, filtered for a single user
     * @param userName the username to filter results for
     */
    public void requestHighscores(ErrorResponseListener listener, String userName) {
        setListener(listener);
        Uri.Builder fullUrl = getFullUrl(DEFAULT_ENDPOINT).buildUpon();
        fullUrl.appendQueryParameter("name", userName);
        makeRequest(Request.Method.GET, fullUrl.build(), null);
    }

    /**
     * Posts a new score to the Highscore API
     * @param score the score numerical value
     * @param name the associated name
     */
    public void postHighscore(ErrorResponseListener listener, int score, String name) {
        setListener(listener);
        JSONObject data = new JSONObject();
        try {
            data.putOpt("score" , score);
            data.putOpt("name", name);

            if(data.length() != 0) {
                makeRequest(Request.Method.POST, getFullUrl(DEFAULT_ENDPOINT), data);
            }
        } catch (JSONException e) {
            if(listener != null) {
                listener.OnErrorResponse(e.getLocalizedMessage());
            }
        }
    }

    public void updateHighscore(ErrorResponseListener listener, int id, int score, String name) {

    }

    public void updateHighscore(ErrorResponseListener listener, int id, HighscoreList.Score score) {

    }

    public void removeHighscore(int id) {

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
        VolleyLog.e("%s", error);
        if(listener != null) {
            listener.OnErrorResponse(error.getLocalizedMessage());
        }

    }

    /**
     * Called when a response is received in JSON format.
     * Parses the JSON Response into an array of Strings representing each category.
     *
     * @param response response object
     */
    @Override
    public void onResponse(JSONObject response) {
        try {
            // parse score entries from /list
            if(response.length() == 1) {
                JSONArray responseArr;
                responseArr = response.toJSONArray(response.names());
                if(responseArr != null && responseArr.length() > 0) {
                    List<HighscoreList.Score> scores = new ArrayList<>();
                    for(int i=0; i<responseArr.length();i++) {
                        HighscoreList.Score entry = parseScore(responseArr.getJSONObject(i));
                        if(entry != null) {
                            scores.add(entry);
                        }
                    }
                    // notify listener if results are available
                    if(scores.size() > 0 && listener instanceof OnHighscoreListResponseListener) {
                        ((OnHighscoreListResponseListener) listener).OnHighscoreListResponse(scores);
                    }
                }
            }
        } catch (JSONException e) {
            if(listener !=  null) {
                listener.OnErrorResponse("Could not parse JSON response");
            }
        }

    }

    /**
     * parses a score entry from JSONObject
     * @param scoreResponse JSON response to parse for a score enty
     * @return {@link online.madeofmagicandwires.trivial.models.HighscoreList.Score} if a score was
     *         found; null otherwise
     */
    private HighscoreList.Score parseScore(JSONObject scoreResponse) {
        try {
            return new HighscoreList.Score(
                    scoreResponse.getInt("id"),
                    scoreResponse.getInt("score"),
                    scoreResponse.getString("name")
            );

        } catch (JSONException e)  {
            return null;
        }
    }
}
