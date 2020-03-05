package com.safehouse.almasecure;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

public class PreferenceUtil {

    private static final String TAG = "Safehouse Bodyguard";

    public static void setString(SharedPreferences _pref, String key, String value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            try {
                if (_pref != null) {
                    SharedPreferences.Editor editor = _pref.edit();
                    editor.putString(key, value);
                    editor.apply();
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception:", e);
            }
        }
    }

    public static void setLong(SharedPreferences _pref, String key, long value) {
        if (!TextUtils.isEmpty(key)) {
            try {
                if (_pref != null) {
                    SharedPreferences.Editor editor = _pref.edit();
                    editor.putLong(key, value);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception:", e);
            }
        }
    }


    public static void setInt(SharedPreferences _pref, String key, int value) {
        if (!TextUtils.isEmpty(key)) {
            try {
                if (_pref != null) {
                    SharedPreferences.Editor editor = _pref.edit();
                    editor.putInt(key, value);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception:", e);
            }
        }
    }


    public static void setDouble(SharedPreferences _pref, String key, double value) {
        if (!TextUtils.isEmpty(key)) {
            try {
                if (_pref != null) {
                    SharedPreferences.Editor editor = _pref.edit();
                    editor.putFloat(key, (float) value);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception:", e);
            }
        }
    }


    public static void setBoolean(SharedPreferences _pref, String key, boolean value) {
        if (!TextUtils.isEmpty(key)) {
            try {
                if (_pref != null) {
                    SharedPreferences.Editor editor = _pref.edit();
                    editor.putBoolean(key, value);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception:", e);
            }
        }
    }

    public static int getInt(SharedPreferences _pref, String key, int defaultValue) {
        if (_pref != null && !TextUtils.isEmpty(key) && _pref.contains(key)) {
            return _pref.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    public static long getLong(SharedPreferences _pref, String key, long defaultValue) {
        if (_pref != null && !TextUtils.isEmpty(key) && _pref.contains(key)) {
            return _pref.getLong(key, defaultValue);
        }
        return defaultValue;
    }

    public static boolean getBoolean(SharedPreferences _pref, String key, boolean defaultValue) {
        if (_pref != null && !TextUtils.isEmpty(key) && _pref.contains(key)) {
            return _pref.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }


    public static boolean isNull(SharedPreferences _pref, String key) {
        return !(_pref != null && !TextUtils.isEmpty(key) && _pref.contains(key));

    }

    public static String getString(SharedPreferences _pref, String key, String defaultValue) {
        if (_pref != null && !TextUtils.isEmpty(key) && _pref.contains(key)) {
            return _pref.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public static double getDouble(SharedPreferences _pref, String key, double defaultValue) {
        if (_pref != null && !TextUtils.isEmpty(key) && _pref.contains(key)) {
            return _pref.getFloat(key, (float) defaultValue);
        }
        return defaultValue;
    }

    public static void removeString(SharedPreferences _pref, String key) {
        if (!TextUtils.isEmpty(key)) {
            try {
                if (_pref != null && _pref.contains(key)) {
                    SharedPreferences.Editor editor = _pref.edit();
                    editor.remove(key);
                    editor.apply();
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception:", e);
            }
        }
    }

}
