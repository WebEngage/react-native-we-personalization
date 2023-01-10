package com.webengage.sdk.android;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.webengage.sdk.android.actions.database.ReportingStrategy;
import com.webengage.sdk.android.actions.rules.RuleExecutor;
import com.webengage.sdk.android.actions.rules.RuleExecutorFactory;
import com.webengage.sdk.android.utils.Provider;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by shahrukhimam on 04/10/17.
 */

class QueuedWebEngageImpl extends AbstractWebEngage {

    private WebEngageConfig webEngageConfig = null;
    private Queue<Task> queue = null;

    QueuedWebEngageImpl(WebEngageConfig webEngageConfig) {
        this.webEngageConfig = webEngageConfig;
        this.queue = new LinkedList<Task>();
    }

    protected Queue<Task> getQueue() {
        return this.queue;
    }

    @Override
    public WebEngageConfig getWebEngageConfig() {
        return this.webEngageConfig;
    }

    @Override
    public void setLocationTracking(boolean state) {
        Task task = new WebEngageTask(WebEngageTask.LOCATION_TRACKING, state);
        this.queue.add(task);
    }

    @Override
    public void setLocationTrackingStrategy(LocationTrackingStrategy locationTrackingStrategy) {
        Task task = new WebEngageTask(WebEngageTask.LOCATION_TRACKING_STRATEGY, locationTrackingStrategy);
        this.queue.add(task);
    }

    @Override
    public void setLogLevel(int logLevel) {
        Task task = new WebEngageTask(WebEngageTask.LOG_LEVEL, logLevel);
        this.queue.add(task);
    }

    @Override
    public void setEventReportingStrategy(ReportingStrategy reportingStrategy) {
        Task task = new WebEngageTask(WebEngageTask.REPORTING_STRATEGY, reportingStrategy);
        this.queue.add(task);
    }

    @Override
    public void setRegistrationID(String registrationID, String projectNumber) {
        Task task = new WebEngageTask(WebEngageTask.SET_REGISTRATION_ID, registrationID, projectNumber, Provider.FCM.name());
        this.queue.add(task);
    }

    @Override
    public void setRegistrationID(String registrationID) {
        this.setRegistrationID(registrationID, null);
    }

    @Override
    public void setXiaomiRegistrationID(String registrationID) {
        Task task = new WebEngageTask(WebEngageTask.SET_REGISTRATION_ID, registrationID, null, Provider.MI.name());
        this.queue.add(task);
    }

    @Override
    public void setHuaweiRegistrationID(String registrationID) {
        Task task = new WebEngageTask(WebEngageTask.SET_REGISTRATION_ID, registrationID, null, Provider.HW.name());
        this.queue.add(task);
    }

    @Override
    public void receive(Intent intent) {
        if (intent == null) {
            Logger.e(WebEngageConstant.TAG, "Intent is null");
            return;
        }
        dispatchGCMMessage(intent);
    }

    @Override
    public void receive(Bundle data) {
        if (data == null) {
            Logger.e(WebEngageConstant.TAG, "Push bundle is null");
        } else {
            Intent intent = new Intent();
            intent.putExtras(data);
            this.receive(intent);
        }
    }

    @Override
    public void receive(Map<String, String> data) {
        if (data == null) {
            Logger.e(WebEngageConstant.TAG, "Push data is null");
        } else {
            Bundle bundle = WebEngageUtils.convertMapToBundle(data);
            if (bundle != null) {
                this.receive(bundle);
            }
        }
    }

    @Override
    public void setEveryActivityIsScreen(boolean everyActivityIsScreen) {
        Task task = new WebEngageTask(WebEngageTask.SET_EVERY_ACTIVITY_IS_SCREEN, everyActivityIsScreen);
        this.queue.add(task);
    }

    @Override
    public void filterCustomEvents(boolean filterCustomEvents) {
        Task task = new WebEngageTask(WebEngageTask.FILTER_CUSTOM_EVENT, filterCustomEvents);
        this.queue.add(task);
    }

    @Override
    public Analytics analytics() {
        return AnalyticsFactory.getQueuedImpl(this.queue);
    }

    @Override
    public User user() {
        return UserFactory.getQueuedImpl(this.queue);
    }

    @Override
    public RuleExecutor ruleExecutor() {
        return RuleExecutorFactory.getRuleExecutor();
    }

    @Override
    protected void dispatchGCMMessage(Intent intent) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_GCM_MESSAGE, intent);
        this.queue.add(task);
    }

    @Override
    protected void dispatchDeeplinkIntent(Intent intent, BroadcastReceiver broadcastReceiver) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_DEEPLINK_ACTION, intent, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchPushNotificationRerender(Intent intent) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_PUSH_RERENDER, intent, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchLocation(Location location, BroadcastReceiver broadcastReceiver) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_LOCATION, location, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchSessionDestroy(BroadcastReceiver broadcastReceiver) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_SESSION_DESTROY, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchFlushAction(BroadcastReceiver broadcastReceiver) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_FLUSH_ACTION, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchConfigRefreshPing(BroadcastReceiver broadcastReceiver) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_CONFIG_REFRESH, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchSessionDelay(Intent intent, BroadcastReceiver broadcastReceiver) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_SESSION_DELAY, intent, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchPageDelay(Intent intent, BroadcastReceiver broadcastReceiver) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_PAGE_DELAY, intent, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchLeaveIntent(Intent intent) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_LEAVE_INTENT, intent);
        this.queue.add(task);
    }

    @Override
    protected void dispatchGeoFenceTransition(LocationManagerImpl.GeoFenceTransition geoFenceTransition, BroadcastReceiver broadcastReceiver) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_GEOFENCE, geoFenceTransition, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchUserProfileFetchCall(BroadcastReceiver broadcastReceiver) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_USER_PROFILE, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchJourneyContext(BroadcastReceiver broadcastReceiver) {
        Task task = new WebEngageTask(WebEngageTask.DISPATCH_JOURNEY_CONTEXT, null);
        this.queue.add(task);
    }

    @Override
    protected void dispatchAmplify(BroadcastReceiver broadcastReceiver) {

    }

}
