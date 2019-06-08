package online.madeofmagicandwires.trivial;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

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
     * Make a Volley Request
     * @param method the method to be used, must be one of {@link com.android.volley.Request.Method}
     * @param url the endpoint to send the request to
     * @param data the data to send along the request
     */
    public void makeRequest( int method,  String url, @Nullable JSONObject data) {
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
     * Make a Volley Request using query parameters instead of attaching data
     * @param method the method to be used; must be one of {@link com.android.volley.Request.Method}
     * @param url the endpoint to send the request to
     * @param queryParams the query parameters to attach to the request url
     */
    public void makeRequest(int method, String url, String queryParams) {
        makeRequest(method, url + "?" + queryParams, (JSONObject) null);
    }

    /**
     * Make a Volley Request using multiple query parameters instead of attaching data
     * @param method the method to be used; must be one of {@link com.android.volley.Request.Method}
     * @param url the endpoint to send the request to
     * @param queryParams an array of query parameters to attach to the request url
     */
    @TargetApi(Build.VERSION_CODES.O)
    public void makeRequest(int method, String url, String[] queryParams) {
        String joined;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            joined = String.join("&", queryParams);
        } else {
            joined = TextUtils.join("&", queryParams);
        }

        makeRequest(method, url + "?" + joined, (JSONObject) null);
    }


}
