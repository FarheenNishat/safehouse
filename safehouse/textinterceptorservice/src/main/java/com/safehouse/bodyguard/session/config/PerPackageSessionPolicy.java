package com.safehouse.bodyguard.session.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PerPackageSessionPolicy {

    public PerPackageSessionPolicy(String packageName, List<Integer> exitEvents, List<Integer> enterEvents) {
        this.packageName = packageName;
        this.exitEvents = exitEvents;
        this.enterEvents = enterEvents;
    }

    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("exitEvents")
    @Expose
    private List<Integer> exitEvents = null;
    @SerializedName("enterEvents")
    @Expose
    private List<Integer> enterEvents = null;

    public String getPackageName() {
        return packageName;
    }
    public List<Integer> getExitEvents() {
        return exitEvents;
    }
    public List<Integer> getEnterEvents() {
        return enterEvents;
    }



}









