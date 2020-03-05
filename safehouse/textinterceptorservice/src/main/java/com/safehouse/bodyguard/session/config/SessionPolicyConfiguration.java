package com.safehouse.bodyguard.session.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SessionPolicyConfiguration {

    public SessionPolicyConfiguration(List<PerPackageSessionPolicy> packagesPolicies, DefaultSessionPolicy defaultPolicy) {
        this.packagesPolicies = packagesPolicies;
        this.defaultPolicy = defaultPolicy;
    }

    @SerializedName("packagesPolicies")
    @Expose
    private List<PerPackageSessionPolicy> packagesPolicies = null;
    @SerializedName("defaultPolicy")
    @Expose
    private DefaultSessionPolicy defaultPolicy;

    public List<PerPackageSessionPolicy> getPackagesPolicies() {
        return packagesPolicies;
    }

    public DefaultSessionPolicy getDefaultPolicy() {
        return defaultPolicy;
    }



}


