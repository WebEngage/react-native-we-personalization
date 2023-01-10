package com.webengage.sdk.android;

import android.app.Activity;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.Map;

class AnalyticsNoOpImpl extends Analytics {

    @Override
    public void track(String eventName) {

    }

    @Override
    public void track(String eventName, Options options) {

    }

    @Override
    public void track(String eventName, Map<String, ? extends Object> attributes) {

    }

    @Override
    public void track(String eventName, Map<String, ?> attributes, Options options) {

    }

    @Override
    public void trackSystem(String eventName, Map<String, ?> systemAttributes, Map<String, ?> eventAttributes) {

    }

    @Override
    public void start(Activity activity) {

    }

    @Override
    public void stop(Activity activity) {

    }

    @Override
    public void screenNavigated(String screenName) {

    }

    @Override
    public void screenNavigated(String screenName, Map<String, ? extends Object> screenDaat) {

    }

    @Override
    public void setScreenData(Map<String, ? extends Object> screenData) {

    }

    @Override
    public void installed(Intent intent) {

    }

    @Override
    public WeakReference<Activity> getActivity() {
        return null;
    }

    @Override
    protected SessionManager getSessionManager() {
        return null;
    }

    @Override
    protected Scheduler getScheduler() {
        return null;
    }

    @Override
    protected AnalyticsPreferenceManager getPreferenceManager() {
        return null;
    }

    @Override
    protected void dispatchEventTopic(Object data) {

    }

    @Override
    protected void dispatchExceptionTopic(Object data) {

    }
}
