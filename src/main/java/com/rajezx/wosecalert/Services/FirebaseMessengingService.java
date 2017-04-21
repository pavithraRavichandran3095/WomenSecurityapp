package com.rajezx.wosecalert.Services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rajezx.wosecalert.Activities.FirebaseDataEntry;
import com.rajezx.wosecalert.Activities.MainActivity;
import com.rajezx.wosecalert.NotificationShow;

public class FirebaseMessengingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d("firebaseMessage", "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d("firebaseMessage", "From: " + remoteMessage.getFrom());
        shownotify(remoteMessage.getNotification().getBody(),remoteMessage.getFrom());
    }
    private void shownotify(String body, String from) {

        /*Intent openactivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(openactivity);
        */
        NotificationShow nshow = new NotificationShow(getApplicationContext());
        nshow.showNotification(from,body,new Intent(getApplicationContext(), FirebaseDataEntry.class));
    }
}
