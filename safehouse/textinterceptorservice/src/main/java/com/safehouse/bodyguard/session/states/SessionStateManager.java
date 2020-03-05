package com.safehouse.bodyguard.session.states;

import android.view.accessibility.AccessibilityEvent;
import com.safehouse.bodyguard.session.SessionPolicyFunctions;

/**
 * Manages session states, the logic of each state depends on the session policy
 */
public interface SessionStateManager extends SessionPolicyFunctions {

    void setState(SESSION_STATE state, AccessibilityEvent nodeInfo);

    void extractUrlsFromEvents(AccessibilityEvent nodeInfo);


    void resetState();

}
