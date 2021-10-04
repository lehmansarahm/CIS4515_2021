package edu.temple.convoy.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import edu.temple.convoy.LoginActivity;
import edu.temple.convoy.api.AccountAPI;
import edu.temple.convoy.api.BaseAPI;
import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class FcmService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i(Constants.LOG_TAG, "Received new FCM message: "
                + remoteMessage.getData().get("payload"));
        // TODO - parse out list of user locations
        // TODO - ignore my location, since I already know it
        // TODO - send out broadcast with filtered list of locations
    }

    /**
     *
     * @param token
     */
    @Override
    public void onNewToken(String token) {
        Log.i(Constants.LOG_TAG, "Attempting to register new FCM token: "
                + token + " with server.");
        registerToken(this, token);
    }

    /**
     *
     * @param ctx
     */
    public static void verifyCurrentToken(Context ctx) {
        Log.i(Constants.LOG_TAG, "Current FCM token: " + SharedPrefs.getFcmToken(ctx));
        if (!SharedPrefs.isFcmTokenSet(ctx)) {
            Log.i(Constants.LOG_TAG, "FCM token not previously set ... "
                    + "Getting current token and registering it with the server.");
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                Log.i(Constants.LOG_TAG, "Current FCM token: " + token);
                FcmService.registerToken(ctx, token);
            });
        }
    }

    /**
     *
     * @param ctx
     * @param token
     */
    private static void registerToken(Context ctx, String token) {
        BaseAPI.ResultListener listener = new BaseAPI.ResultListener() {
            @Override
            public void onSuccess(String sessionKey) {
                Log.i(Constants.LOG_TAG, "Attempt to register new FCM token with server "
                        + "was SUCCESSFUL.");
                SharedPrefs.setFcmToken(ctx, token);
            }

            @Override
            public void onFailure(String message) {
                Log.i(Constants.LOG_TAG, "Attempt to register new FCM token with server "
                        + "FAILED with error message: " + message);
            }
        };

        AccountAPI accountAPI = new AccountAPI(ctx);
        accountAPI.update(SharedPrefs.getLoggedInUser(ctx),
                SharedPrefs.getSessionKey(ctx), token, listener);
    }

    /*
    public static void subscribeToTopic(Context context, String topicLabel) {
        Log.d(Constants.LOG_TAG, "Attempting to subscribe to topic: " + topicLabel);
        FirebaseMessaging.getInstance().subscribeToTopic(topicLabel)
                .addOnCompleteListener(task -> {
                    String message = task.isSuccessful()
                            ? "Successfully subscribed to FCM topic for convoy: "
                            : "Failed to subscribe to FCM topic for convoy: ";
                    message += topicLabel;
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    Log.d(Constants.LOG_TAG, message);
                });
    }

    public static void unsubscribeFromTopic(Context context, String topicLabel) {
        Log.d(Constants.LOG_TAG, "Attempting to subscribe to topic: " + topicLabel);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicLabel)
                .addOnCompleteListener(task -> {
                    String message = task.isSuccessful()
                            ? "Successfully UN-subscribed from FCM topic for convoy: "
                            : "Failed to unsubscribe from FCM topic for convoy: ";
                    message += topicLabel;
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    Log.d(Constants.LOG_TAG, message);
                });
    }
    */

}