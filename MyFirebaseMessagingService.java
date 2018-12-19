package com.chocolateradio.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.chocolateradio.Activities.SplashScreen;
import com.chocolateradio.Custom.Utils;
import com.chocolateradio.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "mytag";
    public static final String NOTIFICATION_CHANNEL_ID = "playerNotification";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Utils.getInstance().d("data" + remoteMessage.getData());
            showNotification(remoteMessage.getData().get("body"));
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
//       Utils.getInstance().d("noti"+remoteMessage.getNotification());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification().getBody());
        }
    }

    private void showNotification(String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Intent resultIntent = new Intent(getApplicationContext(), SplashScreen.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.setFlags(Notification.FLAG_AUTO_CANCEL);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Chocolate Radio");
        mBuilder.setContentText(message);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mBuilder.setLargeIcon(icon);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
        }
        mBuilder.setContentIntent(intent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
//            notificationChannel.setDescription("Dj Notification");
//            notificationChannel.setName("Android");
            notificationChannel.setShowBadge(false);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
//            ShortcutBadger.applyCount(getApplicationContext(), Integer.parseInt(badge));
        }
        assert mNotificationManager != null;

        mNotificationManager.notify(1, mBuilder.build());

    }


//    private void showNotification(String message) {
//        NotificationCompat.Builder mBuilder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String CHANNEL_ID = "my_channel_01";// The id of the channel.
//            CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
//            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
////            mChannel.setShowBadge(false);
//
//            mBuilder = new NotificationCompat.Builder(this);
//            mBuilder.setChannelId(CHANNEL_ID);
////            mBuilder.setNumber(0);
//
//            android.app.NotificationManager mNotificationManager =
//                    (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//            if (mNotificationManager != null) {
//                mNotificationManager.createNotificationChannel(mChannel);
//            }
//        }else{
//            mBuilder = new NotificationCompat.Builder(this);
//        }
//        Intent resultIntent = new Intent(getApplicationContext(), SplashScreen.class);
//        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        resultIntent.setFlags(Notification.FLAG_AUTO_CANCEL);
//        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);
//        mBuilder.setSmallIcon(R.drawable.appicon);
//        mBuilder.setContentTitle("ChocolateRadio");
//        mBuilder.setContentText(message);
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.appicon);
//        mBuilder.setLargeIcon(icon);
//        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
//        mBuilder.setAutoCancel(true);
//        mBuilder.setPriority(Notification.PRIORITY_HIGH);
//        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
//        mBuilder.setDefaults(Notification.DEFAULT_ALL);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
//        }
//        mBuilder.setContentIntent(intent);
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(1, mBuilder.build());
//    }
}
