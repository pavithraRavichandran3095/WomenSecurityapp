package com.rajezx.wosecalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.rajezx.wosecalert.Activities.MainActivity;

/**
 * Created by py on 4/5/2017.
 */

public class NotificationShow {

    Context context;

    public  NotificationShow(Context context1) {
        context = context1;
    }

    public void showNotification(String from, String notification, Intent intent) {

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                123,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.emer);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification not = mBuilder.setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_stat_name)) // notification icon
                .setContentTitle("title") // title for notification
                .setContentText("notification") // message for notification
                .setColor(Color.BLUE)
                .setAutoCancel(true)
                .setSound(uri)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        not.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, not);
    }
}