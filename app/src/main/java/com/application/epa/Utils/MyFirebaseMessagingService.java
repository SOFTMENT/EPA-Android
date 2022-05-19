package com.application.epa.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;


import com.application.epa.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import com.application.epa.WelcomeActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String Channel_Name = "ch_epa";
    String Channel_Id = "id_epa";
    NotificationChannel notificationChannel;
    int rq = 10;
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        String title = "";
        String body = "";
try {
    title = remoteMessage.getNotification().getTitle();
    body = remoteMessage.getNotification().getBody();
}
catch (Exception e) {

    title = remoteMessage.getData().get("title");
    body = remoteMessage.getData().get("message");

}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            craeteNotificationChannel();

        }


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,Channel_Id)
                .setContentTitle(title).setAutoCancel(true).setContentText(body).setStyle(new NotificationCompat.BigTextStyle().bigText(body));


        mBuilder.setSmallIcon(R.drawable.ic_baseline_message_24);
        mBuilder.setColor(getResources().getColor(R.color.salmon));
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent notificationIntent = new Intent(this, WelcomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(notificationIntent);
        // set intent so it does not start a new activity
       PendingIntent intent =  taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(intent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                notificationManager.createNotificationChannel(notificationChannel);

        }


        notificationManager.notify(new Random().nextInt(), mBuilder.build());



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void craeteNotificationChannel() {
        notificationChannel = new NotificationChannel(Channel_Id,Channel_Name,NotificationManager.IMPORTANCE_DEFAULT);

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
    }


    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("token", s).apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("token", "empty");
    }

}
