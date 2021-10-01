package edu.temple.convoy.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    public static void clearAllUserSettings(Context ctx) {
        clearLoggedInUser(ctx);
        clearSessionKey(ctx);
        clearActiveConvoyID(ctx);
        clearDidStartActiveConvoy(ctx);
        clearFcmToken(ctx);
    }

    // ================================================================================
    //      USERNAME FOR CURRENTLY LOGGED IN USER
    // ================================================================================

    public static void setLoggedInUser(Context ctx, String username) {
        setSharedPrefsString(ctx, Constants.SHARED_PREFS_USERNAME, username);
    }

    public static String getLoggedInUser(Context ctx) {
        return getSharedPrefsString(ctx, Constants.SHARED_PREFS_USERNAME,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    public static boolean isLoggedInUserSet(Context ctx) {
        return (!getLoggedInUser(ctx).equals(Constants.SHARED_PREFS_DEFAULT_STRING));
    }

    protected static void clearLoggedInUser(Context ctx) {
        setLoggedInUser(ctx, Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      SESSION KEY FOR CURRENTLY LOGGED IN USER
    // ================================================================================

    public static void setSessionKey(Context ctx, String sessionKey) {
        setSharedPrefsString(ctx, Constants.SHARED_PREFS_SESSION_KEY, sessionKey);
    }

    public static String getSessionKey(Context ctx) {
        return getSharedPrefsString(ctx, Constants.SHARED_PREFS_SESSION_KEY,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    public static boolean isSessionKeySet(Context ctx) {
        return (!getSessionKey(ctx).equals(Constants.SHARED_PREFS_DEFAULT_STRING));
    }

    protected static void clearSessionKey(Context ctx) {
        setSessionKey(ctx, Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      ID FOR CONVOY STARTED BY CURRENTLY LOGGED IN USER
    // ================================================================================

    public static void setActiveConvoyID(Context ctx, String convoyID) {
        setSharedPrefsString(ctx, Constants.SHARED_PREFS_ACTIVE_CONVOY_ID, convoyID);
    }

    public static String getActiveConvoyID(Context ctx) {
        return getSharedPrefsString(ctx, Constants.SHARED_PREFS_ACTIVE_CONVOY_ID,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    public static boolean isActiveConvoyIdSet(Context ctx) {
        return (!getActiveConvoyID(ctx).equals(Constants.SHARED_PREFS_DEFAULT_STRING));
    }

    public static void clearActiveConvoyID(Context ctx) {
        setActiveConvoyID(ctx, Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      ID FOR CONVOY STARTED BY CURRENTLY LOGGED IN USER
    // ================================================================================

    public static void setDidStartActiveConvoy(Context ctx, boolean didStart) {
        setSharedPrefsBool(ctx, Constants.SHARED_PREFS_DID_START_ACTIVE_CONVOY, didStart);
    }

    public static boolean getDidStartActiveConvoy(Context ctx) {
        return getSharedPrefsBool(ctx, Constants.SHARED_PREFS_DID_START_ACTIVE_CONVOY,
                Constants.SHARED_PREFS_DEFAULT_BOOL);
    }

    public static void clearDidStartActiveConvoy(Context ctx) {
        setDidStartActiveConvoy(ctx, Constants.SHARED_PREFS_DEFAULT_BOOL);
    }

    // ================================================================================
    //      FIREBASE MESSAGING TOKEN FOR CURRENTLY LOGGED IN USER
    // ================================================================================

    public static void setFcmToken(Context ctx, String sessionKey) {
        setSharedPrefsString(ctx, Constants.SHARED_PREFS_FCM_TOKEN, sessionKey);
    }

    public static String getFcmToken(Context ctx) {
        return getSharedPrefsString(ctx, Constants.SHARED_PREFS_FCM_TOKEN,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    protected static void clearFcmToken(Context ctx) {
        setFcmToken(ctx, Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      PRIVATE UTILITY METHODS
    // ================================================================================

    private static void setSharedPrefsString(Context ctx, String key, String value) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor =
                ctx.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getSharedPrefsString(Context ctx, String key, String defaultValue) {
        SharedPreferences sp = ctx.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    private static void setSharedPrefsBool(Context ctx, String key, boolean value) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor =
                ctx.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static boolean getSharedPrefsBool(Context ctx, String key, boolean defaultValue) {
        SharedPreferences sp = ctx.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

}