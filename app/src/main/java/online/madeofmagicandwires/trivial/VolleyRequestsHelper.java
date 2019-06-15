package online.madeofmagicandwires.trivial;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

public abstract class VolleyRequestsHelper implements Response.ErrorListener, Response.Listener<JSONObject> {


    /** context to be used by Volley **/
    private RequestQueue queue;

    /**
     * Standard Constructor
     * @param context application context used to create a requestqueue
     */
    public VolleyRequestsHelper(@NonNull Context context) {
        this.queue = Volley.newRequestQueue(context);
    }

    /**
     * Callback method that an error has been occurred with the provided error code and optional
     * user-readable message.
     *
     * @param error exception containing information on what went wrong
     */
    @Override
    abstract public void onErrorResponse(VolleyError error);

    /**
     * Called when a response is received in JSON format.
     * Parses the JSON Response into an array of Strings representing each category.
     *
     * @param response response object
     */
    @Override
    abstract public void onResponse(JSONObject response);


    /**
     * Make a Volley Request with possible data attached
     * @param method the method to be used, must be one of {@link com.android.volley.Request.Method}
     * @param url the URI to send the request to
     * @param data possible data to send along with the request
     * @see #makeRequest(int, String, Map) for using query parameters instead of attaching POST data
     */
    public void makeRequest(int method, Uri url, @Nullable JSONObject data) {
        try {
            JsonObjectRequest request = new JsonObjectRequest(
                    method,
                    url.toString(),
                    data,
                    this,
                    this
            );
            queue.add(request);
        } catch (NullPointerException e) {
            onErrorResponse(new VolleyError(e.getMessage()));
        }
    }


    /**
     * Make a Volley Request with possible data attached
     * @param method the method to be used, must be one of {@link com.android.volley.Request.Method}
     * @param url the endpoint to send the request to
     * @param data possible data to send along with the request
     * @see #makeRequest(int, String, Map) for using query parameters instead of attaching POST data
     */
    public void makeRequest( int method,  String url, @Nullable JSONObject data) {
        Log.d("makeRequest", url);
        try {
            JsonObjectRequest request = new JsonObjectRequest(
                    method,
                    url,
                    data,
                    this,
                    this
            );
            queue.add(request);


        } catch (NullPointerException e) {
            onErrorResponse(new VolleyError(e.getMessage()));
        }

    }

    /**
     * Make a Volley Request using a query parameter instead of attaching data
     * @param method the method to be used; must be one of {@link com.android.volley.Request.Method}
     * @param url the endpoint to send the request to
     * @param queryParams the query parameters to attach to the request url
     * @see #makeRequest(int, String, Map) for more passing than one query parameter;
     */
    public void makeRequest(int method, String url, String queryParams) {
        makeRequest(method, url + "?" + queryParams, (JSONObject) null);
    }

    /**
     * Make a Volley Request using <b>multiple</b> query parameters instead of attaching data
     * @param method the method to be used; must be one of {@link com.android.volley.Request.Method}
     * @param url the endpoint to send the request to
     * @param queryParams an array of query parameters to attach to the request url
     * @see #makeRequest(int, String, String) to pass query parameters as a string;
     *                                        useful when you've fewer parameters
     */
    public void makeRequest(int method, String url, Map<String, String> queryParams) {
        Uri.Builder queryUri = Uri.parse(url).buildUpon();
        queryParams.forEach(queryUri::appendQueryParameter);
        makeRequest(method, queryUri.build(), null);
    }


}
