package com.safehouse.almasecure;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import de.blinkt.openvpn.R;

public class AppListActivity extends AppCompatActivity {

    private List<ApplicationInfo> mPackages; //Applicationinfo: Information you can retrieve about a particular application.
    private boolean isChanged = false;
    private ListView appListView;
    private JSONArray whiteOrBlackListedApps;
    private ProgressDialog progressDialog;
    private ApiListTask apiListTask;

    private TextView excludeAppTitle, tvdescription;
    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        apiListTask = new ApiListTask();
        Typeface face = Typeface.createFromAsset(getAssets(),
                "titillium_regular.otf");
        Typeface boldFace = Typeface.createFromAsset(getAssets(),
                "titillium_bold.otf");
        excludeAppTitle = findViewById(R.id.excludeAppTitle);
        excludeAppTitle.setTypeface(boldFace);
        tvdescription = findViewById(R.id.textView2);
        tvdescription.setTypeface(face);

        ImageButton closeExclude = findViewById(R.id.close_exclude);
        mixpanel =
                MixpanelAPI.getInstance(getApplicationContext(), getResources().getString(R.string.MIXPANAL_TOKEN));
        JSONObject props = new JSONObject();
        try {
            props.put(getString(R.string.analytics_from), getString(R.string.main_screen));
            props.put(getString(R.string.analytics_to), getString(R.string.exclude_apps_screen));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track("Exclude apps screen appeared", props);

        closeExclude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnAmISecure = findViewById(R.id.button_amisecure);
        btnAmISecure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppListActivity.this, HIBPActivity.class);
                startActivity(intent);
            }
        });

        apiListTask.execute();
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
        Data.getInstance(this).save();
        apiListTask.cancel(true);
        super.onDestroy();

    }

    private void populateList() {
        List<ApplicationInfo> installedPackages = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        int androidSystemUid = 0;
        ApplicationInfo system = null;
        Vector<ApplicationInfo> apps = new Vector<>();
        StringBuilder sb = new StringBuilder();
        String finalListNames = "";
        try {
            system = getPackageManager().getApplicationInfo("android", PackageManager.GET_META_DATA);
            androidSystemUid = system.uid;
           // apps.add(system);

            for (ApplicationInfo app : installedPackages) {

                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    // System application
                } else {
                    // Installed by user

                    if (getPackageManager().checkPermission(Manifest.permission.INTERNET, app.packageName) == PackageManager.PERMISSION_GRANTED &&
                            app.uid != androidSystemUid)
                    {
                        boolean found = false;
                        if (whiteOrBlackListedApps != null && whiteOrBlackListedApps.length() > 0)
                            for (int i = 0; i < whiteOrBlackListedApps.length(); i++) {
                                if (whiteOrBlackListedApps.getJSONObject(i).getString("type").equalsIgnoreCase("app"))
                                    if (whiteOrBlackListedApps.getJSONObject(i).getString("package_name") != null &&
                                            whiteOrBlackListedApps.getJSONObject(i).getString("package_name").equalsIgnoreCase(app.packageName)) {
                                        //Data.getInstance(this).getSelectedApps().add(whiteOrBlackListedApps.getJSONObject(i).getString("package_name"));
                                        found = true;
                                        break;
                                    }
                            }
                        if (!found && (!app.packageName.equals("com.bulletproof") && !app.packageName.equals("com.safehouse.bodyguard"))) {
                            apps.add(app);
                            finalListNames = sb.append(app.packageName + " , ").toString();
                        }

                    }
                }
            }
        } catch (PackageManager.NameNotFoundException | JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(getPackageManager()));
        mPackages = apps;
        System.out.println("apps:- " + apps);
        System.out.print("Packege names: " + finalListNames);
        //mixpanel.track("Exclude Apps lists: "+finalListNames);
        PackageAdapter adapter = new PackageAdapter(this, mPackages);
        appListView = findViewById(R.id.app_list);
        appListView.setAdapter(adapter);

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }

    public String fetchAppList() {

        Myutil ob=new Myutil();
        int status=ob.getStatus(getString(R.string.server_url),ConstantFile.blacklisturl);




            HashMap<String, String> params = new HashMap<>();
            params.put("user", Data.getInstance(this).getUsername());
            params.put("pass", Data.getInstance(this).getPassword());

            ob.setvalue(params);

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", Data.getInstance(this).getUsername());
                jsonObject.put("pass", Data.getInstance(this).getPassword());

                if (status != HttpURLConnection.HTTP_OK)
                  {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ob.getError()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return "401";

                   }
                else
                    {
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

    class ApiListTask extends AsyncTask<Void, Void, String> {

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

            if ("401".equals(json)) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog.cancel();
                }
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AppListActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Wrong username or password. Please try again.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                String previousAppList = Data.getInstance(AppListActivity.this).getAppList();
                try {
                    JSONObject jsonObject = new JSONObject(previousAppList);
                    whiteOrBlackListedApps = jsonObject.getJSONArray("items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                populateList();

            } else if (json != null) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    whiteOrBlackListedApps = jsonObject.getJSONArray("items");
                    populateList();
                    Data data = Data.getInstance(AppListActivity.this);
                    data.setAppList(json);
                    data.save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog.cancel();
                }
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AppListActivity.this);
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

                String previousAppList = Data.getInstance(AppListActivity.this).getAppList();
                try {
                    JSONObject jsonObject = new JSONObject(previousAppList);
                    whiteOrBlackListedApps = jsonObject.getJSONArray("items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                populateList();
            }
        }
    }

    class PackageAdapter extends ArrayAdapter<ApplicationInfo> implements CompoundButton.OnCheckedChangeListener {

        private final List<ApplicationInfo> applicationInfoList;
        private Activity context;

        public PackageAdapter(@NonNull Activity context, List<ApplicationInfo> applicationInfoList) {
            super(context, R.layout.allowed_vpn_apps, applicationInfoList);
            this.context = context;
            this.applicationInfoList = applicationInfoList;

        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            isChanged = true;
            String packageName = (String) compoundButton.getTag();
            if (isChecked) {
                Data.getInstance(context).getSelectedApps().add(packageName);
            } else {
                Data.getInstance(context).getSelectedApps().remove(packageName);
            }
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                LayoutInflater inflator = context.getLayoutInflater();
                view = inflator.inflate(R.layout.allowed_application_layout, null);
                final AppViewHolder viewHolder = new AppViewHolder();
                viewHolder.appName = view.findViewById(R.id.app_name);
                viewHolder.appIcon = view.findViewById(R.id.app_icon);
                viewHolder.checkBox = view.findViewById(R.id.app_selected);
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }
            ApplicationInfo mInfo = applicationInfoList.get(position);
            AppViewHolder holder = (AppViewHolder) view.getTag();
            CharSequence appName = mInfo.loadLabel(getPackageManager());

            if (TextUtils.isEmpty(appName))
                appName = mInfo.packageName;
            holder.appName.setTypeface(Typeface.createFromAsset(getAssets(),
                    "titillium_bold.otf"));
            holder.appName.setText(appName);
            try {
                holder.appIcon.setImageDrawable(mInfo.loadIcon(getPackageManager()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.checkBox.setTag(mInfo.packageName);
            holder.checkBox.setOnCheckedChangeListener(this);
            holder.checkBox.setChecked(Data.getInstance(context).getSelectedApps().contains(mInfo.packageName));
            return view;

        }

        @Override
        public int getCount() {
            if (applicationInfoList == null)
                return 0;
            return super.getCount();
        }

        class AppViewHolder {
            protected ApplicationInfo mInfo;
            protected View rootView;
            protected TextView appName;
            protected ImageView appIcon;
            protected Switch checkBox;
        }


    }
}
