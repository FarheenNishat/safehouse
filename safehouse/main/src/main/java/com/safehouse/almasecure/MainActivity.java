package com.safehouse.almasecure;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.blinkt.openvpn.R;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ConnectionStatus;
import de.blinkt.openvpn.core.IOpenVPNServiceInternal;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;

public class MainActivity extends AppCompatActivity implements VpnStatus.StateListener, VpnStatus.ByteCountListener {

    public static String TAG = "SAFEHOUSE";
    private boolean mCmfixed = false;
    private static final int START_VPN_PROFILE = 70;
    private TextView textTitle;
    private TextView titleSecure;
    private TextView subtitleSecure;
    private TextView securetitle;
    private TextView tvBodyguardLabel;
    private View tutorialView;
    private ImageView mTutorialImage;
    private LinearLayout mLinearLayoutInfo;
    private DrawerLayout mDrawer;
    private FrameLayout mFrame;
    private LinearLayout secureLayout;
    private static int VPNConnectionRetryCount = 0;

    private static MainActivity mainActivityContext;
    Collection<VpnProfile> mAllvpn;
    static VpnProfile mSelectedProfile;
    private boolean mhideLog = false;
    private static String mAliasName = null;
    private static String mEmbeddedPwFile;
    //private static Map<Utils.FileType, FileSelectLayout> fileSelectMap = new HashMap<>();
    private static transient List<String> mPathsegments;
    private ImageView mAppSelect, countryFlag;
    protected IOpenVPNServiceInternal mService;
    String region = "";
    String server = "";
    String userIp = "";
    static String serverIp = "";
    private static String androidId = "";

    private ProgressBar mProgressbar, mProgressbarLocation;
    private ImageButton mButtonLocation;
    private ProgressDialog progressDialog;
    OptionsPickerView pvOptions;

    Button disconnectBtn, buttonLocationTitle;

    JSONArray arrRegions;
    String bestRegion;
    boolean showRegionsAtEnd, getRegionSuccessfully;

    long onPauseTime;
    private GetRegionsTask mGetRegions;
    private Data mData;

    private LottieAnimationView animationView;
    private LottieAnimationView animationLineView;
    MixpanelAPI mixpanel;

