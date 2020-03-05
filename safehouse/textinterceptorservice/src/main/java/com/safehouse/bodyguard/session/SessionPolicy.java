package com.safehouse.bodyguard.session;

import android.annotation.SuppressLint;
import android.view.accessibility.AccessibilityEvent;
import com.safehouse.bodyguard.session.config.DefaultSessionPolicy;
import com.safehouse.bodyguard.session.config.PerPackageSessionPolicy;
import com.safehouse.bodyguard.session.config.SessionPolicyConfiguration;

import java.util.*;

/**
 * Each package has its on start-finish rules to determine when a session starts and when it ends, and it uses the accessibility event type that it receives to decide.
 * This means that for example, for some package we would like to start the session when the user clicks on something on the screen and end the session when the window focus change.
 * This may vary depending on the product use case and can be configured.
 */
public class SessionPolicy implements SessionPolicyFunctions {

    private static final String TAG = "SessionPolicy";
    private final DefaultSessionPolicy defaultPolicy;

    private Map<CharSequence, PerPackageSessionPolicy> packageToPolicyMap = new HashMap<>();

    public SessionPolicy(SessionPolicyConfiguration flowPolicyConfiguration) {

        defaultPolicy=flowPolicyConfiguration.getDefaultPolicy();
        for (PerPackageSessionPolicy policy :
                flowPolicyConfiguration.getPackagesPolicies()) {
            packageToPolicyMap.put(policy.getPackageName(), policy);
        }

    }


    @SuppressLint("WrongConstant")
    @Override
    public boolean shouldStartSession(AccessibilityEvent nodeInfo) {

        if(nodeInfo.getPackageName()==null)
            return false;


        if (packageToPolicyMap.containsKey(nodeInfo.getPackageName())) {
            PerPackageSessionPolicy policy = packageToPolicyMap.get(nodeInfo.getPackageName());
            return policy.getEnterEvents().contains(nodeInfo.getEventType());

        } else {
            return defaultPolicy.getEnterEvents().contains(nodeInfo.getEventType());
        }

    }


    @SuppressLint("WrongConstant")
    @Override
    public boolean shouldFinishSession(AccessibilityEvent nodeInfo) {


        if (packageToPolicyMap.containsKey(nodeInfo.getPackageName())) {
            PerPackageSessionPolicy policy = packageToPolicyMap.get(nodeInfo.getPackageName());
            return policy.getExitEvents().contains(nodeInfo.getEventType());

        } else {
            return defaultPolicy.getExitEvents().contains(nodeInfo.getEventType());

        }

    }
}
