package com.safehouse.almasecure;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import com.google.firebase.messaging.RemoteMessage;
import com.mixpanel.android.mpmetrics.MixpanelFCMMessagingService;
import com.mixpanel.android.mpmetrics.MixpanelPushNotification;


import java.util.Random;

import de.blinkt.openvpn.R;

public class SafeHouseNotificationService extends MixpanelFCMMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("Notification received:-  " + remoteMessage);
        //showPushNotificationLocally(getApplicationContext(), remoteMessage.toIntent());
        createNotification(getApplicationContext(),remoteMessage.toIntent());
    }

    private void showPushNotificationLocally(Context applicationContext, Intent toIntent) {

        //NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext, "mp");
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
         final PendingIntent contentIntent = PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                       toIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("mp");
        }
        builder.setContentTitle("Bodyguard");
        builder.setContentText(toIntent.getStringExtra("mp_message"));
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setContentIntent(contentIntent);
        //builder.setVisibility(VISIBILITY_PUBLIC)
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// notificationId is a unique int for each notification that you must define
        final int notificationId = new Random().nextInt(Integer.MAX_VALUE);
        notificationManager.notify(notificationId, builder.build());
    }


    public void createNotification(Context applicationContext, Intent toIntent)
    {
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotificationManager;
        /**Creates an explicit intent for an Activity in your app**/
        try {

            toIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(applicationContext,
                    0 /* Request code */, toIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder = new NotificationCompat.Builder(getApplicationContext(),"my_channel");

            if((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)){
                mBuilder.setSmallIcon(R.mipmap.notification_logo);
               // mBuilder.setColor(getResources().getColor(R.color.background));
            }
            else {
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            }
            //mBuilder.setLargeIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle("BodyGuard")
                    .setContentText(toIntent.getStringExtra("mp_message"))
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);

            mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel("mp", "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mBuilder.setChannelId("mp");
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
