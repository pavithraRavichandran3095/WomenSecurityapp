package com.rajezx.wosecalert.Receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.rajezx.wosecalert.Activities.AlertActivity;
import com.rajezx.wosecalert.Activities.MainActivity;
import com.rajezx.wosecalert.R;

/**
 * Created by py on 4/5/2017.
 */
public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        triggerNotification(context);
    }
    private void triggerNotification(Context context) {

        Intent notificationIntent = new Intent(context, AlertActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_name) // notification icon
                .setContentTitle("Women Security !") // title for notification
                .setContentText("Click on this Notification to make an alert") // message for notification
                .setColor(Color.RED)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
