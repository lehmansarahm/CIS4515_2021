package edu.temple.convoy.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import edu.temple.convoy.utils.Constants;

public class LocationService extends Service {

    private LocationManager locationManager;
    private LocationListener listener;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startServiceForOreo();
        } else {
            startForeground(1, new Notification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        requestLocationUpdates();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(listener);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startServiceForOreo() {
        /*
            Borrowed inspiration from:
            https://github.com/thakkarkomal009/Android-Samples/blob/master/GetLocationBackground/app/src/main/java/com/getlocationbackground/service/LocationService.kt
         */

        String channelID = "edu.temple.convoy.notifications";
        String channelName = "Convoy Notifications";
        NotificationChannel channel = new NotificationChannel(channelID, channelName,
                NotificationManager.IMPORTANCE_DEFAULT);

        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        Notification notification = builder.setOngoing(true)
                .setContentTitle("Convoy App is listening for location updates.")
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(2, notification);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = location -> {
            Log.i(Constants.LOG_TAG, "USER HAS MOVED.  NEW LAT: " + location.getLatitude()
                    + " NEW LONG: " + location.getLongitude());
            Intent intent = new Intent();
            intent.setAction(Constants.BROADCAST_LOCATION_UPDATE);
            intent.putExtra(Constants.BROADCAST_KEY_LAT, location.getLatitude());
            intent.putExtra(Constants.BROADCAST_KEY_LON, location.getLongitude());
            sendBroadcast(intent);
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 10, listener);
    }

}