package edu.temple.convoy.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.Serializable;

import edu.temple.convoy.Traveler;
import edu.temple.convoy.api.ConvoyAPI;
import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class LocationService extends Service {

    private static final int FOREGROUND_SERVICE_ID = 1;

    private LocationManager locationManager;
    private LocationListener listener;
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = location -> {
            Log.i(Constants.LOG_TAG, "USER HAS MOVED!"
                    + "\n\t NEW LAT: " + location.getLatitude()
                    + "\n\t NEW LONG: " + location.getLongitude());

            // send an "update location" API request to server
            sendLocationUpdate(location.getLatitude(), location.getLongitude());

            // prepare to broadcast the current user's location to any subscribers
            Intent intent = new Intent();
            intent.setAction(Constants.BROADCAST_LOCATION_UPDATE);
            intent.putExtra(Constants.BROADCAST_KEY_CURR_USER_LOC,
                    (Serializable) new Traveler(SharedPrefs.getLoggedInUser(LocationService.this),
                            location.getLatitude(), location.getLongitude()));

            // send the broadcast
            sendBroadcast(intent);
        };

        buildForegroundNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startForeground(FOREGROUND_SERVICE_ID, notification);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000, 10, listener);
            Log.i(Constants.LOG_TAG, "Foreground Service is started and listening for location updates");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(Constants.LOG_TAG, "Location service has been destroyed.");
        locationManager.removeUpdates(listener);
    }

    /**
     * Construct a notification to the user to let them know what we're doing with this service
     */
    private void buildForegroundNotification() {
        String channelID = "edu.temple.convoy.notifications";
        String channelName = "Convoy Notifications";
        NotificationChannel channel = new NotificationChannel(channelID, channelName,
                NotificationManager.IMPORTANCE_DEFAULT);

        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        notification = builder.setOngoing(true)
                .setContentTitle("Convoy App is listening for location updates.")
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
    }

    /**
     * Register the current user's location with the remote server by executing
     * the corresponding API endpoint
     *
     * @param lat
     * @param lon
     */
    private void sendLocationUpdate(double lat, double lon) {
        // check to make sure we have an assigned convoy before we continue with loc updates
        if (!SharedPrefs.isActiveConvoyIdSet(this)) {
            Log.e(Constants.LOG_TAG, "Can't send location updates without an associated convoy!");
            return;
        }

        // result listener to respond to operation success / failure
        ConvoyAPI.ResultListener listener = new ConvoyAPI.ResultListener() {
            @Override
            public void onSuccess(String convoyID) {
                // inform the user that the operation was successful
                Toast.makeText(LocationService.this, "You have updated your location with convoy: "
                        + convoyID, Toast.LENGTH_LONG).show();
                Log.d(Constants.LOG_TAG, "You have updated your location with convoy: "
                        + convoyID);
            }

            @Override
            public void onFailure(String message) {
                // inform the user that the operation has failed
                Log.e(Constants.LOG_TAG, "Attempt to update location has failed with message: "
                        + message);
                Toast.makeText(LocationService.this, "Attempt to update location as failed.  "
                        + "Check LogCat for message.", Toast.LENGTH_LONG).show();
            }
        };

        // submit an "update location" API request
        ConvoyAPI convoyAPI = new ConvoyAPI(this);
        convoyAPI.updateLocation(SharedPrefs.getLoggedInUser(this),
                SharedPrefs.getSessionKey(this), SharedPrefs.getActiveConvoyID(this),
                String.valueOf(lat), String.valueOf(lon), listener);
    }

}