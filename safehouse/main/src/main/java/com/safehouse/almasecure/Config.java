package com.safehouse.almasecure;

import android.content.Context;
import android.content.SharedPreferences;


public class Config {
    private final String TAG = Config.class.getSimpleName();
    public static final String PREF_NAME = "safehouse_bodyguard";
    private final SharedPreferences _pref;

    public Config(Context context) {
        _pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This Method Clear shared preference.
     */
    public void clear() {
        SharedPreferences.Editor editor = _pref.edit();
        editor.clear();
        editor.commit();
    }


    public void setVersionCode(int id){
        PreferenceUtil.setInt(_pref,Key.versionCode,id);
    }

    public int getVersionCode(){
        return  PreferenceUtil.getInt(_pref,Key.versionCode,0);
    }



    private final class Key {
        public static final String versionCode="versionCode";
    }
}



