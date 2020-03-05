package com.safehouse.almasecure;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;

/**
 * Created by danielbenedykt on 3/9/17.
 */

public class OnBootService extends IntentService{


    private boolean mCmfixed = false;

    public OnBootService() {
        super("AlmaSecure");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public OnBootService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(MainActivity.TAG, "OnBootService:Getting configuration");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.d(MainActivity.TAG, "OnBootService:InterruptedException");
            e.printStackTrace();
        }
        Log.d(MainActivity.TAG, "OnBootService:Continue");

        Data data = Data.getInstance(getApplicationContext());
        //check for last connection and get the configuration file
        if (data.isConnectedForReboot() && MainActivity.GetConfigurationParams(null,getApplicationContext()))
        {
            Log.d(MainActivity.TAG, "OnBootService:Got configuration");

            Log.d(MainActivity.TAG, "OnBootService:Add profile");
            ProfileManager.getInstance(getApplicationContext()).addProfile(MainActivity.mSelectedProfile);

            //Data data = Data.getInstance(getApplicationContext());
            //Log.d(MainActivity.TAG, "OnBootService:username:" + data.getUsername());
            //Log.d(MainActivity.TAG, "OnBootService:password:" + data.getPassword());

            //String usernameWithUnderscore = data.getUsername().replace("@","_");
            MainActivity.mSelectedProfile.mUsername = data.getUsername();
            MainActivity.mSelectedProfile.mPassword = data.getPassword();


            Intent intent2 = VpnService.prepare(this);
            // Check if we want to fix /dev/tun
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            boolean usecm9fix = prefs.getBoolean("useCM9Fix", false);
            boolean loadTunModule = prefs.getBoolean("loadTunModule", false);

            if (loadTunModule)
                execeuteSUcmd("insmod /system/lib/modules/tun.ko");

            if (usecm9fix && !mCmfixed) {
                execeuteSUcmd("chown system /dev/tun");
            }


            Log.d(MainActivity.TAG, "OnBootService:Start VPN");
            VPNLaunchHelper.startOpenVpn(MainActivity.mSelectedProfile, getApplicationContext());
            Log.d(MainActivity.TAG, "OnBootService:Finished");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Log.d(MainActivity.TAG, "OnBootService:InterruptedException");
                e.printStackTrace();
            }
            Log.d(MainActivity.TAG, "OnBootService:ReallyFinished");
        }

    }

    private void execeuteSUcmd(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("su", "-c", command);
            Process p = pb.start();
            int ret = p.waitFor();
            if (ret == 0)
                mCmfixed = true;
        } catch (InterruptedException | IOException e) {
            VpnStatus.logException("SU command", e);
        }
    }

/*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        return Service.START_NOT_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    */
}
