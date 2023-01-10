package com.webengage.sdk.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.List;


public class WebEngageReceiver extends BroadcastReceiver {
    public static final String WEBENGAGE_ACTION = "com.webengage.sdk.android.intent.ACTION";
    public static final String ACTION = "action";
    public static final String LOCATION = "WebEngageLocation";
    public static final String GEOFENCE = "WebEngageGeofence";
    public static final String SESSION_DESTROY = "session_destroy";
    public static final String SYNC = "sync";
    public static final String CONFIG_FETCH = "config_refresh";
    public static final String SESSION_DELAY_EVENT = "session_delay_event";
    public static final String PAGE_DELAY_EVENT = "page_delay_event";
    public static final String DELAY_VALUE = "delay_value";
    public static final String LEAVE_INTENT_EVENT = "leave_intent_event";
    public static final String USER_PROFILE = "user_profile";
    public static final String JOURNEY_CONTEXT = "journey_context";
    public static final String AMPLIFY = "amplify";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (WebEngagePushReceiver.GCM_MESSAGE_ACTION.equals(action)) {
                Logger.w(WebEngageConstant.TAG, "GCM/FCM message received in WebEngageReceiver, Please fix your integration");
            } else if (WEBENGAGE_ACTION.equals(action)) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    if (WebEngage.get().getWebEngageConfig().isLocationTrackingEnabled()) {
                        Location location = LocationManagerFactory.getLocationManager(context).parseLocation(intent);
                        if (location != null) {
                            WebEngage.get().dispatchLocation(location, this);
                            Logger.d(WebEngageConstant.TAG, "UserUpdateGeoInfo: " + location);
                        }
                        List<LocationManagerImpl.GeoFenceTransition> geoFenceTransitions = LocationManagerFactory.getLocationManager(context).detectGeoFenceTransition(intent);
                        if (geoFenceTransitions != null) {
                            for (LocationManagerImpl.GeoFenceTransition geoFenceTransition : geoFenceTransitions) {
                                WebEngage.get().dispatchGeoFenceTransition(geoFenceTransition, this);
                                Logger.d(WebEngageConstant.TAG, geoFenceTransition.toString());
                            }
                        }
                    }
                    String task = extras.getString(ACTION);
                    if (SESSION_DESTROY.equals(task)) {
                        WebEngage.get().dispatchSessionDestroy(this);
                    } else if (SYNC.equals(task)) {
                        WebEngage.get().dispatchFlushAction(this);
                    } else if (CONFIG_FETCH.equals(task)) {
                        WebEngage.get().dispatchConfigRefreshPing(this);
                    } else if (SESSION_DELAY_EVENT.equals(task)) {
                        WebEngage.get().dispatchSessionDelay(intent, this);
                    } else if (PAGE_DELAY_EVENT.equals(task)) {
                        WebEngage.get().dispatchPageDelay(intent, this);
                    } else if (LEAVE_INTENT_EVENT.equals(task)) {
                        WebEngage.get().dispatchLeaveIntent(intent);
                    } else if (USER_PROFILE.equals(task)) {
                        WebEngage.get().dispatchUserProfileFetchCall(this);
                    } else if (JOURNEY_CONTEXT.equals(task)) {
                        WebEngage.get().dispatchJourneyContext(this);
                    } else if (AMPLIFY.equals(task)) {
                        WebEngage.get().dispatchAmplify(this);
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, e.toString());
        }
    }
}
