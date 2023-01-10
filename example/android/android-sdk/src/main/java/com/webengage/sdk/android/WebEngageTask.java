package com.webengage.sdk.android;

import android.content.Intent;
import android.location.Location;

import com.webengage.sdk.android.actions.database.ReportingStrategy;
import com.webengage.sdk.android.utils.Provider;
import com.webengage.sdk.android.utils.WebEngageConstant;

class WebEngageTask implements Task<AbstractWebEngage> {

    private Object[] args;
    private int task = -1;
    protected static final int LOCATION_TRACKING = 0;
    protected static final int LOG_LEVEL = 1;
    protected static final int REPORTING_STRATEGY = 2;
    protected static final int FILTER_CUSTOM_EVENT = 3;
    protected static final int SET_EVERY_ACTIVITY_IS_SCREEN = 4;
    protected static final int SET_REGISTRATION_ID = 5;
    protected static final int DISPATCH_GCM_MESSAGE = 6;
    protected static final int DISPATCH_DEEPLINK_ACTION = 7;
    protected static final int DISPATCH_LOCATION = 8;
    protected static final int DISPATCH_SESSION_DESTROY = 9;
    protected static final int DISPATCH_FLUSH_ACTION = 10;
    protected static final int DISPATCH_CONFIG_REFRESH = 11;
    protected static final int DISPATCH_SESSION_DELAY = 12;
    protected static final int DISPATCH_PAGE_DELAY = 13;
    protected static final int DISPATCH_LEAVE_INTENT = 14;
    protected static final int DISPATCH_GEOFENCE = 15;
    protected static final int DISPATCH_USER_PROFILE = 16;
    protected static final int DISPATCH_PUSH_RERENDER = 17;
    protected static final int LOCATION_TRACKING_STRATEGY = 19;
    protected static final int DISPATCH_JOURNEY_CONTEXT = 20;

    WebEngageTask(int task, Object... args) {
        this.args = args;
        this.task = task;
    }

    @Override
    public void execute(AbstractWebEngage webEngage) {
        try {
            switch (this.task) {
                case LOCATION_TRACKING:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        webEngage.setLocationTracking((boolean) this.args[0]);
                    }
                    break;

                case LOCATION_TRACKING_STRATEGY:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null && this.args[0] instanceof LocationTrackingStrategy) {
                        webEngage.setLocationTrackingStrategy((LocationTrackingStrategy) this.args[0]);
                    }
                    break;

                case LOG_LEVEL:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        webEngage.setLogLevel((int) this.args[0]);
                    }
                    break;

                case REPORTING_STRATEGY:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        ReportingStrategy reportingStrategy = (ReportingStrategy) this.args[0];
                        webEngage.setEventReportingStrategy(reportingStrategy);
                    }
                    break;

                case FILTER_CUSTOM_EVENT:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        webEngage.filterCustomEvents((boolean) this.args[0]);
                    }
                    break;

                case SET_EVERY_ACTIVITY_IS_SCREEN:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        webEngage.setEveryActivityIsScreen((boolean) this.args[0]);
                    }
                    break;

                case SET_REGISTRATION_ID:
                    String registrationID = null;
                    String gcmProjectNumber = null;
                    String provider = null;
                    if (this.args.length > 0) {
                        registrationID = (String) this.args[0];
                    }
                    if (this.args.length > 1) {
                        gcmProjectNumber = (String) this.args[1];
                    }

                    if (this.args.length > 2) {
                        provider = (String) this.args[2];
                    }
                    Logger.d(WebEngageConstant.TAG, "Inside SET_REGISTRATION_ID with provider: " + provider + " token: " + registrationID);
                    if (Provider.FCM.name().equalsIgnoreCase(provider)) {
                        webEngage.setRegistrationID(registrationID);
                    } else if (Provider.MI.name().equalsIgnoreCase(provider)) {
                        webEngage.setXiaomiRegistrationID(registrationID);
                    } else if (Provider.HW.name().equalsIgnoreCase(provider)) {
                        webEngage.setHuaweiRegistrationID(registrationID);
                    }
                    break;

                case DISPATCH_GCM_MESSAGE:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        Intent intent = (Intent) this.args[0];
                        webEngage.dispatchGCMMessage(intent);
                    }
                    break;

                case DISPATCH_DEEPLINK_ACTION:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        Intent intent = (Intent) this.args[0];
                        webEngage.dispatchDeeplinkIntent(intent, null);
                    }
                    break;

                case DISPATCH_LOCATION:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        Location location = (Location) this.args[0];
                        webEngage.dispatchLocation(location, null);
                    }
                    break;

                case DISPATCH_SESSION_DESTROY:
                    webEngage.dispatchSessionDestroy(null);
                    break;

                case DISPATCH_FLUSH_ACTION:
                    webEngage.dispatchFlushAction(null);
                    break;

                case DISPATCH_CONFIG_REFRESH:
                    webEngage.dispatchConfigRefreshPing(null);
                    break;

                case DISPATCH_SESSION_DELAY:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        Intent intent = (Intent) this.args[0];
                        webEngage.dispatchSessionDelay(intent, null);
                    }
                    break;

                case DISPATCH_PAGE_DELAY:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        Intent intent = (Intent) this.args[0];
                        webEngage.dispatchPageDelay(intent, null);
                    }
                    break;

                case DISPATCH_LEAVE_INTENT:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        Intent intent = (Intent) this.args[0];
                        webEngage.dispatchLeaveIntent(intent);
                    }
                    break;

                case DISPATCH_GEOFENCE:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        LocationManagerImpl.GeoFenceTransition geoFenceTransition = (LocationManagerImpl.GeoFenceTransition) this.args[0];
                        webEngage.dispatchGeoFenceTransition(geoFenceTransition, null);
                    }
                    break;

                case DISPATCH_USER_PROFILE:
                    webEngage.dispatchUserProfileFetchCall(null);
                    break;

                case DISPATCH_JOURNEY_CONTEXT:
                    webEngage.dispatchJourneyContext(null);
                    break;

                case DISPATCH_PUSH_RERENDER:
                    if (this.args != null && this.args.length > 0 && this.args[0] != null) {
                        Intent intent = (Intent) this.args[0];
                        webEngage.dispatchPushNotificationRerender(intent);
                    }
                    break;
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Some error occurred while executing queued task of WebEngage: " + e.toString());
        }
    }
}
