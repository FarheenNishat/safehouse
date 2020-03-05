package com.safehouse.bodyguard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.safehouse.bodyguard.service.TextServiceConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileUtilsTest {

    @Test
    public void getConfigurationFromFile() {

       // String json = "{\"packages\":\"com.google.android.gm\",\"flowPolicyConfiguration\":{\"packagesPolicies\":[{\"packageName\":\"com.google.android.gm\",\"exitEvents\":[32,4,1],\"enterEvents\":[32,4,1]}],\"defaultPolicy\":{\"exitEvents\":[32,4,1],\"enterEvents\":[32,4,1]}}}";
        String json = "{\"packages\":[\"com.google.android.gm\"],\"flowPolicyConfiguration\":{\"packagesPolicies\":[{\"packageName\":\"com.google.android.gm\",\"exitEvents\":[],\"enterEvents\":[]}],\"defaultPolicy\":{\"exitEvents\":[],\"enterEvents\":[]}}}";

        TextServiceConfiguration config = new Gson().fromJson(json, TextServiceConfiguration.class);


        assertEquals(1,config.getPackages().size());
    }
}