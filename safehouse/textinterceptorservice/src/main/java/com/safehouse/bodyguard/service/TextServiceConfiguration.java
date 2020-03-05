package com.safehouse.bodyguard.service;

import android.content.Context;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.safehouse.bodyguard.R;
import com.safehouse.bodyguard.session.config.SessionPolicyConfiguration;
import com.safehouse.bodyguard.utils.JsonParseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A data class that contains the service configuration (i.e which packages should we read from) and the session policy per package.
 */
public class TextServiceConfiguration {



    @SerializedName("packages")
    @Expose
    List<String> packages;

    @SerializedName("allowAll")
    @Expose
    boolean allowAll;

    @SerializedName("flowPolicyConfiguration")
    @Expose
    SessionPolicyConfiguration config;

    public List<String> getPackages() {
        return packages;
    }

    public SessionPolicyConfiguration getConfig() {
        return config;
    }


    /**
     * retrieves the default TextServiceConfiguration and policy from a local file
     * @param context
     * @return
     */
    public static TextServiceConfiguration getConfigurationFromFile(Context context) {

        TextServiceConfiguration config = null;
        try (InputStream is = context
                .getResources()
                .openRawResource(R.raw.configuration)) {

            config= new JsonParseUtils<TextServiceConfiguration>().readJsonFromFile(is, TextServiceConfiguration.class, new GsonBuilder());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }
}
