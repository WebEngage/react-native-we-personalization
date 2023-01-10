package com.webengage.sdk.android;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.webengage.sdk.android.actions.database.ReportingStrategy;
import com.webengage.sdk.android.actions.rules.RuleExecutor;

import java.util.Map;

public abstract class AbstractWebEngage {

    public abstract WebEngageConfig getWebEngageConfig();

    @Deprecated
    public abstract void setLocationTracking(boolean state);

    public abstract void setLocationTrackingStrategy(LocationTrackingStrategy locationTrackingStrategy);

    public abstract void setLogLevel(int logLevel);

    public abstract void setEventReportingStrategy(ReportingStrategy reportingStrategy);

    @Deprecated
    public abstract void setRegistrationID(String registrationID, String projectNumber);

    public abstract void setRegistrationID(String registrationID);

    public abstract void setXiaomiRegistrationID(String registrationID);

    public abstract void setHuaweiRegistrationID(String registrationID);

    public abstract void receive(Intent intent);

    public abstract void receive(Bundle data);

    public abstract void receive(Map<String, String> data);

    public abstract void setEveryActivityIsScreen(boolean everyActivityIsScreen);

    public abstract void filterCustomEvents(boolean filterCustomEvents);

    public abstract Analytics analytics();

    public abstract User user();

    public abstract RuleExecutor ruleExecutor();

    protected abstract void dispatchGCMMessage(Intent intent);

    protected abstract void dispatchDeeplinkIntent(Intent intent, BroadcastReceiver broadcastReceiver);

    protected abstract void dispatchPushNotificationRerender(Intent intent);

    protected abstract void dispatchLocation(Location location, BroadcastReceiver broadcastReceiver);

    protected abstract void dispatchSessionDestroy(BroadcastReceiver broadcastReceiver);

    protected abstract void dispatchFlushAction(BroadcastReceiver broadcastReceiver);

    protected abstract void dispatchConfigRefreshPing(BroadcastReceiver broadcastReceiver);

    protected abstract void dispatchSessionDelay(Intent intent, BroadcastReceiver broadcastReceiver);

    protected abstract void dispatchPageDelay(Intent intent, BroadcastReceiver broadcastReceiver);

    protected abstract void dispatchLeaveIntent(Intent intent);

    protected abstract void dispatchGeoFenceTransition(LocationManagerImpl.GeoFenceTransition geoFenceTransition, BroadcastReceiver broadcastReceiver);

    protected abstract void dispatchUserProfileFetchCall(BroadcastReceiver broadcastReceiver);

    protected abstract void dispatchJourneyContext(BroadcastReceiver broadcastReceiver);

    protected abstract void dispatchAmplify(BroadcastReceiver broadcastReceiver);
}