    public static boolean GetConfigurationParams(final MainActivity mainActivity, Context context) {
        try {
            Data data = Data.getInstance(context);
            URL url = new URL(context.getString(R.string.server_url) + "/apiv1/register_app/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            HashMap<String, String> params = new HashMap<>();
            params.put("client_os", "android");
            params.put("id", androidId);
            params.put("push_id", data.getPushIDToken());
            params.put("user", data.getUsername());
            params.put("pass", data.getPassword());
            params.put("app", context.getString(R.string.appcode));
            params.put("license", data.getLicenseHash());
            params.put("device", androidId);

            if (mainActivity != null) {
                if (data.getSelectedRegion() != "") {
                    boolean contains = false;

                    try {
                        for (int i = 0; i < mainActivity.arrRegions.length(); i++) {
                            JSONObject jsonRegion = mainActivity.arrRegions.getJSONObject(i);
                            String short_name = jsonRegion.getString("short_name");
                            if (short_name == data.getSelectedRegion()) {
                                contains = true;
                                break;
                            }
                        }
                        if (contains) {
                            params.put("region", data.getSelectedRegion());
                        }
                    } catch (Exception exe) {
                        exe.printStackTrace();
                    }
                } else if (mainActivity.bestRegion != "" && mainActivity.bestRegion != null) {
                    params.put("region", mainActivity.bestRegion);
                }
            }
            System.out.println("calling /apiv1/register_app/");
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
                System.out.println("got result for  /apiv1/register_app/");
                if (status != HttpURLConnection.HTTP_OK)
                    inputStream = urlConnection.getErrorStream();
                else
                    inputStream = urlConnection.getInputStream();


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String json = stringBuilder.toString();
                Log.e("Config JSON File", json);

                String configFile = null;
                JSONObject jsonObject = new JSONObject(json);
                configFile = jsonObject.getString("config_file");
                serverIp = jsonObject.getString("server");
                data.setServerIp(serverIp);
                data.save();
                if (mainActivity != null) {
                    mainActivity.region = jsonObject.getString("region");
                    mainActivity.server = jsonObject.getString("server");
                } else {
                    //on service
                }
                data.setRegion(jsonObject.getString("region"));
                data.save();
                System.out.println("calling https://vpn-app.safehouse-technologies.com/static/configFile");
                URL url2 = new URL("https://vpn-app.safehouse-technologies.com" + "/static/" + configFile);
                urlConnection2 = (HttpURLConnection) url2.openConnection();


                InputStream is = urlConnection2.getInputStream();
               // Log.d(TAG, "Parsing starts");
                System.out.println("Parsing starts");
                doImport(is);
                return true;
            } finally {
                urlConnection.disconnect();
                if (urlConnection2 != null) {
                    urlConnection2.disconnect();
                }
            }
        } catch (Exception e) {
            Log.e("QWERTY", e.getMessage(), e);
            if (mainActivity != null) {
                mainActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        mainActivity.mButtonLocation.setVisibility(View.VISIBLE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                        builder.setTitle("Error");
                        builder.setMessage("Not able to connect to the VPN server. Please ensure that you are connected to the internet.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mainActivity.setDisconnectView();
                            }
                        });
                        builder.show();
                    }
                });
            }
            return false;
        }
    }

    public void getRegions() {
        buttonLocationTitle.setText("Fetching Servers...");
        System.out.println("getRegions");
        mButtonLocation.setVisibility(View.INVISIBLE);
        mGetRegions = new GetRegionsTask();
        mGetRegions.execute();
    }

    class GetRegionsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Data data = Data.getInstance(MainActivity.this);


            return AuthenticateAndGetRegions(MainActivity.this.getApplicationContext(), data.getUsername(), data.getPassword());
        }


        @Override
        protected void onPostExecute(String json) {

            Log.e(TAG, "onPostExecute: " + json);
            mProgressbar.setVisibility(View.GONE);
            try {
                getRegionSuccessfully = true;
                JSONObject jsonObject = new JSONObject(json);
                bestRegion = jsonObject.getString("best_region");
                arrRegions = jsonObject.getJSONArray("regions");

                JSONObject bestJson = new JSONObject();
                bestJson.put("long_name", "Fastest Server");
                bestJson.put("short_name", "");

                Util.addToPos(0, bestJson, arrRegions);

                if (showRegionsAtEnd) {
                    showRegionsAtEnd = false;
                    MainActivity.this.mButtonLocation.performClick();
                }

                for (int i = 0; i < arrRegions.length(); i++) {
                    JSONObject obj = arrRegions.getJSONObject(i);
                    String shortName = obj.getString("short_name");
                    Data data = Data.getInstance(MainActivity.this);
                    if (data.getSelectedRegion().equals(shortName)) {
                        System.out.println("getregiontasks post execute");
                        buttonLocationTitle.setText(obj.getString("long_name"));
                        mButtonLocation.setVisibility(View.VISIBLE);
                        setUpFlagForCode(shortName);
                        data.setSelectedRegion(obj.getString("short_name"));
                        data.setSelectedRegionName(obj.getString("long_name"));
                        data.save();
                        System.out.println("country name: - " + obj.getString("long_name"));
                    }
                }
                Data data = Data.getInstance(MainActivity.this);
                data.setBestRegionCode(bestRegion);
            } catch (Exception e) {
                showRegionsAtEnd = false;
                getRegionSuccessfully = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Server Locations couldn’t be found. Please ensure that you are connected to the internet.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity_2);
        mainActivityContext = this;
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
            actionBar.setTitle("");
        }

        mDrawer = findViewById(R.id.drawer_layout);
        mFrame = findViewById(R.id.frame_layout);
        mDrawer.setScrimColor(Color.TRANSPARENT);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "titillium_regular.otf");
        Typeface boldFace = Typeface.createFromAsset(getAssets(),
                "titillium_bold.otf");

        countryFlag = findViewById(R.id.country_flag);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open, R.string.close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                mFrame.setTranslationX(slideX);
            }
        };

        mDrawer.addDrawerListener(actionBarDrawerToggle);

        TextView excludeAppText = findViewById(R.id.exclude_app);
        excludeAppText.setTypeface(face);
        excludeAppText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AppListActivity.class);
                startActivityForResult(intent, 1);
                mixpanel.track("Exclude apps button clicked on Main Screen");
                mDrawer.closeDrawers();
            }
        });

        TextView logout = findViewById(R.id.logout);
        logout.setTypeface(face);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject logoutProps = new JSONObject();
                try {
                    logoutProps.put(getString(R.string.analytics_from), "Side Menu");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Logout Button Clicked on Side Menu Screen", logoutProps);

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Logging out");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new LogoutTask().execute();

            }
        });

        TextView aboutUs = findViewById(R.id.about_us);
        aboutUs.setTypeface(face);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mixpanel.track("About us Button Clicked on Side Menu Screen");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.about_us_link)));
                startActivity(i);
            }
        });


        TextView textInterceptor = findViewById(R.id.interceptor);
        textInterceptor.setTypeface(face);
        textInterceptor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TextInterceptorResult.class);
                startActivityForResult(intent, 1);
            }
        });

        TextView helpCentre = findViewById(R.id.help_centre);
        helpCentre.setTypeface(face);
        helpCentre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mixpanel.track("Help Center Button Clicked on Side Menu Screen");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.help_center_link)));
                startActivity(i);
            }
        });

        TextView privacyPolicy = findViewById(R.id.privacy_policy);
        privacyPolicy.setTypeface(face);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mixpanel.track("Privacy Policy Button Clicked on Side Menu");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.privacy_policy_link)));
                startActivity(i);
            }
        });

        mData = Data.getInstance(this);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("ID", androidId);
        mAppSelect = findViewById(R.id.app_select);
        //    mTextViewStatus, mTextViewTime, mTextViewIP, mTextViewRegion,
        TextView skipButton = findViewById(R.id.skip_button);
        tutorialView = findViewById(R.id.tutorial_screen_conatiner);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialView.setVisibility(View.GONE);
                mData.setFirstTime(false);
                mData.save();
            }
        });

        mTutorialImage = findViewById(R.id.tutorial_image);

        if (mData.isFirstTime()) {
            //tutorialView.setVisibility(View.VISIBLE);
            mData.setFirstTime(false);
            mData.save();
        }

        //mData.setFirstTime(true);

        mAppSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject btnProps = new JSONObject();
                try {
                    btnProps.put("Name", getString(R.string.side_menu_btn));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Side Menu button Clicked on Main Screen", btnProps);
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });


        mButtonLocation = findViewById(R.id.buttonLocation);
        mProgressbar = findViewById(R.id.progressbar);
        mProgressbarLocation = findViewById(R.id.progressbarLocation);
        TextView button_amisecure_title = findViewById(R.id.button_amisecure_title);

        skipButton.setTypeface(face);
        SpannableStringBuilder secureSs = new SpannableStringBuilder("AM I EXPOSED?");
        secureSs.setSpan(new CustomTypefaceSpan("", face), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        secureSs.setSpan(new CustomTypefaceSpan("", boldFace), 5, 12, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        button_amisecure_title.setText(secureSs);

        TextView checkYouDataLabel = findViewById(R.id.checkYouDataLabel);
        checkYouDataLabel.setTypeface(face);

        Button btnAmISecure = findViewById(R.id.button_amisecure);
        btnAmISecure.setTypeface(boldFace);
        btnAmISecure.setTransformationMethod(null);
        btnAmISecure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HIBPActivity.class);
                JSONObject btnProps = new JSONObject();
                try {
                    btnProps.put(getString(R.string.analytics_from), "Main screen");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Lets Check Clicked on Main Screen", btnProps);

                JSONObject btnProps1 = new JSONObject();
                try {
                    btnProps1.put(getString(R.string.analytics_from), getString(R.string.main_screen));
                    btnProps1.put(getString(R.string.analytics_to), getString(R.string.am_i_exposed_screen));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Am I Exposed Search Screen Appeared", btnProps1);
                startActivityForResult(intent, 1);
            }
        });


        tvBodyguardLabel = findViewById(R.id.tvBodyguardLabel);
        tvBodyguardLabel.setTypeface(boldFace);
        animationView = findViewById(R.id.animationView);
        animationView.playAnimation();
        animationView.setRepeatCount(Integer.MAX_VALUE);
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getRegionSuccessfully) {
                    onClickConnect(v);
                } else {
                    showAlertWithOKMSG("Fetching Location now... Please try again.");
                }
            }
        });

        animationLineView = findViewById(R.id.animationView2);

        textTitle = findViewById(R.id.textTitle);
        titleSecure = findViewById(R.id.textSecure);
        subtitleSecure = findViewById(R.id.textSecureSubtitle);
        secureLayout = findViewById(R.id.secureLayout);
        titleSecure.setVisibility(View.INVISIBLE);
        //subtitleSecure.setVisibility(View.INVISIBLE);
        subtitleSecure.setTypeface(face);
        secureLayout.setVisibility(View.INVISIBLE);
        securetitle = findViewById(R.id.securetitle);
        textTitle.setTypeface(boldFace);
        SpannableStringBuilder ss = new SpannableStringBuilder(getResources().getString(R.string.notSecure));
        ss.setSpan(new CustomTypefaceSpan("", face), 0, 15, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ss.setSpan(new CustomTypefaceSpan("", boldFace), 15, 18, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ss.setSpan(new CustomTypefaceSpan("", face), 19, 25, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        securetitle.setText(ss);

        SpannableStringBuilder secureTextSs = new SpannableStringBuilder(getResources().getString(R.string.notSecure3));
        secureTextSs.setSpan(new CustomTypefaceSpan("", face), 0, 14, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        secureTextSs.setSpan(new CustomTypefaceSpan("", boldFace), 15, 22, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        titleSecure.setText(secureTextSs);

        buttonLocationTitle = findViewById(R.id.buttonLocationTitle);
        buttonLocationTitle.setTypeface(boldFace);

        disconnectBtn = findViewById(R.id.btn_disconnect);
        disconnectBtn.setTypeface(face);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mixpanel.track("Disconnect VPN Button Clicked on Main Screen");
                onClickConnect(view);
            }
        });

        mButtonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrRegions == null) {
                    showRegionsAtEnd = true;
                    getRegions();
                    return;
                }
                mixpanel.track("Server Location Button Clicked on Main Screen");
                ArrayList strRegions = new ArrayList();
                try {
                    for (int i = 0; i < arrRegions.length(); i++) {
                        JSONObject jsonRegion = arrRegions.getJSONObject(i);
                        strRegions.add(jsonRegion.getString("long_name"));
                    }
                } catch (Exception e) {

                }
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "titillium_regular.otf");
                pvOptions = new OptionsPickerBuilder(MainActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3, View v) {
                        try {
                            JSONObject region = arrRegions.getJSONObject(options1);
                            Data data = Data.getInstance(MainActivity.this);
                            data.setSelectedRegion(region.getString("short_name"));
                            data.setSelectedRegionName(region.getString("long_name"));
                            buttonLocationTitle.setText(region.getString("long_name"));
                            data.save();
                            JSONObject props1 = new JSONObject();
                            try {
                                props1.put("Country Name", region.getString("short_name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mixpanel.track("VPN Location Selected", props1);
                            setUpFlagForCode(region.getString("short_name"));
//                            mTextViewRegion.setText(region.getString("short_name"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                        .setSubmitText("Done")
                        .setCancelText("Cancel")
                        .setSubmitColor(Color.rgb(0, 212, 165))
                        .setCancelColor(Color.rgb(0, 212, 165))
                        .setTitleBgColor(Color.rgb(245, 245, 245))
                        .setBgColor(Color.rgb(245, 245, 245))
                        .setTypeface(face)
                        .setContentTextSize(20)
                        .build();

                pvOptions.setPicker(strRegions);
                pvOptions.show();
            }
        });

        if (mData.getSelectedLicense().equals("personal")) {
            mTutorialImage.setImageDrawable(getResources().getDrawable(R.drawable.tutorial_personal_android));
            try {
                JSONObject object = new JSONObject();
                object.put("long_name", "India");
                object.put("short_name", "IND");
                arrRegions = new JSONArray();
                arrRegions.put(object);
                bestRegion = "IND";
                buttonLocationTitle.setText(object.getString("long_name"));
                setFlagOnly(object.getString("short_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            if (!VpnStatus.isVPNActive()) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.getRegions();
                    }
                }, 1000);
            } else {
                System.out.println("on create when vpn is active");
                Data data = Data.getInstance(MainActivity.this);
                buttonLocationTitle.setText(data.getSelectedRegionName());
                setFlagOnly(data.getSelectedRegion());
            }

            mTutorialImage.setImageDrawable(getResources().getDrawable(R.drawable.tutorial_pro_vip_android));
        }
        new AppListTask().execute();

        mixpanel =
                MixpanelAPI.getInstance(getApplicationContext(), getResources().getString(R.string.MIXPANAL_TOKEN));

        checktype();

    }

    public void checktype() {
        JSONObject props1 = new JSONObject();
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                try {
                    props1.put("Connection", "Wifi");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                if (activeNetwork.getSubtypeName() == "LTE") { //4G
                    try {
                        props1.put("Connection", "Cellular");
                        props1.put("Connection Status", "4G");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (activeNetwork.getSubtypeName() == "HSPA+") { //3G
                    try {
                        props1.put("Connection", "Cellular");
                        props1.put("Connection Status", "3G");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (activeNetwork.getSubtypeName() == "EDGE") { //2G
                    try {
                        props1.put("Connection", "Cellular");
                        props1.put("Connection Status", "2G");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        mixpanel.track(getString(R.string.internet_connectivity_type), props1);

    }

    void showAlertWithOKMSG(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    public void onClickDisconnect(View view) {
        if (VpnStatus.isVPNActive()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle("Disconnect?");
            builder.setMessage("Are you sure you want to logout? Your VPN will automatically disconnect.");
            builder.setNegativeButton(android.R.string.no, null);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DeleteFromServer().execute();
                    Data data = Data.getInstance(MainActivity.this);
                    data.setConnectedForReboot(false);
                    data.save();
                    try {
                        mService.stopVPN(false);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

            builder.show();
        } else {
            new LogoutTask().execute();
        }
    }

    void setFlagOnly(String code) {
        if (code.equals("AUS")) {
            countryFlag.setImageResource(R.drawable.australia);
        } else if (code.equals("DEU")) {
            countryFlag.setImageResource(R.drawable.germany);
        } else if (code.equals("FRA")) {
            countryFlag.setImageResource(R.drawable.france);
        } else if (code.equals("GBR")) {
            countryFlag.setImageResource(R.drawable.united_kingdom);
        } else if (code.equals("IND")) {
            countryFlag.setImageResource(R.drawable.india);
        } else if (code.equals("SGP")) {
            countryFlag.setImageResource(R.drawable.singapore);
        } else if (code.equals("US2")) {
            countryFlag.setImageResource(R.drawable.usa);
        } else if (code.equals("")) {
            countryFlag.setImageDrawable(null);
        }
    }


    public void setUpFlagForCode(String code) {
        setFlagOnly(code);
        if (arrRegions != null && arrRegions.length() > 0) {
            for (int i = 0; i < arrRegions.length(); i++) {

                try {
                    JSONObject jsonRegion = arrRegions.getJSONObject(i);
                    String short_name = null;
                    short_name = jsonRegion.getString("short_name");
                    if (short_name.equals(code)) {
                        buttonLocationTitle.setText(jsonRegion.getString("long_name"));
                        System.out.println("country name btn locat: - " + jsonRegion.getString("long_name"));
                        Data data = Data.getInstance(MainActivity.this);
                        data.setSelectedRegion(jsonRegion.getString("short_name"));
                        data.setSelectedRegionName(jsonRegion.getString("long_name"));
                        data.save();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void onClickConnect(View view) {

        animationView.setClickable(false);

        if (VpnStatus.isVPNActive()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle("Disconnect?");
            builder.setMessage("Disconnect VPN?");
            builder.setNegativeButton(android.R.string.no, null);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Data data = Data.getInstance(MainActivity.this);
                    data.setConnectedForReboot(false);
                    data.save();
                    try {
                        mService.stopVPN(false);
                        new DeleteFromServer().execute();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

            builder.show();
            animationView.setClickable(true);

        } else {
            JSONObject btnProps = new JSONObject();
            try {
                btnProps.put("Name", getString(R.string.vpn_connect_btn));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mixpanel.track("Connect VPN Button Clicked on Main Screen", btnProps);
            mButtonLocation.setVisibility(View.GONE);
            mProgressbar.setVisibility(View.GONE);
            mProgressbarLocation.setVisibility(View.GONE);
            animationView.setAnimation(R.raw.state2);
            animationView.playAnimation();
            animationView.setRepeatCount(Integer.MAX_VALUE);
            System.out.println("calling    onBtn connect clicked");
            if (VPNConnectionRetryCount > 0) {
                textTitle.setText("Connecting... (Please wait)");
            } else {
                textTitle.setText("Connecting...");
            }
            securetitle.setVisibility(View.INVISIBLE);
            new RetrieveServerTask().execute();
        }
    }


    @Override
    public void setConnectedVPN(String uuid) {
        String a = "a";
    }


    /*
    ==============================================================================================
    START GET OVPN FILE FROM API
    ==============================================================================================
     */

    class RetrieveServerTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return GetConfigurationParams(MainActivity.this, MainActivity.this.getApplicationContext());
            //return null;
        }


        @Override
        protected void onPostExecute(Boolean resultOk) {
            //super.onPostExecute(aVoid);
            //start connection

            //Data data = Data.getInstance(MainActivity.this);
           // Log.d(TAG, "Parsing stops");
            System.out.println("Parsing stops");
            if (resultOk) {
                startVpnFromIntent();
            }
        }


    }

    private static void doImport(InputStream is) {
        ConfigParser cp = new ConfigParser();
        try {
            InputStreamReader isr = new InputStreamReader(is);

            cp.parseConfig(isr);
            mSelectedProfile = cp.convertProfile();
            mSelectedProfile.mAllowedAppsVpn.addAll(Data.getInstance(mainActivityContext).getDisallowedApps());
            System.out.println("mSelectedProfile:-  " + mSelectedProfile);
            Log.e(TAG, "doImport: " + mSelectedProfile.mAllowedAppsVpn);
            embedFiles(cp);
            return;

        } catch (IOException | ConfigParser.ConfigParseError e) {
            Log.d(TAG, "Error reading config file"); //getString(R.string.error_reading_config_file));
            Log.d(TAG, e.getLocalizedMessage());
        }
        mSelectedProfile = null;

    }

    /*
    ==============================================================================================
    START READ FILE CONFIGURATION METHODS
    ==============================================================================================
     */

    @Override
    public void updateState(String state, String logmessage, int localizedResId, final ConnectionStatus level) {
        //final String cleanLogMessage = VpnStatus.getLastCleanLogMessage(this);
//        Toast.makeText(MainActivity.this, state+"//////////>>>"+ logmessage+ "////////////>>>" +
//                        ""+ localizedResId + "///////////////>>>>"+ level, Toast.LENGTH_LONG).show();
        this.runOnUiThread(new Runnable() {

                               @Override
                               public void run() {

                                   switch (level) {
                                       case LEVEL_CONNECTED:
                                           System.out.println("connected callback called");
                                           mButtonLocation.setVisibility(View.GONE);
                                           mProgressbarLocation.setVisibility(View.GONE);
                                           disconnectBtn.setVisibility(View.VISIBLE);
                                           animationView.setAnimation(R.raw.transition2_state3);
                                           animationView.setRepeatCount(0);
                                           animationView.playAnimation();
                                           mixpanel.track("Main Screen VPN Connected");
                                           animationView.addAnimatorListener(new Animator.AnimatorListener() {
                                               @Override
                                               public void onAnimationStart(Animator animation) {

                                               }

                                               @Override
                                               public void onAnimationEnd(Animator animation) {
                                                   vpnConnected();
                                               }

                                               @Override
                                               public void onAnimationCancel(Animator animation) {

                                               }

                                               @Override
                                               public void onAnimationRepeat(Animator animation) {

                                               }
                                           });
                                           Data data = Data.getInstance(MainActivity.this);
                                           data.setCurrentIP(server);
                                           data.setConnectedForReboot(true);
                                           data.save();
                                           VPNConnectionRetryCount = 0;

                                          /* JSONObject props = new JSONObject();
                                           try {
                                               props.put("from", "Tutorial");
                                               props.put("to", "Main");
                                           } catch (JSONException e) {
                                               e.printStackTrace();
                                           }

                                           mixpanel.track("Main Screen VPN Connected", props)*/
                                           ;

                                          // new PostToServer().execute(); // no need for this now

                                           break;
                                       case LEVEL_VPNPAUSED:
                                          // new RetrievePublicIPTask().execute();
                                           break;
                                       case LEVEL_CONNECTING_SERVER_REPLIED:
                                       case LEVEL_CONNECTING_NO_SERVER_REPLY_YET:
                                           break;
                                       case LEVEL_NONETWORK:

                                           break;
                                       case LEVEL_NOTCONNECTED:
                                           if (!(VPNConnectionRetryCount > 0)) {
                                               animationView.setClickable(true);
                                               textTitle.setVisibility(View.VISIBLE);
                                               textTitle.setText("Tap to connect");
                                               mixpanel.track("Main Screen VPN Disconnected");
                                               titleSecure.setVisibility(View.INVISIBLE);
                                               //subtitleSecure.setVisibility(View.INVISIBLE);
                                               secureLayout.setVisibility(View.INVISIBLE);
                                               securetitle.setVisibility(View.VISIBLE);
                                               animationView.setVisibility(View.VISIBLE);
                                               animationView.setAnimation(R.raw.state1);
                                               animationView.setRepeatCount(Integer.MAX_VALUE);
                                               animationView.playAnimation();
                                               animationLineView.setVisibility(View.INVISIBLE);
                                               disconnectBtn.setVisibility(View.INVISIBLE);
                                               mButtonLocation.setVisibility(View.VISIBLE);
                                               mProgressbarLocation.setVisibility(View.VISIBLE);
                                               //new RetrievePublicIPTask().execute();
                                           }
                                           break;
                                       case LEVEL_START:
                                           System.out.println("connection start callback called");
                                           mButtonLocation.setVisibility(View.GONE);
                                           mProgressbarLocation.setVisibility(View.GONE);
                                           userIp = Data.getInstance(MainActivity.this).getCurrentIP();
                                           break;

                                       case LEVEL_AUTH_FAILED:
                                           VPNConnectionRetryCount = VPNConnectionRetryCount + 1;
                                           System.out.println("VPNConnectionRetryCount value: " + VPNConnectionRetryCount);
                                           if (VPNConnectionRetryCount < 6) {
                                               onClickConnect(null);//Retry upto 5 times
                                           } else {
                                               animationView.setClickable(true);
                                               textTitle.setVisibility(View.VISIBLE);
                                               textTitle.setText("Tap to connect");
                                               animationView.setAnimation(R.raw.state1);
                                               animationView.playAnimation();
                                               animationView.setRepeatCount(Integer.MAX_VALUE);
                                               animationView.setVisibility(View.VISIBLE);
                                               animationLineView.setVisibility(View.INVISIBLE);
                                               titleSecure.setVisibility(View.INVISIBLE);
                                               //subtitleSecure.setVisibility(View.INVISIBLE);
                                               secureLayout.setVisibility(View.INVISIBLE);
                                               securetitle.setVisibility(View.VISIBLE);
                                               disconnectBtn.setVisibility(View.INVISIBLE);
                                               mButtonLocation.setVisibility(View.VISIBLE);
                                               mProgressbarLocation.setVisibility(View.VISIBLE);
                                               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                               builder.setTitle("Error");
                                               builder.setMessage("Connection to our Server fails. Please try again after sometime.");
                                               builder.setPositiveButton("OK", null);
                                               builder.show();
                                               VPNConnectionRetryCount = 0;
                                           }
                                           break;
                                       case LEVEL_WAITING_FOR_USER_INPUT:
                                           mButtonLocation.setVisibility(View.GONE);
                                           mProgressbarLocation.setVisibility(View.GONE);
                                           break;

                                       case UNKNOWN_LEVEL:
                                       default:
                                          setToDefaultView();
                                           break;
                                   }
                               }
                           }
        );

    }

    private void vpnConnected() {
        animationLineView.setVisibility(View.VISIBLE);
        animationView.setVisibility(View.INVISIBLE);
        textTitle.setVisibility(View.INVISIBLE);
        titleSecure.setVisibility(View.VISIBLE);
        secureLayout.setVisibility(View.VISIBLE);
        //subtitleSecure.setVisibility(View.VISIBLE);
        securetitle.setVisibility(View.GONE);
        animationLineView.setAnimation(R.raw.animation2);
        animationLineView.setMinAndMaxProgress(0f, 0.5f);
        animationLineView.playAnimation();
        animationLineView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animationLineView.setMinAndMaxProgress(0.2f, 0.5f);
                animationLineView.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    static void embedFiles(ConfigParser cp) {

        if (mSelectedProfile.mPKCS12Filename != null) {
            File pkcs12file = findFileRaw(mSelectedProfile.mPKCS12Filename);
            if (pkcs12file != null) {
                mAliasName = pkcs12file.getName().replace(".p12", "");
            } else {
                mAliasName = "Imported PKCS12";
            }
        }


      /*  mSelectedProfile.mCaFilename = embedFile(mSelectedProfile.mCaFilename, Utils.FileType.CA_CERTIFICATE, false);
        mSelectedProfile.mClientCertFilename = embedFile(mSelectedProfile.mClientCertFilename, Utils.FileType.CLIENT_CERTIFICATE, false);
        mSelectedProfile.mClientKeyFilename = embedFile(mSelectedProfile.mClientKeyFilename, Utils.FileType.KEYFILE, false);
        mSelectedProfile.mTLSAuthFilename = embedFile(mSelectedProfile.mTLSAuthFilename, Utils.FileType.TLS_AUTH_FILE, false);
        mSelectedProfile.mPKCS12Filename = embedFile(mSelectedProfile.mPKCS12Filename, Utils.FileType.PKCS12, false);
        mSelectedProfile.mCrlFilename = embedFile(mSelectedProfile.mCrlFilename, Utils.FileType.CRL_FILE, true);
        if (cp != null) {
            mEmbeddedPwFile = cp.getAuthUserPassFile();
            mEmbeddedPwFile = embedFile(cp.getAuthUserPassFile(), Utils.FileType.USERPW_FILE, false);
        }*/

    }

  /*  private static String embedFile(String filename, Utils.FileType type, boolean onlyFindFileAndNullonNotFound) {
        if (filename == null)
            return null;

        // Already embedded, nothing to do
        if (VpnProfile.isEmbedded(filename))
            return filename;

        File possibleFile = findFile(filename, type);
        if (possibleFile == null)
            if (onlyFindFileAndNullonNotFound)
                return null;
            else
                return filename;
        else if (onlyFindFileAndNullonNotFound)
            return possibleFile.getAbsolutePath();
        else
            return readFileContent(possibleFile, type == Utils.FileType.PKCS12);

    }

    private static File findFile(String filename, Utils.FileType fileType) {
        File foundfile = findFileRaw(filename);

        if (foundfile == null && filename != null && !filename.equals("")) {
            Log.d("Cant find file %1$s", filename); // mentioned in the imported config file", filename);
        }
        //fileSelectMap.put(fileType, null);

        return foundfile;
    }*/


    private static File findFileRaw(String filename) {
        if (filename == null || filename.equals(""))
            return null;

        // Try diffent path relative to /mnt/sdcard
        File sdcard = Environment.getExternalStorageDirectory();
        File root = new File("/");

        HashSet<File> dirlist = new HashSet<>();

        for (int i = mPathsegments.size() - 1; i >= 0; i--) {
            String path = "";
            for (int j = 0; j <= i; j++) {
                path += "/" + mPathsegments.get(j);
            }
            // Do a little hackish dance for the Android File Importer
            // /document/primary:ovpn/openvpn-imt.conf


            if (path.indexOf(':') != -1 && path.lastIndexOf('/') > path.indexOf(':')) {
                String possibleDir = path.substring(path.indexOf(':') + 1, path.length());
                possibleDir = possibleDir.substring(0, possibleDir.lastIndexOf('/'));


                dirlist.add(new File(sdcard, possibleDir));

            }
            dirlist.add(new File(path));


        }
        dirlist.add(sdcard);
        dirlist.add(root);


        String[] fileparts = filename.split("/");
        for (File rootdir : dirlist) {
            String suffix = "";
            for (int i = fileparts.length - 1; i >= 0; i--) {
                if (i == fileparts.length - 1)
                    suffix = fileparts[i];
                else
                    suffix = fileparts[i] + "/" + suffix;

                File possibleFile = new File(rootdir, suffix);
                if (possibleFile.canRead())
                    return possibleFile;

            }
        }
        return null;
    }

    static String readFileContent(File possibleFile, boolean base64encode) {
        byte[] filedata;
        try {
            filedata = readBytesFromFile(possibleFile);
        } catch (IOException e) {
            Log.d(TAG, e.getLocalizedMessage());
            return null;
        }

        String data;
        if (base64encode) {
            data = Base64.encodeToString(filedata, Base64.DEFAULT);
        } else {
            data = new String(filedata);

        }

        return VpnProfile.DISPLAYNAME_TAG + possibleFile.getName() + VpnProfile.INLINE_TAG + data;

    }

    private static byte[] readBytesFromFile(File file) throws IOException {
        InputStream input = new FileInputStream(file);

        long len = file.length();
        if (len > VpnProfile.MAX_EMBED_FILE_SIZE)
            throw new IOException("File size of file to import too large.");

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) len];

        // Read in the bytes
        int offset = 0;
        int bytesRead;
        while (offset < bytes.length
                && (bytesRead = input.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += bytesRead;
        }

        input.close();
        return bytes;
    }

    /*
    ==============================================================================================
    START CONNECTION METHODS
    ==============================================================================================
    */

    private ProfileManager getPM() {
        return ProfileManager.getInstance(this);
    }

    private void startVpnFromIntent() {

        //get all profiles
        //mAllvpn = getPM().getProfiles();
        //mSelectedProfile = (VpnProfile) mAllvpn.toArray()[0];

        System.out.println("calling   getPM().addProfile(mSelectedProfile);");
        //TODO check save profile
        getPM().addProfile(mSelectedProfile);
        //getPM().saveProfile(this,mSelectedProfile);


        Data data = Data.getInstance(this);


        //String usernameWithUnderscore = data.getUsername().replace("@","_");
        mSelectedProfile.mUsername = data.getUsername();
        mSelectedProfile.mPassword = data.getPassword();

        //mSelectedProfile.mPassword = mEditTextPassword.getText().toString();
        JSONObject props1 = new JSONObject();
        try {
            props1.put("Country Name", this.region);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track(getString(R.string.selected_vpn_location), props1);
        System.out.println("mSelectedProfile mPassword:- " + mSelectedProfile.mPassword);
        setUpFlagForCode(this.region);
        System.out.println("calling   launchVPN intent");
        launchVPN();
    }

    private void launchVPN() {

        Intent intent = VpnService.prepare(this);
        // Check if we want to fix /dev/tun
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean usecm9fix = prefs.getBoolean("useCM9Fix", false);
        boolean loadTunModule = prefs.getBoolean("loadTunModule", false);

        if (loadTunModule)
            execeuteSUcmd("insmod /system/lib/modules/tun.ko");

        if (usecm9fix && !mCmfixed) {
            execeuteSUcmd("chown system /dev/tun");
        }

        System.out.println("calling   userpermission");

        if (intent != null) {
            VpnStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                    ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                // Shame on you Sony! At least one user reported that
                // an official Sony Xperia Arc S image triggers this exception
                VpnStatus.logError(R.string.no_vpn_support_image);
                //TODO: showLogWindow();
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.d("LOG", "On Activity Result");
//        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
//            super.onActivityResult(requestCode, resultCode, data);
//        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            boolean isSelected = data.getBooleanExtra("isChanged", false);
            if (isSelected) {
                onClickConnect(null);
            }
        }
        System.out.println("calling   onActivityResult");
        if (requestCode == START_VPN_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                /*int needpw = mSelectedProfile.needUserPWInput(false);
                if (needpw != 0) {
                    VpnStatus.updateStateString("USER_VPN_PASSWORD", "", R.string.state_user_vpn_password,
                            VpnStatus.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
                    askForPW(needpw);
                } else { */
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                boolean showLogWindow = prefs.getBoolean("showlogwindow", true);

                    /*if (!mhideLog && showLogWindow) {
                        showLogWindow();
                    }*/
                System.out.println("calling    VPNLaunchHelper.startOpenVpn");
                VPNLaunchHelper.startOpenVpn(mSelectedProfile, getBaseContext());
                //finish();
                //}
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User does not want us to start, so we just vanish
                VpnStatus.updateStateString("USER_VPN_PERMISSION_CANCELLED", "", R.string.state_user_vpn_permission_cancelled,
                        ConnectionStatus.LEVEL_NOTCONNECTED);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    VpnStatus.logError(R.string.nought_alwayson_warning);

                finish();
            }
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

    void showLogWindow() {

       /* Intent startLW = new Intent(getBaseContext(), LogWindow.class);
        startLW.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(startLW);*/

    }


    /*
    ==============================================================================================
    START LOG STATUS METHODS
    ==============================================================================================
     */


    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        Data data = Data.getInstance(MainActivity.this);
        server = data.getCurrentIP();
        region = data.getRegion();
//        mTextViewIP.setText(data.getCurrentIP());
//        mTextViewRegion.setText(data.getRegion());

        VpnStatus.addStateListener(this);
        VpnStatus.addByteCountListener(this);

        long onResumeTime = System.currentTimeMillis();

        if (onPauseTime != 0) {
            if ((onResumeTime - onPauseTime) / 1000 / 60 > 59) {
                //need to refresh
                if (mData.getSelectedLicense().equals("personal")) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("long_name", "India");
                        object.put("short_name", "IND");

                        arrRegions = new JSONArray();
                        arrRegions.put(object);
                        bestRegion = "IND";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.getRegions();
                        }
                    }, 1000);
                }

            }
        }

        String a = "a";


        final IntentFilter mIFNetwork = new IntentFilter();
        mIFNetwork.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION); //"android.net.conn.CONNECTIVITY_CHANGE"
        connectivityReceiver = new ConnectivityBroadcastReceiver();
        registerReceiver(connectivityReceiver, mIFNetwork);

//        new RetrievePublicIPTask().execute();

    }

    private ConnectivityBroadcastReceiver connectivityReceiver;

    private class ConnectivityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                return;
            }
            /*ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork.isConnected()) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    Log.d(TAG, "Network type: " + activeNetwork.getTypeName() +
                            " Network subtype: " + activeNetwork.getSubtypeName());
                    //getOwnIpAddress();
                    //mClient.updateUnicastSocket(mOwnAddress, mUnicastPort);
                }
            }
            else {
                Log.e(TAG, "Network connection lost");
            }*/
            //new RetrievePublicIPTask().execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(mConnection);

        VpnStatus.removeStateListener(this);

        onPauseTime = System.currentTimeMillis();
        //VpnStatus.removeByteCountListener(this);

        unregisterReceiver(connectivityReceiver);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance

            mService = OpenVPNService.Stub.asInterface(service);
            System.out.println("calling    onServiceConnected");
            //OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
            //mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }

    };

    class PostToServer extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.e(TAG, "Executiing - Post to Server");
            try {

                URL url = new URL(getApplicationContext().getString(R.string.server_url) + "/apiv1/connected_users/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                urlConnection.setRequestProperty("Accept", "*/*");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", Data.getInstance(MainActivity.this).getUsername());
                jsonObject.put("pass", Data.getInstance(MainActivity.this).getPassword());
                jsonObject.put("user_email", Data.getInstance(MainActivity.this).getUsername());
                jsonObject.put("serverIP", serverIp);
                jsonObject.put("osType", "android");


                try {
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();
                    os.close();
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
        }
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
                jsonObject.put("user", Data.getInstance(MainActivity.this).getUsername());
                jsonObject.put("pass", Data.getInstance(MainActivity.this).getPassword());
                jsonObject.put("user_email", Data.getInstance(MainActivity.this).getUsername());
                jsonObject.put("serverIP", serverIp);
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
        }
    }


    @Override
    public void updateByteCount(final long in, final long out, long diffIn, long diffOut) {

        String a = "a";

        this.runOnUiThread(new Runnable() {

                               @Override
                               public void run() {
                                   //mTextViewUpload.setText(humanReadableByteCount(out, false));
                                   //mTextViewDownload.setText(humanReadableByteCount(in, false));
                                   try {
                                       if (mService != null && mService.getConnectionTime() != 0) {
                                           long now = System.currentTimeMillis();
                                           long millis = now - mService.getConnectionTime();
                                           String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                                   TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                                                   TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

//                                           mTextViewTime.setText(hms);
                                       } else {
//                                           mTextViewTime.setText("");
                                           //mTextViewDownload.setText("");
                                           //mTextViewUpload.setText("");
                                       }
                                   } catch (RemoteException rem) {
//                                       mTextViewTime.setText("");
                                       //mTextViewDownload.setText("");
                                       //mTextViewUpload.setText("");
                                   }


                               }
                           }
        );
    }


    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String AuthenticateAndGetRegions(Context context, String user, String password) {
        try {
            Data data = Data.getInstance(context);

            URL url = new URL(context.getString(R.string.server_url) + "/apiv1/authenticate_and_get_regions/");
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
                    Log.e("ABCDEF", "AuthenticateAndGetRegions: " + json);
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



    /*
    ==============================================================================================
    START GET PUBLIC IP
    ==============================================================================================
     */

    class RetrievePublicIPTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {

                try {
                    //sleep because it takes time to reset connection sometimes
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();

                }

                URL url = new URL("https://ipinfo.io/ip");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setConnectTimeout(60000);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);//.append("\n");
                    }
                    bufferedReader.close();
                    String ip = stringBuilder.toString();
                    return ip;

                } catch (Exception e2) {
                    Log.e("ERROR", e2.getMessage(), e2);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return null;
        }


        @Override
        protected void onPostExecute(String ip) {

            if (VpnStatus.isVPNActive()) {
                return;
            }
            if (ip == null) {
                ip = "";
                //new RetrievePublicIPTask().execute();
            }

            Data data = Data.getInstance(MainActivity.this);
            data.setCurrentIP(ip);
            data.save();
//            mTextViewIP.setText(ip);


        }


    }


    class LogoutTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return LogoutUser(MainActivity.this.getApplicationContext());
        }


        @Override
        protected void onPostExecute(String json) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog.cancel();
            }

            if ("Success".equals(json)) {
                Data data = Data.getInstance(MainActivity.this);
                data.resetData();
                if (VpnStatus.isVPNActive()) {
                    try {
                        mService.stopVPN(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new DeleteFromServer().execute();
                }
                JSONObject props1 = new JSONObject();
                try {
                    props1.put("Email", data.getUsername());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track(getString(R.string.user_logged_out), props1);
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                mixpanel.getPeople().clearPushRegistrationId();
                startActivity(i);
                finish();
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
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

    class AppListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return fetchAppList();
        }

        @Override
        protected void onPostExecute(String json) {

            if (!"401".equals(json)) {
                if (json != null) {
                    Data data = Data.getInstance(MainActivity.this);
                    data.setAppList(json);
                    data.save();
                } else {
                    String previousAppList = Data.getInstance(MainActivity.this).getAppList();
                    Data data = Data.getInstance(MainActivity.this);
                    data.setAppList(previousAppList);
                    data.save();
                }
            }
        }
    }

    public String fetchAppList() {
        try {

            URL url = new URL(getString(R.string.server_url) + "/apiv1/black-list-url/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            HashMap<String, String> params = new HashMap<>();
            params.put("user", Data.getInstance(this).getUsername());
            params.put("pass", Data.getInstance(this).getPassword());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user", Data.getInstance(this).getUsername());
            jsonObject.put("pass", Data.getInstance(this).getPassword());

            try {
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                int status = urlConnection.getResponseCode();

                if (status != HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return "401";
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
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }
        return null;

    }

    public static String LogoutUser(Context context) {
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

    void setToDefaultView(){
        mButtonLocation.setVisibility(View.VISIBLE);
        mProgressbarLocation.setVisibility(View.VISIBLE);
        animationView.setVisibility(View.VISIBLE);
        animationView.setAnimation(R.raw.state1);
        animationView.setRepeatCount(Integer.MAX_VALUE);
        animationView.playAnimation();
        animationLineView.setVisibility(View.INVISIBLE);
        textTitle.setVisibility(View.VISIBLE);
        textTitle.setText("Tap to connect");
        securetitle.setVisibility(View.VISIBLE);
        disconnectBtn.setVisibility(View.INVISIBLE);
    }

    void setDisconnectView(){
        animationView.setClickable(true);
        textTitle.setVisibility(View.VISIBLE);
        textTitle.setText("Tap to connect");
        titleSecure.setVisibility(View.INVISIBLE);
        //subtitleSecure.setVisibility(View.INVISIBLE);
        secureLayout.setVisibility(View.INVISIBLE);
        securetitle.setVisibility(View.VISIBLE);
        animationView.setVisibility(View.VISIBLE);
        animationView.setAnimation(R.raw.state1);
        animationView.setRepeatCount(Integer.MAX_VALUE);
        animationView.playAnimation();
        animationLineView.setVisibility(View.INVISIBLE);
        disconnectBtn.setVisibility(View.INVISIBLE);
        mButtonLocation.setVisibility(View.VISIBLE);
        mProgressbarLocation.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (mGetRegions != null)
            mGetRegions.cancel(true);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
        super.onDestroy();
    }
}
