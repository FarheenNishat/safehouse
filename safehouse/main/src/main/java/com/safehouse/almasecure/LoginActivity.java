package com.safehouse.almasecure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;

import de.blinkt.openvpn.BuildConfig;
import de.blinkt.openvpn.R;

/**
 * Created by danielbenedykt on 11/7/16.
 */

public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    TextView mTextViewPressing, safehouseLabel, fasterPrivateLabel;
    ProgressBar progressbar;
    Button buttonLogin;
    static String androidId = "";
    private Data mData;
    //ScrollView scrollview;
    MixpanelAPI mixpanel;
    String fireBaseDesignToken = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mixpanel =
                MixpanelAPI.getInstance(getApplicationContext(), getResources().getString(R.string.MIXPANAL_TOKEN));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);


        mData = Data.getInstance(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
            actionBar.setTitle("Login");
        }
        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.VISIBLE);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            progressbar.setVisibility(View.GONE);
                            fireBaseDesignToken = task.getResult().getToken();
                        }
                    }
                });

        Typeface face = Typeface.createFromAsset(getAssets(),
                "titillium_regular.otf");
        Typeface boldFace = Typeface.createFromAsset(getAssets(),
                "titillium_bold.otf");
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        mTextViewPressing = findViewById(R.id.textViewPressing);
        safehouseLabel = findViewById(R.id.safehouse);
        fasterPrivateLabel = findViewById(R.id.fasterPrivateLabel);

        fasterPrivateLabel.setTypeface(face);

        editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
        buttonLogin = findViewById(R.id.buttonLogin);

        mTextViewPressing.setClickable(true);
        mTextViewPressing.setTypeface(face);
        mTextViewPressing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mixpanel.track("Terms and Conditions Button Clicked on Login Screen");
                Intent intent = new Intent(LoginActivity.this, TermsAndConditions.class);
                startActivity(intent);
            }
        });

        TextView mTextViewTerms = findViewById(R.id.textViewPrivacy);
        mTextViewTerms.setTypeface(face);
        mTextViewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mixpanel.track("Privacy Policy Button Clicked on Login Screen");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.safehousetech.com/legal/consumer-privacy"));
                startActivity(browserIntent);
            }
        });
        TextView textviewTitle = findViewById(R.id.textviewTitle);
        TextView textViewForgotPassword = findViewById(R.id.textViewForgotPassword);


        editTextUsername.setTypeface(face);
        safehouseLabel.setTypeface(boldFace);
        Data data = Data.getInstance(LoginActivity.this);
        editTextUsername.setText(data.getUsername());
        editTextPassword.setTypeface(face);
        mTextViewPressing.setTypeface(face);
        textviewTitle.setTypeface(boldFace);
        textViewForgotPassword.setTypeface(face);



        buttonLogin.setTypeface(boldFace);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextUsername.getText().toString().trim().isEmpty() || editTextPassword.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(LoginActivity.this).setTitle("Error")
                            .setMessage("Please enter a valid email and a password to continue.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                mixpanel.track("Login Button Clicked");
                new AuthenticateTask().execute();

            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mixpanel.track("Forgot Your Password Button Clicked on Login Screen");
                Intent i = new Intent(LoginActivity.this, ResetActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    class AuthenticateTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);
            buttonLogin.setEnabled(false);

        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... params) {
            return loginUser(LoginActivity.this.getApplicationContext(),
                    editTextUsername.getText().toString().trim(),
                    editTextPassword.getText().toString());
        }

        @Override
        protected void onPostExecute(String json) {
            progressbar.setVisibility(View.GONE);
            buttonLogin.setEnabled(true);

            if ("401".equals(json)) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
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


    class LicenseTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);
            buttonLogin.setEnabled(false);

        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... voids) {
            return AuthenticateAndGetLicense(getApplicationContext(),
                    editTextUsername.getText().toString(),
                    editTextPassword.getText().toString());
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            progressbar.setVisibility(View.GONE);
            buttonLogin.setEnabled(true);

            Data data = Data.getInstance(LoginActivity.this);
            data.setUsername(editTextUsername.getText().toString().trim());
            data.setPassword(editTextPassword.getText().toString());
            data.setJsonLicense(result);
            data.save();
            mixpanel.identify(editTextUsername.getText().toString().trim());
            mixpanel.getPeople().identify(editTextUsername.getText().toString().trim());
            mixpanel.getPeople().set("$name", editTextUsername.getText().toString().trim());
            mixpanel.getPeople().set("$email", editTextUsername.getText().toString().trim());
            mixpanel.getPeople().set("$test", editTextUsername.getText().toString().trim());
            Log.d("registration token:- ",fireBaseDesignToken);
       //     if(!TextUtils.isEmpty(mixpanel.getPeople().getPushRegistrationId()))
                Log.d("registration  token after clear:- ", fireBaseDesignToken);
                mixpanel.getPeople().setPushRegistrationId(fireBaseDesignToken);
            //mixpanel.getPeople().set("$created", new Date());
            mixpanel.getPeople().set("$last_login", new Date());
            try {
                Config config = new Config(LoginActivity.this);
                config.setVersionCode(BuildConfig.VERSION_CODE);
            }catch (Exception e){
                Log.d("Safehouse", "version code not set");
            }
            Intent intent = new Intent(LoginActivity.this, LicenseSelectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            JSONObject props1 = new JSONObject();
            try {
                props1.put("Email", editTextUsername.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mixpanel.track(getString(R.string.user_logged_in), props1);
         //   mixpanel.flush();
            finish();
        }
    }


    public static String AuthenticateAndGetLicense(Context context, String user, String password) {
        Myutil ob=new Myutil();
       int status= ob.getStatus(context.getString(R.string.server_url),ConstantFile.getLicense);
        HashMap<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("pass", password);
        params.put("device", androidId);
        ob.setvalue(params);


        Log.e("Status", "" + status);
                try {
                    if (status != HttpURLConnection.HTTP_OK) {
                        return "401";
                    } else {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ob.getInStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        Log.e("ABCD", "AuthenticateAndGetLicense: " + stringBuilder.toString());
                        return stringBuilder.toString();
                    }
                }
                catch (Exception e)
                {
                    Log.e("ERROR", e.getMessage(), e);
                }
        return null;

        }




    public static String loginUser(Context context, String user, String password) {

        Myutil ob=new Myutil();
        int status= ob.getStatus(context.getString(R.string.server_url), ConstantFile.login);




            HashMap<String, String> params = new HashMap<>();
            params.put("user", user);
            params.put("pass", password);
            params.put("app", context.getString(R.string.appcode));
            params.put("device", androidId);
            ob.setvalue(params);


           try {
               if (status != HttpURLConnection.HTTP_OK) {

                   BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ob.getError()));
                   StringBuilder stringBuilder = new StringBuilder();
                   String line;
                   while ((line = bufferedReader.readLine()) != null) {
                       stringBuilder.append(line).append("\n");
                   }
                   bufferedReader.close();
                   String json = stringBuilder.toString();
                   Log.e("ABCDEF", "AuthenticateAndGetRegions: " + json);
                   return "401";
               } else {

                   BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ob.getInStream()));
                   StringBuilder stringBuilder = new StringBuilder();
                   String line;
                   while ((line = bufferedReader.readLine()) != null) {
                       stringBuilder.append(line).append("\n");
                   }
                   bufferedReader.close();
                   String json = stringBuilder.toString();
                   return json;
               }
           }
           catch (Exception e) {
               Log.e("ERROR", e.getMessage(), e);
           }
        return null;

       }
}





