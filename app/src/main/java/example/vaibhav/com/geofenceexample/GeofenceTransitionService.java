package example.vaibhav.com.geofenceexample;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionService extends IntentService {

    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    private static final String TAG = GeofenceTransitionService.class.getSimpleName();

    public GeofenceTransitionService() {
        super(TAG);
    }

    // Handle errors
    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve the Geofencing intent
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Handling errors
        if (geofencingEvent.hasError()) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMsg);
            return;
        }

        // Retrieve GeofenceTrasition
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Create a detail message with Geofences received
            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences);
            // Send notification details as a String
            sendNotification(geofenceTransitionDetails);
        }
    }

    // Create a detail message with Geofences received
    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesList.add(geofence.getRequestId());
        }

        String status = null;
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            status = "Entering ";
        else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
            status = "Exiting ";
        return status + TextUtils.join(", ", triggeringGeofencesList);
    }

    // Send a notification
    private void sendNotification(String msg) {
        Log.i(TAG, "sendNotification: " + msg);

        // Intent to start the main Activity
        Intent notificationIntent = MainActivity.makeNotificationIntent(
                getApplicationContext(), msg
        );

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent, notificatioMng));
    }

    // Create a notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent, NotificationManager notificatioMng) {

        String channelId = "80001";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);

            notificatioMng.createNotificationChannel(notificationChannel);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setColor(Color.RED)
                    .setContentTitle(msg)
                    .setContentText("Geofence Notification!")
                    .setContentIntent(notificationPendingIntent)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setChannelId(channelId);
            return notificationBuilder.build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setColor(Color.RED)
                    .setContentTitle(msg)
                    .setContentText("Geofence Notification!")
                    .setContentIntent(notificationPendingIntent)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                    .setAutoCancel(true);
            return notificationBuilder.build();
        }
    }

//    public void shownotificationfororeo(final String title, final String message, Intent intent) {
//        String channelId = "80001";
//        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0 / Request code /, intent, PendingIntent.FLAG_UPDATE_CURRENT);/* Date now = new Date();long uniqueId = now.getTime();//use date to generate an unique id to differentiate the notifications.*/
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel notificationChannel = new NotificationChannel(channelId, "NOTIFICATION_CHANNEL_NAME", importance);
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            assert mNotificationManager != null;
//            mBuilder.setContentTitle(message);
//            mBuilder.setChannelId(channelId);
//            mBuilder.setSmallIcon(R.drawable.mtpapp_logo);
//            mBuilder.setAutoCancel(false).setSound(Settings.System.DEFAULT_NOTIFICATION_URI).setContentIntent(resultPendingIntent).setContentTitle(title);
//            mNotificationManager.createNotificationChannel(notificationChannel);
//        } assert mNotificationManager != null;
//        mNotificationManager.notify(0, mBuilder.build());
//    }
}
