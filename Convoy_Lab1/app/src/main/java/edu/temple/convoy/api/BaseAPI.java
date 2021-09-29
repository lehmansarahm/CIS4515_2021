package edu.temple.convoy.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

public abstract class BaseAPI {

    private static final String LOG_TAG = "BaseAPI";
    protected static final String BASE_API_URL = "https://kamorris.com/lab/convoy/";

    protected Context context;

    protected BaseAPI(Context initialContext) {
        this.context = initialContext;
    }

    private Response.ErrorListener getErrorListener() {
        /*
            Lambda notation ... takes the place of:
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
         */
        Response.ErrorListener errorListener = error -> {
            Log.e(LOG_TAG, "Error: " + error.getMessage());
            Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
        };
        return errorListener;
    }

    protected void post(Map<String, String> params, Response.Listener<String> successListener) {
        StringRequest request = new StringRequest(Request.Method.POST,
                (BASE_API_URL + getApiSuffix()), successListener, getErrorListener()) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        Log.i(LOG_TAG, "Attempting to submit POST request: " + request + " with params: " + params);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    protected abstract String getApiSuffix();

    /**
     * Constructs the full API URL with the provided parameters
     *
     * @param params
     * @return
     */
    private String getFullAPI(Map<String, String> params) {
        String fullAPI = (BASE_API_URL + getApiSuffix() + "?");
        for (Map.Entry<String,String> param : params.entrySet()) {
            if (!fullAPI.endsWith("?")) fullAPI += "&";
            fullAPI += (param.getKey() + "=" + param.getValue());
        }
        return fullAPI;
    }

}