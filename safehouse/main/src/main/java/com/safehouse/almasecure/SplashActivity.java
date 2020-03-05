package com.safehouse.almasecure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import com.google.firebase.FirebaseApp;
import com.mixpanel.android.mpmetrics.MixpanelAPI;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import de.blinkt.openvpn.BuildConfig;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.core.VpnStatus;

/**
 * Created by danielbenedykt on 11/7/16.
 */

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    int versionCode;
    public static String TAG = "SAFEHOUSE";
    private String username = "";
    private String password = "";
    private String androidId = "";
    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        versionCode = BuildConfig.VERSION_CODE;
        mixpanel =
                MixpanelAPI.getInstance(getApplicationContext(), getResources().getString(R.string.MIXPANAL_TOKEN));
        final LottieAnimationView lottieAnimationView = findViewById(R.id.animationView);
        lottieAnimationView.setRenderMode(RenderMode.HARDWARE);
        lottieAnimationView.setAnimation("splash.zip");
        lottieAnimationView.playAnimation();
        checkForAppUpdate();
        //FirebaseApp.initializeApp(getApplicationContext());

    }

    private void checkForAppUpdate() {
        Data data = Data.getInstance(SplashActivity.this);
        Config config = new Config(SplashActivity.this);
        System.out.println("config.getVersionCode() "+ config.getVersionCode());
        if (data.getPassword() == null) { // user is logout
            System.out.println("mData.getPassword():- "+ data.getPassword());
            startWithScreens();
        } else if (config.getVersionCode() < versionCode) { // app update when user is logged in
            // auto logout and auto login
            System.out.println("mData.getVersionCode():- "+ config.getVersionCode());
            System.out.println("versionCode:- "+ versionCode);
            username = data.getUsername();
            password = data.getPassword();
            new LogoutTask().execute();
        }else {
            startWithScreens();
        }
    }

    private void startWithScreens() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Data data = Data.getInstance(SplashActivity.this);
                if (data.getPassword() != null) { // user is logged in
                    if (data.getSelectedLicense() != null) {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);

                        if (data.isFirstTime()) {
                            i = new Intent(SplashActivity.this, TutorialActivity.class);
                            JSONObject props = new JSONObject();
                            try {
                                props.put(getString(R.string.analytics_from), getString(R.string.splash_screen));
                                props.put(getString(R.string.analytics_to), getString(R.string.tutorial_screen));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mixpanel.track("Tutorial begin", props);
                        }

                        startActivity(i);

                    } else {
                        Intent i = new Intent(SplashActivity.this, LicenseSelectionActivity.class);
                        JSONObject props = new JSONObject();
                        try {
                            props.put(getString(R.string.analytics_from), getString(R.string.splash_screen));
                            props.put(getString(R.string.analytics_to), getString(R.string.choose_license_screen));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mixpanel.track("Choose License Screen Appeared", props);
                        startActivity(i);
                    }
                    finish();
                } else {
                    //to register screen
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    JSONObject props = new JSONObject();
                    try {
                        props.put(getString(R.string.analytics_from), getString(R.string.splash_screen));
                        props.put(getString(R.string.analytics_to), getString(R.string.login_screen));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Login Screen Appeared", props);
                    startActivity(i);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    class LogoutTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return LogoutUser(SplashActivity.this.getApplicationContext());
        }

        @Override
        protected void onPostExecute(String json) {
            if ("Success".equals(json)) {
                Data data = Data.getInstance(SplashActivity.this);
                data.resetData();
                if (VpnStatus.isVPNActive()) {
                    try {
                       // mService.stopVPN(false);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    new DeleteFromServer().execute();
                }else {
                    onSuccessfulLogout();
                }
              /*  Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();*/
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Error");
                builder.setMessage(json);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
        }


    }

    private void onSuccessfulLogout() {
        // create autologin
        new AuthenticateTask().execute();
    }


    class AuthenticateTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... params) {
            return loginUser(SplashActivity.this.getApplicationContext(),
                   username,
                   password);
        }

        @Override
        protected void onPostExecute(String json) {
            if ("401".equals(json)) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Wrong username or password. Please try again.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.show();
            } else if (json != null) {
//                try {
//                    JSONObject obj = new JSONObject(json);
//                    if (obj.getBoolean("is_tutorial_viewed")
//                        mData.setFirstTime(false);
//                        mData.save();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                new LicenseTask().execute();
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Error");
                builder.setMessage("An error has occurred. Please ensure that you are connected to the internet.");
                //builder.setNegativeButton(android.R.string.no,null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                //builder.setOnCancelListener(this);

                builder.show();
            }
        }
    }


    public String loginUser(Context context, String user, String password) {
        try {

            URL url = new URL(context.getString(R.string.server_url) + "/apiv1/login/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            HashMap<String, String> params = new HashMap<>();
            params.put("user", user);
            params.put("pass", password);
            params.put("app", context.getString(R.string.appcode));
            params.put("device", androidId);

            HttpURLConnection urlConnection2 = null;
            try {
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(Util.getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();

                InputStream inputStream;

                int status = urlConnection.getResponseCode();

                if (status != HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getErrorStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String json = stringBuilder.toString();
                    return "401";
                } else {
                    inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String json = stringBuilder.toString();
                    return json;
                }


               /*



                return true;*/
            } finally {
                urlConnection.disconnect();
                if (urlConnection2 != null) {
                    urlConnection2.disconnect();
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }
        return null;
    }

    class LicenseTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... voids) {
            return AuthenticateAndGetLicense(getApplicationContext(),
                   username,
                    password);
        }

        @Override
        protected void onPostExecute(String result) {
            Data data = Data.getInstance(SplashActivity.this);
            data.setUsername(username);
            data.setPassword(password);
            data.setJsonLicense(result);
            data.setVersionCode(versionCode);
            data.save();

            Config config = new Config(SplashActivity.this);
            config.setVersionCode(versionCode);
            System.out.println("mdata.getVersionCode():- "+config.getVersionCode());
            Intent intent = new Intent(SplashActivity.this, LicenseSelectionActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public String AuthenticateAndGetLicense(Context context, String user, String password) {
        try {
            URL url = new URL(context.getString(R.string.server_url) + "/apiv1/get-license/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            HashMap<String, String> params = new HashMap<>();
            params.put("user", user);
            params.put("pass", password);
            params.put("device", androidId);

            try {
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(Util.getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();

                int status = urlConnection.getResponseCode();
                Log.e("Status", "" + status);
                if (status != HttpURLConnection.HTTP_OK) {
                    return "401";
                } else {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    Log.e("ABCD", "AuthenticateAndGetLicense: " + stringBuilder.toString());
                    return stringBuilder.toString();
                }

            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }
        return null;
    }

    public String LogoutUser(Context context) {
        try {
            Data data = Data.getInstance(context);


            URL url = new URL(context.getString(R.string.server_url) + "/apiv1/logout/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            HashMap<String, String> params = new HashMap<>();
            params.put("user", data.getUsername());
            params.put("pass", data.getPassword());
            params.put("app", context.getString(R.string.appcode));
            params.put("device", androidId);
            params.put("license", data.getLicenseHash());

            HttpURLConnection urlConnection2 = null;
            try {
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(Util.getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();

                InputStream inputStream;

                int status = urlConnection.getResponseCode();

                if (status == HttpURLConnection.HTTP_OK) {
                    return "Success";
                } else {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String json = stringBuilder.toString();
                    return json;
                }

//                if (status != HttpURLConnection.HTTP_OK) {
//                    inputStream = urlConnection.getErrorStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
//                    StringBuilder stringBuilder = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        stringBuilder.append(line).append("\n");
//                    }
//                    bufferedReader.close();
//                    String json = stringBuilder.toString();
//                    Log.e("ABCDEF", "AuthenticateAndGetRegions: " + json);
//                    return "401";
//                } else {
//                    inputStream = urlConnection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                    StringBuilder stringBuilder = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        stringBuilder.append(line).append("\n");
//                    }
//                    bufferedReader.close();
//                    String json = stringBuilder.toString();
//                    return json;
//                }

            } finally {
                urlConnection.disconnect();
                if (urlConnection2 != null) {
                    urlConnection2.disconnect();
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }
        return null;
    }

    class DeleteFromServer extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.e(TAG, "Executiing - Delete From Server");
            try {

                URL url = new URL(getApplicationContext().getString(R.string.server_url) + "/apiv1/connected_users/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                urlConnection.setRequestProperty("Accept", "*/*");
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", Data.getInstance(SplashActivity.this).getUsername());
                jsonObject.put("pass", Data.getInstance(SplashActivity.this).getPassword());
                jsonObject.put("user_email", Data.getInstance(SplashActivity.this).getUsername());
                jsonObject.put("serverIP", Data.getInstance(SplashActivity.this).getServerIp());
                jsonObject.put("osType", "android");

                try {
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = urlConnection.getResponseCode();
                    Log.e(TAG, "doInBackground: " + responseCode);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            onSuccessfulLogout();
        }
    }

}
