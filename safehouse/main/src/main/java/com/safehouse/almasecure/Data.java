package com.safehouse.almasecure;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by danielbenedykt on 11/7/16.
 */

public class Data {

    private static Data instance = null;
    Context context;

    public static final String PREFS_NAME = "MyPrefsFile";

    private String username;
    private String password;
    private String currentIP;
    private String region;
    private String pushIDToken;
    private boolean isConnectedForReboot;
    private boolean isFirstTime;
    private String selectedRegion;

    public String getSelectedRegionName() {
        return selectedRegionName;
    }

    public void setSelectedRegionName(String selectedRegionName) {
        this.selectedRegionName = selectedRegionName;
    }

    private String selectedRegionName;
    private Set<String> selectedApps;
    private String jsonLicense;
    private String selectedLicense;
    private String licenseHash;
    private String appList;

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    private String serverIp;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        System.out.println("set versionCode: - "+versionCode);
    }

    private int versionCode;

    private String bestRegionCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCurrentIP() {
        if(currentIP==null)
        {
            return "";
        }
        return currentIP;
    }

    public void setCurrentIP(String currentIP) {
        this.currentIP = currentIP;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public String getPushIDToken() {
        return pushIDToken;
    }

    public void setPushIDToken(String pushIDToken) {
        this.pushIDToken = pushIDToken;
    }

    public Set<String> getSelectedApps() {
        if (selectedApps == null)
            selectedApps = new HashSet<>();
        return selectedApps;
    }

    public Set<String> getDisallowedApps(){
        Set<String> disallowedApps = new HashSet<>();
        if(selectedApps != null){
            disallowedApps.addAll(selectedApps);
        }

        if(appList != null) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(appList);
                JSONArray whiteOrBlackListedApps = jsonObject.getJSONArray("items");
                for (int i = 0; i < whiteOrBlackListedApps.length(); i++) {
                    if(whiteOrBlackListedApps.getJSONObject(i).getString("package_name") != null)
                        disallowedApps.add(whiteOrBlackListedApps.getJSONObject(i).getString("package_name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return disallowedApps;

    }

    public void setSelectedApps(HashSet<String> selectedApps) {
        this.selectedApps = selectedApps;
    }

    public boolean isConnectedForReboot() {
        return isConnectedForReboot;
    }

    public void setConnectedForReboot(boolean connectedForReboot) {
        isConnectedForReboot = connectedForReboot;
    }

    public String getSelectedRegion() {
        return selectedRegion;
    }

    public void setSelectedRegion(String selectedRegion) {
        this.selectedRegion = selectedRegion;
    }

    public String getJsonLicense() {
        return jsonLicense;
    }

    public void setJsonLicense(String jsonLicense) {
        this.jsonLicense = jsonLicense;
    }

    public String getSelectedLicense() {
        return selectedLicense;
    }

    public void setSelectedLicense(String selectedLicense) {
        this.selectedLicense = selectedLicense;
    }

    public String getLicenseHash() {
        return licenseHash;
    }

    public void setLicenseHash(String licenseHash) {
        this.licenseHash = licenseHash;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }



    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    public String getAppList() {
        return appList;
    }

    public void setAppList(String appList) {
        this.appList = appList;
    }

    public static Data getInstance(Context theContext) {
        if (instance == null) {
            instance = new Data(theContext);
            //instance.load();
        }
        return instance;
    }

    private Data(Context context) {
        this.context = context.getApplicationContext();
        this.load();
    }

    public void load() {
        // Restore preferences
        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        username = settings.getString("username", null);
        password = settings.getString("password", null);
        currentIP = settings.getString("currentIP", "");
        region = settings.getString("region", "");
        pushIDToken = settings.getString("pushIDToken","");
        isConnectedForReboot = settings.getBoolean("isConnectedForReboot",false);
        selectedRegion = settings.getString("selectedRegion","");
        selectedApps = settings.getStringSet("selectedApps", new HashSet<String>());
        jsonLicense = settings.getString("licenses","");
        selectedLicense = settings.getString("selected_license",null);
        isFirstTime = settings.getBoolean("is_first_time",true);
        licenseHash = settings.getString("license_hash","");
        appList = settings.getString("app_list", "");
        versionCode = settings.getInt("version_code", 0);
        selectedRegionName = settings.getString("selectedRegionName","");
    }

    public void save() {

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("username",username);
        editor.putString("password",password);
        editor.putString("currentIP",currentIP);
        editor.putString("region",region);
        editor.putString("pushIDToken", pushIDToken);
        editor.putBoolean("isConnectedForReboot", isConnectedForReboot);
        editor.putString("selectedRegion", selectedRegion);
        editor.putString("selectedRegionName", selectedRegionName);
        editor.putStringSet("selectedApps", new HashSet<String>(selectedApps));
        editor.putString("licenses",jsonLicense);
        editor.putString("selected_license",selectedLicense);
        editor.putBoolean("is_first_time",isFirstTime);
        editor.putString("license_hash",licenseHash);
        editor.putString("app_list", appList);
        editor.putInt("versionCode", versionCode);
        editor.commit();
    }

    public void resetData() {
        /*
         * phoneNumberLong = ""; xAuthToken = ""; this.save();
         */
        String user = getUsername();
        System.out.println("resetData called: -");
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        instance=null;

        getInstance(context).setUsername(user);
        getInstance(context).save();
    }

    public String getBestRegionCode() {
        return bestRegionCode;
    }

    public void setBestRegionCode(String bestRegionCode) {
        this.bestRegionCode = bestRegionCode;
    }
}
