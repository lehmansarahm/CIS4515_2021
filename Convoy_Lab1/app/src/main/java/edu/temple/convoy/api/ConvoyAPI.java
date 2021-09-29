package edu.temple.convoy.api;

import android.content.Context;

public class ConvoyAPI extends BaseAPI {

    // ================================================================================
    //      API RESULT LISTENER
    // ================================================================================

    public interface ResultListener {
        void onSuccess(String convoyID);
        void onFailure(String message);
    }

    // ================================================================================

    public ConvoyAPI(Context initialContext) {
        super(initialContext);
    }

    @Override
    protected String getApiSuffix() {
        return "convoy.php";
    }

    /**
     * Create a new convoy using the remote API
     *
     * @param listener
     */
    public void create(CreateListener listener) {

    }

    /**
     * End an existing convoy using the remote API
     * 
     * @param listener
     */
    public void end(EndListener listener) {

    }

    // ================================================================================
    //      API RESULT LISTENERS
    // ================================================================================

    public interface CreateListener {
        void onSuccess();
        void onFailure();
    }

    public interface EndListener {
        void onSuccess();
        void onFailure();
    }

}