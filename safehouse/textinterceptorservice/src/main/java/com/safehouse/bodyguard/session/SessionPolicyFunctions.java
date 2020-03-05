package com.safehouse.bodyguard.session;

import android.view.accessibility.AccessibilityEvent;
import com.safehouse.bodyguard.session.config.SessionPolicyConfiguration;

/**
 * Here we check weather the event starts a session or ends one.
 * This will be decided per package read from {@link SessionPolicyConfiguration}
 */
public interface SessionPolicyFunctions {


    //should look at the start flow policy
    boolean shouldStartSession(AccessibilityEvent nodeInfo);

    boolean shouldFinishSession(AccessibilityEvent nodeInfo);
}
