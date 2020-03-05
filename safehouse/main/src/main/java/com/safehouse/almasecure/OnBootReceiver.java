package com.safehouse.almasecure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;

/**
 * Created by danielbenedykt on 3/9/17.
 */

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MainActivity.TAG, "OnBootReceiver");
        context.getApplicationContext().startService(new Intent(context, OnBootService.class));
        Log.d(MainActivity.TAG, "OnBootReceiver:finished");
        /*Log.d(MainActivity.TAG, "OnBootReceiver:Getting configuration");
        if (MainActivity.GetConfigurationParams(null,context.getApplicationContext()));
        {
            Log.d(MainActivity.TAG, "OnBootReceiver:Got configuration");

            Log.d(MainActivity.TAG, "OnBootReceiver:Add profile");
            ProfileManager.getInstance(context).addProfile(MainActivity.mSelectedProfile);


            Data data = Data.getInstance(context.getApplicationContext());

            String usernameWithUnderscore = data.getUsername().replace("@","_");
            MainActivity.mSelectedProfile.mUsername = usernameWithUnderscore;
            MainActivity.mSelectedProfile.mTransientPW = data.getPassword();

            Log.d(MainActivity.TAG, "OnBootReceiver:Start VPN");
            VPNLaunchHelper.startOpenVpn(MainActivity.mSelectedProfile, context);
            Log.d(MainActivity.TAG, "OnBootReceiver:Finished");
        }
        */
    }
}
