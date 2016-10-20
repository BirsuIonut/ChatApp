package com.example.firewaves.chatapp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by atnm1 on 28/07/16.
 * Logging purposesprefere
 */
public class ChatFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "ChatFMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
    }

    @Override
    public void onMessageSent(String s) {
        Log.d(TAG, "FCM Message Sent: " + s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        Log.d(TAG, "FCM Message Send Error: " + e + "\n" + s);
    }
}
