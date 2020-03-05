package com.safehouse.bodyguard.session.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DefaultSessionPolicy {


    public DefaultSessionPolicy(List<Integer> exitEvents, List<Integer> enterEvents) {
        this.exitEvents = exitEvents;
        this.enterEvents = enterEvents;
    }


    @SerializedName("exitEvents")
    @Expose
    private List<Integer> exitEvents = null;
    @SerializedName("enterEvents")
    @Expose
    private List<Integer> enterEvents = null;

    public List<Integer> getExitEvents() {
        return exitEvents;
    }
    public List<Integer> getEnterEvents() {
        return enterEvents;
    }



}
