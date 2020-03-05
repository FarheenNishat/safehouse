package com.safehouse.almasecure;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by danielbenedykt on 10/27/17.
 */

public class Util {

    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            Log.e("TTTTTTTT", entry.getKey()+"="+ entry.getValue());
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static void addToPos(int pos, JSONObject jsonObj, JSONArray jsonArr) throws JSONException {
        for (int i = jsonArr.length(); i > pos; i--) {
            jsonArr.put(i, jsonArr.get(i - 1));
        }
        jsonArr.put(pos, jsonObj);
    }

    public static void retunResponseFromAPI(){

    }


}
