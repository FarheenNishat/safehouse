package com.safehouse.almasecure;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonParser;
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
import java.util.HashMap;

import de.blinkt.openvpn.R;

/**
 * Created by danielbenedykt on 10/28/17.
 */

public class ResetActivity extends AppCompatActivity {

    EditText editTextUsername;
    TextView mTextViewPressing, textViewToLogin, titleText, enterYourEmailLabel, weWillEmailLabel, emailLabel;
    ProgressBar progressbar;
    Button buttonLogin;
    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_reset);


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Typeface face = Typeface.createFromAsset(getAssets(),
                "titillium_regular.otf");
        Typeface boldFace = Typeface.createFromAsset(getAssets(),
                "titillium_bold.otf");

        mixpanel =
                MixpanelAPI.getInstance(getApplicationContext(), getResources().getString(R.string.MIXPANAL_TOKEN));
        JSONObject props = new JSONObject();
        try {
            props.put(getString(R.string.analytics_from), getString(R.string.login_screen));
            props.put(getString(R.string.analytics_to) , getString(R.string.forgot_password_screen));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track("Forgot Password Screen Appeared", props);

        ImageButton btnAmISecure = findViewById(R.id.backButton);
        btnAmISecure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        mTextViewPressing = (TextView) findViewById(R.id.textViewPressing);
        textViewToLogin = (TextView) findViewById(R.id.textViewToLogin);
        enterYourEmailLabel = findViewById(R.id.title);
        titleText = (TextView) findViewById(R.id.titleText);
        emailLabel = findViewById(R.id.emailLabel);
        emailLabel.setTypeface(boldFace);
        weWillEmailLabel = findViewById(R.id.weWillEmail);
        weWillEmailLabel.setTypeface(face);
        titleText.setTypeface(boldFace);
        enterYourEmailLabel.setTypeface(face);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);

//        mTextViewPressing.setClickable(true);
//        mTextViewPressing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = getString(R.string.tos_url);
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//
//            }
//        });

        textViewToLogin.setClickable(true);
        textViewToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResetActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                JSONObject props = new JSONObject();
                try {
                    props.put(getString(R.string.analytics_from), getString(R.string.forgot_password_screen));
                    props.put(getString(R.string.analytics_to), getString(R.string.login_screen));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Login Screen Appeared", props);
                startActivity(i);
                finish();

            }
        });


//        TextView textviewTitle = (TextView) findViewById(R.id.textviewTitle);

        editTextUsername.setTypeface(face);
        Data data = Data.getInstance(ResetActivity.this);
        editTextUsername.setText(data.getUsername());
//        mTextViewPressing.setTypeface(face);
//        textviewTitle.setTypeface(face);
        textViewToLogin.setTypeface(face);

        progressbar = findViewById(R.id.progressbar);

        Typeface facebold = Typeface.createFromAsset(getAssets(),
                "titillium_bold.otf");
        buttonLogin.setTypeface(facebold);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextUsername.getText().toString().trim().isEmpty()) {
                    new AlertDialog.Builder(ResetActivity.this).setTitle("Input")
                            .setMessage("Please enter your email to continue.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }


                new ResetActivity.ResetTask().execute();
            }
        });
    }


    class ResetTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);
            buttonLogin.setEnabled(false);

        }

        @Override
        protected String doInBackground(Void... params) {
            return ResetPassword(ResetActivity.this.getApplicationContext());
            //return null;
        }


        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(aVoid);
            //start connection

            //Data data = Data.getInstance(MainActivity.this);

            progressbar.setVisibility(View.GONE);
            buttonLogin.setEnabled(true);


            try {
                JSONObject data = new JSONObject(result);
                if (data.getInt("status") == 400) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ResetActivity.this);
                    builder.setTitle("Account not found");
                    builder.setMessage("We cannot find an account with that email address");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                } else if (data.getInt("status") == 200) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ResetActivity.this);
                    builder.setTitle("Reset Password");
                    builder.setMessage("We have sent you an email to reset your password. Please check your inbox.");
                    //builder.setNegativeButton(android.R.string.no,null);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Intent i = new Intent(ResetActivity.this, LoginActivity.class);
                            //startActivity(i);
                            finish();
                        }
                    });
                    //builder.setOnCancelListener(this);
                    builder.show();
                } else {
                    errorMessage();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                errorMessage();
            }
        }
    }

    public void errorMessage(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ResetActivity.this);
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

    public String ResetPassword(Context context) {
        try {

            Data data = Data.getInstance(context);

            URL url = new URL(context.getString(R.string.server_url) + "/apiv1/reset_password/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            HashMap<String, String> params = new HashMap<>();
            params.put("email", editTextUsername.getText().toString());


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

                if (status != HttpURLConnection.HTTP_OK) {
                    return "Error";
                } else {
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

            } finally {
                urlConnection.disconnect();
                if (urlConnection2 != null) {
                    urlConnection2.disconnect();
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }

        return "Error";
    }
}
