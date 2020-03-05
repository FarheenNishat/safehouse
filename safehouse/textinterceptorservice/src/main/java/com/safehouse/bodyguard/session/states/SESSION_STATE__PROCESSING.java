package com.safehouse.bodyguard.session.states;

import android.view.accessibility.AccessibilityEvent;

public class SESSION_STATE__PROCESSING extends SESSION_STATE {

    private CharSequence packageNameOfFlow;

    public SESSION_STATE__PROCESSING(SessionStateManager sessionStateManager) {
        super(sessionStateManager);
    }

    @Override
    public void process(AccessibilityEvent event) {
        if(stateSessionManager.shouldFinishSession(event))
        {
            stateSessionManager.resetState();
            packageNameOfFlow = null;
            stateSessionManager.setState(new SESSION_STATE__NONE(stateSessionManager), event);

        }else
        {
            stateSessionManager.extractUrlsFromEvents(event);
        }
    }

    @Override
    public void enterState(AccessibilityEvent event) {
        packageNameOfFlow = event.getPackageName();
        stateSessionManager.extractUrlsFromEvents(event);
    }

}
