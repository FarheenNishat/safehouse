package com.safehouse.almasecure;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import de.blinkt.openvpn.R;
import de.blinkt.openvpn.core.OpenVPNService;
/**
 * Created by danielbenedykt on 12/7/16.
 */

public class MyFirebaseMessagingService  extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    /*
    ==============================================================================================
    START VPN SERVICE CONNECTION
    ==============================================================================================
     */

    protected OpenVPNService mService;

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            //OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
            //mService = binder.getService();
           // mService = (OpenVPNService) OpenVPNService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService =null;
        }

    };









     /*
    ==============================================================================================
    START RECIEVE MESSAGE
    ==============================================================================================
     */


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
//
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            Map<String,String> map =  remoteMessage.getData();
//            if(map.containsKey("type") && "reconnect".equals(map.get("type")))
//            {
//                //RECONNECTAR
//                if (VpnStatus.isVPNActive()) {
//                    mService.getManagement().stopVPN(false);
//
//                    if (MainActivity.GetConfigurationParams(null,getApplicationContext()));
//                    {
//                        ProfileManager.getInstance(this).addProfile(MainActivity.mSelectedProfile);
//
//                        Data data = Data.getInstance(getApplicationContext());
//
//                        //String usernameWithUnderscore = data.getUsername().replace("@","_");
//                        MainActivity.mSelectedProfile.mUsername = data.getUsername();
//                        MainActivity.mSelectedProfile.mPassword = data.getPassword();
//
//                        VPNLaunchHelper.startOpenVpn(MainActivity.mSelectedProfile, getBaseContext());
//                    }
//                }
//
//            }
//        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage);
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]










     /*
    ==============================================================================================
    START SHOW NOTIFICATION
    ==============================================================================================
     */


    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
