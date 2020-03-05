package com.safehouse.bodyguard.session.states;

import android.view.accessibility.AccessibilityEvent;

public class SESSION_STATE__NONE extends SESSION_STATE {

    public static final String TAG = "SESSION_STATE__NONE";

    public SESSION_STATE__NONE(SessionStateManager sessionStateManager) {
        super(sessionStateManager);
    }

    @Override
    public void process(AccessibilityEvent nodeInfo) {
        //search for package
        //search for eventtype or event type flow
        //if its ok then, change state

        if(stateSessionManager.shouldStartSession(nodeInfo))
        {
            stateSessionManager.setState(new SESSION_STATE__PROCESSING(stateSessionManager),nodeInfo);
        }
       // stateSessionManager.extractUrlsFromEvents(nodeInfo);


    }

    @Override
    public void enterState(AccessibilityEvent event) {

    }

}
