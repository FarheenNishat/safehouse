package com.safehouse.bodyguard;

import android.annotation.SuppressLint;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.safehouse.bodyguard.data.AccesibilityEventAggregator;
import com.safehouse.bodyguard.data.UrlRepository;
import com.safehouse.bodyguard.output.ActionOnUrlFound;
import com.safehouse.bodyguard.session.SessionPolicy;
import com.safehouse.bodyguard.session.states.SESSION_STATE;
import com.safehouse.bodyguard.session.states.SESSION_STATE__NONE;
import com.safehouse.bodyguard.session.states.SessionStateManager;
import com.safehouse.bodyguard.data.UrlEventWrapper;
import com.safehouse.bodyguard.urlProcessing.UrlProcessor;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

    /**
     * This is the main player that orchestrates all the others.
     * class that orcastrated all the flow, from the entry point that comes through {@link #process(AccessibilityEvent)}
     * to the output that is sent to {@link com.safehouse.bodyguard.output.ActionOnUrlFound}
     */


    public class TextInterceptorManager implements SessionStateManager,AccessibilityEventProcessor {
    private static final String TAG = "TextInterceptorManager";
    private UrlRepository urlRepository;
    private UrlProcessor mUrlProcessor;
    private ActionOnUrlFound mOutputService;
    private SessionPolicy sessionPolicy;
    private AccessibilityEventProcessor mEventProcessor = new SESSION_STATE__NONE(this);




    private TextInterceptorManager(UrlProcessor mUrlProcessor, ActionOnUrlFound mOutputService, SessionPolicy sessionPolicy, UrlRepository urlRepository) {
        this.mUrlProcessor = mUrlProcessor;
        this.mOutputService = mOutputService;
        this.sessionPolicy = sessionPolicy;
        this.urlRepository = urlRepository;
    }

        public static TextInterceptorManager createTextInterceptorManager(UrlProcessor mUrlProcessor, ActionOnUrlFound mOutputService, SessionPolicy sessionPolicy, UrlRepository urlRepository) {
            return new TextInterceptorManager(mUrlProcessor, mOutputService, sessionPolicy, urlRepository);
        }


        public void process(AccessibilityEvent accessibilityEvent) {
        mEventProcessor.process(accessibilityEvent);
    }


        @Override
        public void setState(SESSION_STATE state, AccessibilityEvent nodeInfo) {
            mEventProcessor = state;
            state.enterState(nodeInfo);

        }




        @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    @Override
    public void extractUrlsFromEvents(AccessibilityEvent accessibilityEvent) {

        final AccesibilityEventAggregator aggregator = getAllTextInEvent(accessibilityEvent.getSource(),new AccesibilityEventAggregator());


        if (aggregator.getText().isEmpty() )
            return;

        //try to see if we already processed this event, im sure there are better ways to do this but this is basic
        if(urlRepository.isEventBeenProcessed(aggregator))
        {
           return;
        }

        //save the event
        urlRepository.saveAccesibilityEvent(aggregator);

        Observable.just(mUrlProcessor.findUrlsInText(aggregator.getText())).subscribeOn(Schedulers.computation()).filter(new Predicate<List<String>>() {
            @Override
            public boolean test(List<String> urls) throws Exception {
                return !(urls == null || urls.size() == 0);
            }
        }).flatMap(new Function<List<String>, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(List<String> strings) throws Exception {
                return Observable.fromIterable(strings);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String url) throws Exception {

                UrlEventWrapper urlEvent = new UrlEventWrapper(url, String.valueOf(aggregator.getIdentifierKey()));
                if (urlRepository.isRepeatedEvent(urlEvent)) {
                    return;
                }

                urlRepository.saveUrlEvent(urlEvent);
                mOutputService.execute(url);
            }
        });


    }

    @Override
    public void resetState() {
        urlRepository.reset();
    }

    @Override
    public boolean shouldStartSession(AccessibilityEvent nodeInfo) {
        return sessionPolicy.shouldStartSession(nodeInfo);
    }

    @Override
    public boolean shouldFinishSession(AccessibilityEvent nodeInfo) {
        return sessionPolicy.shouldFinishSession(nodeInfo);
    }





    private AccesibilityEventAggregator getAllTextInEvent(AccessibilityNodeInfo info, AccesibilityEventAggregator accesibilityEventAggregator) {

        if(info==null)
            return accesibilityEventAggregator;


        accesibilityEventAggregator.appendClassHirachy(info.getClassName());

        if (info.getText() != null && info.getText().length() > 0) {
            accesibilityEventAggregator.appendString(info.getText());
        }

        for (int i = 0; i < info.getChildCount(); ++i) {
            getAllTextInEvent(info.getChild(i),accesibilityEventAggregator);
        }

        return accesibilityEventAggregator;

    }





}
