package com.webengage.sdk.android;


import android.content.Context;

import com.webengage.sdk.android.utils.WebEngageConstant;

class WebEngageUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler oldHandler = null;
    private Context applicationContext = null;
    private AnalyticsPreferenceManager analyticsPreferenceManager = null;


    WebEngageUncaughtExceptionHandler(Thread.UncaughtExceptionHandler oldHandler, Context context, AnalyticsPreferenceManager analyticsPreferenceManager) {
        this.oldHandler = oldHandler;
        this.applicationContext = context.getApplicationContext();
        this.analyticsPreferenceManager = analyticsPreferenceManager;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Logger.e(WebEngageConstant.TAG, "App has crashed\n" + e);
        if (this.analyticsPreferenceManager != null) {
            this.analyticsPreferenceManager.saveAppCrashedFlag(true);
        }
        if (this.oldHandler != null) {
            this.oldHandler.uncaughtException(t, e);
        }

    }
}
