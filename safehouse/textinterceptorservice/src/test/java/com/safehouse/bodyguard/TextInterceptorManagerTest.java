package com.safehouse.bodyguard;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.safehouse.bodyguard.data.UrlRepository;
import com.safehouse.bodyguard.session.*;
import com.safehouse.bodyguard.output.ActionOnUrlFound;
import com.safehouse.bodyguard.session.config.DefaultSessionPolicy;
import com.safehouse.bodyguard.session.config.PerPackageSessionPolicy;
import com.safehouse.bodyguard.session.config.SessionPolicyConfiguration;
import com.safehouse.bodyguard.session.states.SessionStateManager;
import com.safehouse.bodyguard.urlProcessing.UrlProcessor;
import com.safehouse.bodyguard.urlProcessing.UrlSearchStrategyRegexBased;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TextInterceptorManagerTest {

    public static final String GMAIL = "com.google.android.gm";
    UrlProcessor mUrlProcessor=new UrlProcessor(new UrlSearchStrategyRegexBased());

    @Mock
    ActionOnUrlFound mOutputService;

    @Mock
    AccessibilityNodeInfo mAccessibilityNodeInfo;
    @Mock
    AccessibilityEvent mAccessibilityEvent;


    SessionPolicyConfiguration mFlowPolicyConfig;


    private TextInterceptorManager mTextInterceptorManager;

    private SessionStateManager sessionStateManager;
    //private PerPackageSessionPolicy mPackageFlowPolicy;

    @Before
    public void setUp() throws Exception {


        List<Integer> changeFlowEvents = new ArrayList<>();
        changeFlowEvents.add(32);//AccessibilityEvent.WINDOWS_CHANGE_ACTIVE
        changeFlowEvents.add(1);//AccessibilityEvent.TYPE_VIEW_CLICKED
        changeFlowEvents.add(4);//AccessibilityEvent.TYPE_VIEW_SELECTED


        List<PerPackageSessionPolicy> test = new ArrayList<>();
        test.add(new PerPackageSessionPolicy(GMAIL, changeFlowEvents, changeFlowEvents));
        DefaultSessionPolicy defaultPolicy = new DefaultSessionPolicy( changeFlowEvents, changeFlowEvents);

        MockitoAnnotations.initMocks(this);
        sessionStateManager = mTextInterceptorManager = TextInterceptorManager.createTextInterceptorManager(mUrlProcessor, mOutputService, new SessionPolicy(new SessionPolicyConfiguration(test,defaultPolicy)),new UrlRepository());

    }

    //test SessionPolicy
    //the flow manager should check if the event is of the same type of the package to begin the flow
    @Test
    public void checkShouldBeginEventsCorrectFlow() {

        when(mAccessibilityEvent.getPackageName()).thenReturn(GMAIL);
        when(mAccessibilityEvent.getEventType()).thenReturn(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
        assertThat(sessionStateManager.shouldStartSession(mAccessibilityEvent), is(true));
        when(mAccessibilityEvent.getEventType()).thenReturn(AccessibilityEvent.TYPE_VIEW_CLICKED);
        assertThat(sessionStateManager.shouldStartSession(mAccessibilityEvent), is(true));
        when(mAccessibilityEvent.getEventType()).thenReturn(AccessibilityEvent.TYPE_VIEW_SELECTED);
        assertThat(sessionStateManager.shouldStartSession(mAccessibilityEvent), is(true));

        when(mAccessibilityEvent.getEventType()).thenReturn(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
        assertThat(sessionStateManager.shouldFinishSession(mAccessibilityEvent), is(false));

    }

    //test SessionPolicy
    //the flow manager should check if the event is of the same type of the package to begin the flow
    @Test
    public void checkShouldBeginEventsNotCorrectFlow() {
        when(mAccessibilityEvent.getPackageName()).thenReturn("dsdlfisjdfos");
        when(mAccessibilityEvent.getEventType()).thenReturn(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
        assertThat(sessionStateManager.shouldStartSession(mAccessibilityEvent), is(false));

    }

    //test SessionPolicy
    //the flow manager should stop flow if there is an event, of any package
    @Test
    public void checkShouldFinishEvents() {
        when(mAccessibilityEvent.getEventType()).thenReturn(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
        assertThat(sessionStateManager.shouldFinishSession(mAccessibilityEvent), is(true));
        when(mAccessibilityEvent.getEventType()).thenReturn(AccessibilityEvent.TYPE_VIEW_CLICKED);
        assertThat(sessionStateManager.shouldFinishSession(mAccessibilityEvent), is(true));
        when(mAccessibilityEvent.getEventType()).thenReturn(AccessibilityEvent.TYPE_VIEW_SELECTED);
        assertThat(sessionStateManager.shouldFinishSession(mAccessibilityEvent), is(true));

        when(mAccessibilityEvent.getEventType()).thenReturn(AccessibilityEvent.TYPE_VIEW_SCROLLED);
        assertThat(sessionStateManager.shouldFinishSession(mAccessibilityEvent), is(false));

    }


}