package com.app.dosmiosdelivery;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.app.dosmiosdelivery.Activity.NewOrdersActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class NotificationHelper {

    private Context context;
    public static final String FCM_CHANNEL_ID = "ORDERS CHANNEL";
    public static final String FCM_CHANNEL_NAME = "JOB ALERTS";
    public static final String FCM_CHANNEL_DESC = "This channel receives job related notifications";

    public interface NotificationHelperListener {
        void onChatNotificationResponse(boolean refreshList);
    }

    private NotificationHelperListener notificationHelperListener;

    public void setNotificationHelperListener(NotificationHelperListener notificationHelperListener) {
        this.notificationHelperListener = notificationHelperListener;
    }

    private NotificationHelper(Context context) {
        // private constructor
        this.context = context;
    }

    private static NotificationHelper notificationHelper;

    public static NotificationHelper getInstance(Context context) {
        if (notificationHelper == null) {
            notificationHelper = new NotificationHelper(context);
        }
        return notificationHelper;
    }

    public void createJobNotification(JSONObject jsonObject) throws JSONException {
        String title = jsonObject.getString("title");
        String message = jsonObject.getString("body");
        //String notificationType = jsonObject.getString("type");

        createNotificationChannel(FCM_CHANNEL_ID, FCM_CHANNEL_NAME, FCM_CHANNEL_DESC);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent notifyIntent = null;
        notifyIntent = new Intent(context, NewOrdersActivity.class);
        /*if (notificationType.equalsIgnoreCase("new_orders")) {
            notifyIntent = new Intent(context, NewOrdersActivity.class);
        } else if (notificationType.equalsIgnoreCase("pickup_orders")) {
            notifyIntent = new Intent(context, PickupOrderActivity.class);
        } else if (notificationType.equalsIgnoreCase("completed_orders")) {
            notifyIntent = new Intent(context, CompletedOrdersActivity.class);
        } else {
            notifyIntent = new Intent(context, SplashActivity.class);
        }*/
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, FCM_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setTicker("Order")
                .setContentTitle(title)
                .setContentText(message)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(generateUniqueNotificationId(), notificationBuilder.build());
    }

    private void createNotificationChannel(String channelId, String channelName, String channelDesc) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDesc);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private int generateUniqueNotificationId() {
        // this id helps in removing and clearing the notifications, so we need to store it
        Random random = new Random();
        return random.nextInt(1000000);
    }

}
