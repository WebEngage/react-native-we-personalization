package com.webengage.sdk.android;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.webengage.sdk.android.actions.database.ReportingStrategy;
import com.webengage.sdk.android.actions.rules.RuleExecutor;
import com.webengage.sdk.android.actions.rules.RuleExecutorFactory;

import java.util.Map;

class WebEngageNoOpImpl extends AbstractWebEngage {

    WebEngageConfig webEngageConfig;

    WebEngageNoOpImpl(WebEngageConfig webEngageConfig) {
        this.webEngageConfig = webEngageConfig;
    }

    WebEngageNoOpImpl() {
        this.webEngageConfig = new WebEngageConfig.Builder().build();
    }

    @Override
    public WebEngageConfig getWebEngageConfig() {
        return this.webEngageConfig;
    }

    @Override
    public void setLocationTracking(boolean state) {

    }

    @Override
    public void setLocationTrackingStrategy(LocationTrackingStrategy locationTrackingStrategy) {

    }

    @Override
    public void setLogLevel(int logLevel) {

    }

    @Override
    public void setEventReportingStrategy(ReportingStrategy reportingStrategy) {

    }

    @Override
    public void filterCustomEvents(boolean filterCustomEvents) {

    }

    @Override
    public void setRegistrationID(String registrationID, String projectNumber) {

    }

    @Override
    public void setRegistrationID(String registrationID) {

    }

    @Override
    public void setXiaomiRegistrationID(String registrationID) {

    }

    @Override
    public void setHuaweiRegistrationID(String registrationID) {

    }

    @Override
    public void receive(Intent intent) {

    }

    @Override
    public void receive(Bundle data) {

    }

    @Override
    public void receive(Map<String, String> data) {

    }

    @Override
    public void setEveryActivityIsScreen(boolean everyActivityIsScreen) {

    }

    @Override
    public Analytics analytics() {
        return AnalyticsFactory.getNoOpAnalytics();
    }

    @Override
    public User user() {
        return UserFactory.getNoOpUser();
    }

    @Override
    public RuleExecutor ruleExecutor() {
        return RuleExecutorFactory.getNoOpRuleExecutor();
    }

    @Override
    protected void dispatchGCMMessage(Intent intent) {

    }

    @Override
    protected void dispatchDeeplinkIntent(Intent intent, BroadcastReceiver broadcastReceiver) {

    }

    @Override
    protected void dispatchLocation(Location location, BroadcastReceiver broadcastReceiver) {

    }

    @Override
    protected void dispatchSessionDestroy(BroadcastReceiver broadcastReceiver) {

    }

    @Override
    protected void dispatchFlushAction(BroadcastReceiver broadcastReceiver) {

    }

    @Override
    protected void dispatchConfigRefreshPing(BroadcastReceiver broadcastReceiver) {

    }

    @Override
    protected void dispatchSessionDelay(Intent intent, BroadcastReceiver broadcastReceiver) {

    }

    @Override
    protected void dispatchPageDelay(Intent intent, BroadcastReceiver broadcastReceiver) {

    }

    @Override
    protected void dispatchLeaveIntent(Intent intent) {

    }

    @Override
    protected void dispatchUserProfileFetchCall(BroadcastReceiver broadcastReceiver) {

    }

    @Override
    protected void dispatchJourneyContext(BroadcastReceiver broadcastReceiver) {

    }

    @Override
    protected void dispatchGeoFenceTransition(LocationManagerImpl.GeoFenceTransition geoFenceTransition, BroadcastReceiver broadcastReceiver) {

    }

    @Override
    protected void dispatchPushNotificationRerender(Intent intent) {

    }

    @Override
    protected void dispatchAmplify(BroadcastReceiver broadcastReceiver) {

    }
}
