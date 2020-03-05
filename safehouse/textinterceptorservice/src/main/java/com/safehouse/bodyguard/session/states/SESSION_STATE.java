package com.safehouse.bodyguard.session.states;

import android.view.accessibility.AccessibilityEvent;
import com.safehouse.bodyguard.AccessibilityEventProcessor;

public abstract class SESSION_STATE implements AccessibilityEventProcessor {

    protected SessionStateManager stateSessionManager;

    public SESSION_STATE(SessionStateManager sessionStateManager) {

        this.stateSessionManager = sessionStateManager;
    }

    public void enterState(AccessibilityEvent event) {

    }
}
