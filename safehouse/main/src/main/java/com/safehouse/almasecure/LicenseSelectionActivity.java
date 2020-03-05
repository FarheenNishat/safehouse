package com.safehouse.almasecure;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.safehouse.almasecure.model.LicenseResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class LicenseSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private String json;
    private LicenseResponse licenseResponse;
    private String selectedLicense;
    private static String androidId = "";
    private String hash = "";
    private ProgressDialog progressDialog;
    private LicenseSelectionTask licenseSelectionTask;
    private Data mData;
    CardView mPersonal;
    CardView mVIP;
    //    BillingProcessor bp;
    MixpanelAPI mixpanel;
    TextView personalLicense, vipLicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_selection);
        getSupportActionBar().hide();

        mData = Data.getInstance(this);

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("Android ID", "" + androidId);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

       mixpanel = MixpanelAPI.getInstance(this, getResources().getString(R.string.MIXPANAL_TOKEN));

        json = Data.getInstance(this).getJsonLicense();
        licenseSelectionTask = new LicenseSelectionTask();
        initViewsItems();

    }

    private void initViewsItems() {
        Typeface face = Typeface.createFromAsset(getAssets(),
                "titillium_regular.otf");
        Typeface boldFace = Typeface.createFromAsset(getAssets(),
                "titillium_bold.otf");

        TextView header = findViewById(R.id.header);
        TextView tvLicenseType = findViewById(R.id.tvLicenseType);
        TextView personalTotal = findViewById(R.id.personal_total_license);
        TextView personalText = findViewById(R.id.personal_text);
        personalLicense = findViewById(R.id.personal_license);
        TextView personalLicenseCount  = findViewById(R.id.personalLicenseCount);
        TextView continueText = findViewById(R.id.continueText);
        TextView tvChoose = findViewById(R.id.tvChoose);
        TextView tvActivateDeviceLabel = findViewById(R.id.tvActivateDeviceLabel);

        TextView tvSelectTypeLabel = findViewById(R.id.tvSelectTypeLabel);
        mPersonal = findViewById(R.id.personal);

        header.setTypeface(boldFace);
        personalTotal.setTypeface(face);
        personalText.setTypeface(boldFace);
        personalLicense.setTypeface(boldFace);
        tvLicenseType.setTypeface(boldFace);
        personalLicenseCount.setTypeface(face);
        tvSelectTypeLabel.setTypeface(face);
        continueText.setTypeface(boldFace);
        tvChoose.setTypeface(face);
        tvActivateDeviceLabel.setTypeface(face);

        TextView vipTotal = findViewById(R.id.vip_total_license);
        TextView vipText = findViewById(R.id.vip_text);
        vipLicense = findViewById(R.id.vip_license);
        TextView personalLicenceAvailableCount = findViewById(R.id.personalLicenceAvailableCount);

        mVIP = findViewById(R.id.vip);

        vipTotal.setTypeface(face);
        vipText.setTypeface(boldFace);
        vipLicense.setTypeface(boldFace);
        personalLicenceAvailableCount.setTypeface(face);

        ImageButton logout = findViewById(R.id.buttonLogout);
        logout.setOnClickListener(this);

        RelativeLayout continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(this);

        Gson gson = new Gson();
        licenseResponse = gson.fromJson(json, LicenseResponse.class);
        try {
            if (licenseResponse != null) {
                LicenseResponse.Data data = licenseResponse.getData();
                if (data != null)
                    for (LicenseResponse.Result result : data.getResults()) {
                        switch (result.getLicenseType()) {
                            case "personal":
                                personalTotal.setText(result.getTotalCount() + " License(s)");
                                if (result.getTotalCount() == (result.getUsedCount())) {
                                    personalLicense.setText("0");
                                } else {
                                    int availPersonalLicense = result.getTotalCount() - result.getUsedCount();
                                    personalLicense.setText(String.valueOf(availPersonalLicense));
                                    mPersonal.setOnClickListener(this);
                                }
                                break;
                            case "vip":
                                vipTotal.setText(result.getTotalCount() + " License(s)");
                                if (result.getTotalCount() == (result.getUsedCount())) {
                                    vipLicense.setText("0");
                                } else {
                                    int availVIPLicense = result.getTotalCount() - result.getUsedCount();
                                    vipLicense.setText(String.valueOf(availVIPLicense));
                                    mVIP.setOnClickListener(this);
                                }
                                break;
                        }
                    }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        Data data = Data.getInstance(this);
        Intent i;
        switch (view.getId()) {
            case R.id.personal:
                JSONObject props = new JSONObject();
                try {
                    props.put("Personal Count", personalLicense.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Personal License Clicked on Choose License Screen", props);
                selectedLicense = "personal";
                mPersonal.setCardBackgroundColor(ContextCompat.getColor(this,R.color.license_selected));
                mVIP.setCardBackgroundColor(ContextCompat.getColor(this,R.color.license_unselected));
                break;
            case R.id.vip:
                JSONObject vipProps = new JSONObject();
                try {
                    vipProps.put("VIP Count", vipLicense.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("VIP License Clicked on Choose License Screen", vipProps);
                selectedLicense = "vip";
                mVIP.setCardBackgroundColor(ContextCompat.getColor(this,R.color.license_selected));
                mPersonal.setCardBackgroundColor(ContextCompat.getColor(this,R.color.license_unselected));
                break;
            case R.id.buttonLogout:
                JSONObject logoutProps = new JSONObject();
                try {
                    logoutProps.put("Name", getString(R.string.logout_btn));
                    logoutProps.put(getString(R.string.analytics_from), getString(R.string.choose_license_screen));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Logout Button Clicked on Choose License Screen", logoutProps);
                progressDialog = new ProgressDialog(LicenseSelectionActivity.this);
                progressDialog.setTitle("Logging out");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new LogoutTask().execute();
                break;
            case R.id.continue_button:
                if (selectedLicense != null) {
                        if(selectedLicense.equalsIgnoreCase("vip")){
                            mixpanel.track("Continue button clicked with VIP license selected");
                        }else {
                            mixpanel.track("Continue button clicked with Personal license selected");
                        }
                    sendSelectedLicense(selectedLicense);
                }
                break;

        }
    }

    private void sendSelectedLicense(String license) {
        if (licenseResponse != null) {
            LicenseResponse.Data data = licenseResponse.getData();
            if (data != null)
                for (LicenseResponse.Result result : data.getResults()) {
                    if (license.equals(result.getLicenseType())) {
                        if (result.getUsedCount() == 0) {
                            hash = result.getAvailableLicenses().get(0).getLicenseHash();
                            new LicenseSelectionTask().execute(hash);
                        } else {
                            boolean unavailable = true;
                            for (LicenseResponse.AvailableLicense availableLicense : result.getAvailableLicenses()) {
                                for (LicenseResponse.UsedLicense usedLicense : result.getUsedLicenses()) {
                                    unavailable = usedLicense.getId() == availableLicense.getId();
                                    if (unavailable) break;
                                }
                                if (unavailable)
                                    continue;
                                hash = availableLicense.getLicenseHash();
                                new LicenseSelectionTask().execute(hash);
                                break;
                            }
                        }
                    }

                }
        }
    }


    class LicenseSelectionTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... id) {
            return licenseSelection(getApplicationContext(), id[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            if ("401".equals(result)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LicenseSelectionActivity.this);
                builder.setTitle("Unauthorized");
                builder.setMessage("Multiple Account Login");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            } else if (result != null) {
                Log.e("SELECTION", "" + result);
                if (selectedLicense != null) {
                    Data data = Data.getInstance(LicenseSelectionActivity.this);
                    data.setSelectedLicense(selectedLicense);
                    data.setLicenseHash(hash);
                    data.save();
                    Intent intent;
                    if (mData.isFirstTime()) {
                        intent = new Intent(LicenseSelectionActivity.this, TutorialActivity.class);
                        JSONObject props = new JSONObject();
                        try {
                            props.put(getString(R.string.analytics_from), getString(R.string.choose_license_screen));
                            props.put(getString(R.string.analytics_to), getString(R.string.tutorial_screen));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mixpanel.track("Tutorial begin", props);
                        JSONObject props1 = new JSONObject();
                        try {
                            props1.put(getString(R.string.analytics_from), getString(R.string.choose_license_screen));
                            props1.put(getString(R.string.analytics_to), getString(R.string.tutorial_screen));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mixpanel.track("First Tutorial Screen Appeared", props1);
                    }else {
                        intent = new Intent(LicenseSelectionActivity.this, MainActivity.class);
                    }
                    startActivity(intent);
                    LicenseSelectionActivity.this.finish();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LicenseSelectionActivity.this);
                builder.setTitle("Error");
                builder.setMessage("An error has occurred. Please ensure that you are connected to the internet.");
                //builder.setNegativeButton(android.R.string.no,null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.show();
            }
        }
    }


    private static String licenseSelection(Context context, String id) {
        Myutil ob=new Myutil();
        int status=ob.getStatus(context.getString(R.string.server_url),ConstantFile.selectlicense);



            Data data = Data.getInstance(context);

            HashMap<String, String> params = new HashMap<>();
            params.put("user", data.getUsername());
            params.put("pass", data.getPassword());
            params.put("device", androidId);
            params.put("license", id);
            ob.setvalue(params);
            Log.e("user", data.getUsername());
            Log.e("pass", data.getPassword());

            try {

                if (status == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ob.getInStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String json = stringBuilder.toString();
                    Log.e("ABCDE", "licenseSelection: " + json);
                    return json;
                } else {
                    Log.e("STATUS", "" + status);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ob.getError()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String json = stringBuilder.toString();
                    Log.e("STATUS", "" + json);
                    return "401";
                }

              }
         catch (Exception e){
             Log.e("ERROR", e.getMessage(), e);
         }

            return null;
    }

    class LogoutTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return LogoutUser(LicenseSelectionActivity.this.getApplicationContext());
        }


        @Override
        protected void onPostExecute(String json) {

            if (progressDialog.isShowing())
                progressDialog.cancel();

            if("Success".equals(json)){
                //mixpanel.track("User Logged out");
                Data data = Data.getInstance(LicenseSelectionActivity.this);
                data.resetData();
                JSONObject props1 = new JSONObject();
                try {
                    props1.put("Email", data.getUsername());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track(getString(R.string.user_logged_out), props1);
                mixpanel.getPeople().clearPushRegistrationId();

                Intent i = new Intent(LicenseSelectionActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LicenseSelectionActivity.this);
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

    public static String LogoutUser(Context context) {

        Myutil ob=new Myutil();
        int status=ob.getStatus(context.getString(R.string.server_url),ConstantFile.logout);
        try {
            Data data = Data.getInstance(context);



            HashMap<String, String> params = new HashMap<>();
            params.put("user", data.getUsername());
            params.put("pass", data.getPassword());
            params.put("app", context.getString(R.string.appcode));
            params.put("device", androidId);
            ob.setvalue(params);
//            params.put("license", data.getLicenseHash());


                if(status ==HttpURLConnection.HTTP_OK)
                {
                    return "Success";
                } else
                    {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ob.getError()));
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

    @Override
    protected void onDestroy() {
//        licenseSelectionTask.cancel(true);
        super.onDestroy();
        if(progressDialog !=null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }
}

