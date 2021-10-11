package edu.temple.convoy.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import edu.temple.convoy.utils.Constants;

public abstract class BaseAPI {

    // ================================================================================
    //      API RESULT LISTENER
    // ================================================================================

    public interface ResultListener {
        void onSuccess(String sessionKey);  // or convoy ID
        void onFailure(String message);
    }

    // ================================================================================

    protected Context context;

    protected BaseAPI(Context initialContext) {
        this.context = initialContext;
    }

    protected abstract String getApiSuffix();

    protected void post(Map<String, String> params, Response.Listener<String> successListener) {
        StringRequest request = new StringRequest(Request.Method.POST,
                (Constants.BASE_API_URL + getApiSuffix()), successListener, getErrorListener()) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        Log.i(Constants.LOG_TAG, "Attempting to submit POST request: " + request + " with params: " + params);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    private Response.ErrorListener getErrorListener() {
        /*
            Lambda notation ... takes the place of:
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
         */
        Response.ErrorListener errorListener = error -> {
            Log.e(Constants.LOG_TAG, "Error: " + error.getMessage());
            Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
        };
        return errorListener;
    }

}