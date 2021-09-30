package edu.temple.convoy.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private Context context;
    private SharedPreferences sharedPrefs;

    public SharedPrefs(Context initialContext) {
        context = initialContext;
        sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE);
    }

    public void clearAllUserSettings() {
        clearLoggedInUser();
        clearSessionKey();
        clearStartedConvoyID();
        clearJoinedConvoyID();
        clearFcmToken();
    }

    // ================================================================================
    //      USERNAME FOR CURRENTLY LOGGED IN USER
    // ================================================================================

    public void setLoggedInUser(String username) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.SHARED_PREFS_USERNAME, username);
        editor.apply();
    }

    public String getLoggedInUser() {
        return sharedPrefs.getString(Constants.SHARED_PREFS_USERNAME,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    protected void clearLoggedInUser() {
        setLoggedInUser(Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      SESSION KEY FOR CURRENTLY LOGGED IN USER
    // ================================================================================

    public void setSessionKey(String sessionKey) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.SHARED_PREFS_SESSION_KEY, sessionKey);
        editor.apply();
    }

    public String getSessionKey() {
        return sharedPrefs.getString(Constants.SHARED_PREFS_SESSION_KEY,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    protected void clearSessionKey() {
        setSessionKey(Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      ID FOR CONVOY STARTED BY CURRENTLY LOGGED IN USER
    // ================================================================================

    public void setStartedConvoyID(String convoyID) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.SHARED_PREFS_STARTED_CONVOY_ID, convoyID);
        editor.apply();
    }

    public String getStartedConvoyID() {
        return sharedPrefs.getString(Constants.SHARED_PREFS_STARTED_CONVOY_ID,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    public boolean isStartedConvoyIdSet() {
        return (!getStartedConvoyID().equals(Constants.SHARED_PREFS_DEFAULT_STRING));
    }

    public void clearStartedConvoyID() {
        setStartedConvoyID(Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      ID FOR CONVOY STARTED BY CURRENTLY LOGGED IN USER
    // ================================================================================

    public void setJoinedConvoyID(String convoyID) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.SHARED_PREFS_JOINED_CONVOY_ID, convoyID);
        editor.apply();
    }

    public String getJoinedConvoyID() {
        return sharedPrefs.getString(Constants.SHARED_PREFS_JOINED_CONVOY_ID,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    public boolean isJoinedConvoyIdSet() {
        return (!getJoinedConvoyID().equals(Constants.SHARED_PREFS_DEFAULT_STRING));
    }

    public void clearJoinedConvoyID() {
        setStartedConvoyID(Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      FIREBASE MESSAGING TOKEN FOR CURRENTLY LOGGED IN USER
    // ================================================================================

    public void setFcmToken(String sessionKey) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.SHARED_PREFS_FCM_TOKEN, sessionKey);
        editor.apply();
    }

    public String getFcmToken() {
        return sharedPrefs.getString(Constants.SHARED_PREFS_FCM_TOKEN,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    protected void clearFcmToken() {
        setFcmToken(Constants.SHARED_PREFS_DEFAULT_STRING);
    }

}