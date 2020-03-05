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
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

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
 * Created by danielbenedykt on 10/30/17.
 */

public class ModifyActivity extends AppCompatActivity{

    EditText editTextUsername, editTextPassword1, editTextPassword2;
    TextView textViewToLogin;
    ProgressBar progressbar;
    Button buttonLogin;

    String url;
    String uid;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_modify);


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }


        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
//        mTextViewPressing = (TextView) findViewById(R.id.textViewPressing);
        textViewToLogin = (TextView) findViewById(R.id.textViewToLogin);
        editTextPassword1 = findViewById(R.id.editTextPassword1);
        editTextPassword2 = findViewById(R.id.editTextPassword2);

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
                Intent i = new Intent(ModifyActivity.this,LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });

        ImageButton btnAmISecure = findViewById(R.id.backButton);
        btnAmISecure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ModifyActivity.this,LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });

//        TextView textviewTitle = (TextView) findViewById(R.id.textviewTitle);

        Typeface face = Typeface.createFromAsset(getAssets(),
                "titillium_regular.otf");
        editTextUsername.setTypeface(face);
        editTextUsername.setEnabled(false);

        editTextPassword1.setTypeface(face);
        editTextPassword2.setTypeface(face);
        editTextPassword1.setTransformationMethod(new PasswordTransformationMethod());
        editTextPassword2.setTransformationMethod(new PasswordTransformationMethod());
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
                modifyPassword();

            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if (data != null) {
            String dataString = data.toString();
            String[] arr= dataString.split("/");
            uid= arr[5];
            token = arr[6];
            loadEmail();
        }

    }

    private void modifyPassword() {

        if(editTextPassword1.getText().toString().trim().isEmpty() ||
                editTextPassword2.getText().toString().trim().isEmpty()
                )
        {
            new AlertDialog.Builder(ModifyActivity.this).setTitle("Error")
                    .setMessage("Please set a new password with at least 8 characters.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        if (editTextPassword1.getText().toString().length()<8)
        {
            new AlertDialog.Builder(ModifyActivity.this).setTitle("Error")
                    .setMessage("New password should be of at least 8 characters.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        if(!editTextPassword1.getText().toString().equals(editTextPassword2.getText().toString()))
        {
            new AlertDialog.Builder(ModifyActivity.this).setTitle("Error")
                    .setMessage("New Password and Confirm Password should be same.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        new ModifyPasswordTask().execute();

    }

    private void loadEmail() {
        new GetEmailTask().execute();
    }


    class GetEmailTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);
            buttonLogin.setEnabled(false);

        }

        @Override
        protected String doInBackground(Void... params) {
            return GetEmail(ModifyActivity.this.getApplicationContext(), uid, token);
            //return null;
        }


        @Override
        protected void onPostExecute(String email) {
            //super.onPostExecute(aVoid);
            //start connection

            //Data data = Data.getInstance(MainActivity.this);

            progressbar.setVisibility(View.GONE);
            buttonLogin.setEnabled(true);

            if (email!=null) {

                editTextUsername.setText(email);



            }
            else
            {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ModifyActivity.this);
                builder.setTitle("Error");
                builder.setMessage("The 'Reset Password' link is no longer valid.");
                //builder.setNegativeButton(android.R.string.no,null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(ModifyActivity.this,LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                });
                //builder.setOnCancelListener(this);

                builder.show();
            }
        }


    }

    public String GetEmail(Context context, String uid, String token) {
        try {

            Data data = Data.getInstance(context);

            URL url = new URL(context.getString(R.string.server_url) + "/apiv1/get_user_email/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            HashMap<String, String> params = new HashMap<>();
            params.put("uid", uid );
            params.put("token", token );


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
                Log.e("TAG", "GetEmail: "+status );

                if (status != HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getErrorStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = br.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                    Log.e("TAG", "GetEmail: "+total );
                    return null;
                }
                else {
                    inputStream = urlConnection.getInputStream();

                }


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String json = stringBuilder.toString();
                JSONObject jsonObject = new JSONObject(json);
                String email = jsonObject.getString("email");


                return email;
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




    class ModifyPasswordTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);
            buttonLogin.setEnabled(false);

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return ModifyPassword(ModifyActivity.this.getApplicationContext(),editTextPassword1.getText().toString());
            //return null;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            //super.onPostExecute(aVoid);
            //start connection

            //Data data = Data.getInstance(MainActivity.this);

            progressbar.setVisibility(View.GONE);
            buttonLogin.setEnabled(true);

            if (result) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ModifyActivity.this);
                builder.setTitle("Password");
                builder.setMessage("Password changed successfully.");
                //builder.setNegativeButton(android.R.string.no,null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(ModifyActivity.this,LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                });
                //builder.setOnCancelListener(this);

                builder.show();



            }
            else
            {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ModifyActivity.this);
                builder.setTitle("Error");
                builder.setMessage("An error has occurred. Please try again.");
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



    public boolean ModifyPassword(Context context, String newPassword) {
        try {

            Data data = Data.getInstance(context);

            URL url = new URL(context.getString(R.string.server_url) + "/apiv1/set_password/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            HashMap<String, String> params = new HashMap<>();
            params.put("uid", uid );
            params.put("token", token );
            params.put("password",newPassword);


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
                    inputStream = urlConnection.getErrorStream();
                    return false;
                }
                else {
                    inputStream = urlConnection.getInputStream();
                    return true;
                }


                /*BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String json = stringBuilder.toString();
                JSONObject jsonObject = new JSONObject(json);
                String email = jsonObject.getString("email");

*/

            } finally {
                urlConnection.disconnect();
                if (urlConnection2 != null) {
                    urlConnection2.disconnect();
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }
        return false;
    }
}
