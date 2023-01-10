package com.webengage.sdk.android;

import android.app.Activity;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Queue;

/**
 * Created by shahrukhimam on 05/10/17.
 */

class QueuedAnalyticsImpl extends Analytics {
    Queue<Task> queue;

    QueuedAnalyticsImpl(Queue<Task> queue) {
        this.queue = queue;
    }

    @Override
    public void track(String eventName) {
        this.track(eventName, null, null);
    }

    @Override
    public void track(String eventName, Options options) {
        this.track(eventName, null, options);
    }

    @Override
    public void track(String eventName, Map<String, ? extends Object> attributes) {
        this.track(eventName, attributes, null);
    }

    @Override
    public void track(String eventName, Map<String, ?> attributes, Options options) {
        Task task = new AnalyticsTask(AnalyticsTask.TRACK, eventName, attributes, options);
        this.queue.add(task);
    }

    @Override
    public void trackSystem(String eventName, Map<String, ?> systemAttributes, Map<String, ?> eventAttributes) {
        Task task = new AnalyticsTask(AnalyticsTask.TRACK_SYSTEM, eventName, systemAttributes, eventAttributes);
        this.queue.add(task);
    }

    @Override
    public void start(Activity activity) {
        Task task = new AnalyticsTask(AnalyticsTask.START, new WeakReference<Activity>(activity));
        this.queue.add(task);
    }

    @Override
    public void stop(Activity activity) {
        Task task = new AnalyticsTask(AnalyticsTask.STOP, new WeakReference<Activity>(activity));
        this.queue.add(task);
    }

    @Override
    public void screenNavigated(String screenName) {
        this.screenNavigated(screenName, null);
    }

    @Override
    public void screenNavigated(String screenName, Map<String, ? extends Object> screenData) {
        Task task = new AnalyticsTask(AnalyticsTask.SCREEN_NAVIGATED, screenName, screenData);
        this.queue.add(task);
    }

    @Override
    public void setScreenData(Map<String, ? extends Object> screenData) {
        Task task = new AnalyticsTask(AnalyticsTask.SCREEN_DATA, screenData);
        this.queue.add(task);
    }

    @Override
    public void installed(Intent intent) {
        Task task = new AnalyticsTask(AnalyticsTask.INSTALLED, intent);
        this.queue.add(task);
    }

    @Override
    public WeakReference<Activity> getActivity() {
        return null;
    }

    @Override
    protected SessionManager getSessionManager() {
        //not need to return queued impl of session manager since it will be never be called before webengage's initialization or from any other queued impls
        return null;
    }

    @Override
    protected Scheduler getScheduler() {
        //not need to return queued impl of scheduler since it will be never be called before webengage's initialization or from any other queued impls
        return null;
    }

    @Override
    protected AnalyticsPreferenceManager getPreferenceManager() {
        //not need to return queued impl of AnalyticsPreferenceManager since it will be never be called before webengage's initialization or from any other queued impls
        return null;
    }

    @Override
    protected void dispatchEventTopic(Object data) {
       // no implementation needed here since it will be never called before webengage's initialization or from any other queued impls.
    }

    @Override
    protected void dispatchExceptionTopic(Object data) {
        // no implementation needed here since it will be never called before webengage's initialization or from any other queued impls.
    }
}
