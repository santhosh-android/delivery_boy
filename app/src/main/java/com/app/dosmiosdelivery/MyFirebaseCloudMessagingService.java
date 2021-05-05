package com.app.dosmiosdelivery;


import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseCloudMessagingService extends FirebaseMessagingService {
    private SharedPreferences sharedPreferences;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Log.d("Push Notification Body", remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                Log.d("PushNotifydata", jsonObject.toString());
                NotificationHelper.getInstance(getApplicationContext()).createJobNotification(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("TAG", "onNewToken: " + token);
        sharedPreferences = getSharedPreferences("pheebsDb", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("device_id", token);
        editor.apply();
        sendRegistrationTokenToServer(token);
        super.onNewToken(token);
    }

    private void sendRegistrationTokenToServer(String token) {
    }
}
