package com.webengage.sdk.android;


import android.app.Activity;
import android.content.Intent;

import com.webengage.sdk.android.actions.database.Strategy;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.lang.ref.WeakReference;
import java.util.Map;

class AnalyticsTask implements Task<Analytics> {

    private int task = -1;
    private Object[] args = null;
    protected static final int TRACK = 0;
    protected static final int START = 1;
    protected static final int STOP = 2;
    protected static final int SCREEN_NAVIGATED = 3;
    protected static final int SCREEN_DATA = 4;
    protected static final int INSTALLED = 5;
    protected static final int TRACK_SYSTEM = 6;

    AnalyticsTask(int task, Object... args) {
        this.task = task;
        this.args = args;
    }

    @Override
    public void execute(Analytics analytics) {
        try {
            switch (this.task) {
                case TRACK:
                    if (this.args != null) {
                        String eventName = null;
                        Map<String, Object> attributes = null;
                        Analytics.Options options = null;
                        if (this.args.length > 0) {
                            eventName = (String) this.args[0];
                        }
                        if (this.args.length > 1) {
                            attributes = (Map<String, Object>) this.args[1];
                        }
                        if (this.args.length > 2) {
                            options = (Analytics.Options) this.args[2];
                        }
                        analytics.track(eventName, attributes, options);
                    }
                    break;

                case TRACK_SYSTEM:
                    if (this.args != null) {
                        String eventName = null;
                        Map<String, Object> systemAttributes = null;
                        Map<String, Object> eventAttributes = null;
                        if (this.args.length > 0) {
                            eventName = (String) this.args[0];
                        }
                        if (this.args.length > 1) {
                            systemAttributes = (Map<String, Object>) this.args[1];
                        }
                        if (this.args.length > 2 && null != this.args[2]) {
                            eventAttributes = (Map<String, Object>) this.args[2];
                        }
                        analytics.trackSystem(eventName, systemAttributes, eventAttributes);
                    }
                    break;
                case START:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        WeakReference<Activity> activityWeakReference = (WeakReference<Activity>) this.args[0];
                        if (activityWeakReference.get() != null) {
                            analytics.start(activityWeakReference.get());
                        }
                    }

                    break;

                case STOP:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        WeakReference<Activity> activityWeakReference = (WeakReference<Activity>) this.args[0];
                        if (activityWeakReference.get() != null) {
                            analytics.stop(activityWeakReference.get());
                        }
                    }
                    break;

                case SCREEN_NAVIGATED:
                    if (this.args != null) {
                        String screenName = null;
                        Map<String, Object> screenAttributes = null;
                        if (this.args.length > 0) {
                            screenName = (String) this.args[0];
                        }
                        if (this.args.length > 1) {
                            screenAttributes = (Map<String, Object>) this.args[1];
                        }
                        analytics.screenNavigated(screenName, screenAttributes);
                    }

                    break;

                case SCREEN_DATA:
                    if (this.args != null && this.args.length > 0) {
                        Map<String, Object> screenData = (Map<String, Object>) this.args[0];
                        analytics.setScreenData(screenData);
                    }
                    break;

                case INSTALLED:
                    if (this.args != null && this.args.length > 0) {
                        Intent intent = (Intent) this.args[0];
                        analytics.installed(intent);
                    }
                    break;

            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Some error occurred while executing queued task of Analytics: " + e.toString());
        }
    }
}
