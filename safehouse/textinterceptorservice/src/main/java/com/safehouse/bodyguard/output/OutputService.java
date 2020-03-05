package com.safehouse.bodyguard.output;

import android.content.Context;
import android.content.Intent;

public class OutputService implements ActionOnUrlFound {

    public OutputService(Context applicationContext, TextInterceptorLogger mTextInterceptorLogger) {
        this.applicationContext = applicationContext;
        this.mTextInterceptorLogger = mTextInterceptorLogger;
    }

    private Context applicationContext;
    TextInterceptorLogger mTextInterceptorLogger;

    @Override
    public void execute(String urls) {
        //mTextInterceptorLogger.logd("url is "+urls);
        Intent intent = new Intent();
        intent.setAction(UrlOuputIntent.URL_ACTION);
        intent.putExtra(UrlOuputIntent.URL_KEY, urls);
        applicationContext.sendBroadcast(intent);
    }
}
