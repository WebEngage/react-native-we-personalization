package com.webengage.sdk.android;


import android.content.Context;

import java.util.Queue;

public class AnalyticsFactory {
    static Analytics analytics = null;
    static Analytics noOp = null;
    static Analytics queuedImpl = null;

    public static Analytics getAnalytics(Context context) {
        if (analytics == null) {
            AnalyticsPreferenceManager analyticsPreferenceManager = new AnalyticsPreferenceManager(context.getApplicationContext());
            Scheduler scheduler = new Scheduler(context.getApplicationContext());
            SessionManager sessionManager = new SessionManager(analyticsPreferenceManager, context.getApplicationContext());
            analytics = new AnalyticsImpl(context, analyticsPreferenceManager, sessionManager, scheduler);
        }
        return analytics;
    }

    protected static Analytics getNoOpAnalytics() {
        if (noOp == null) {
            noOp = new AnalyticsNoOpImpl();
        }
        return noOp;
    }

    protected static Analytics getQueuedImpl(Queue<Task> queue) {
        if (queuedImpl == null) {
            queuedImpl = new QueuedAnalyticsImpl(queue);
        }
        return queuedImpl;
    }
}
