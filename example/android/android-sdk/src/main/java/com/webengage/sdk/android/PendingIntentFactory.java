package com.webengage.sdk.android;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.webengage.sdk.android.actions.render.CallToAction;
import com.webengage.sdk.android.actions.render.PushNotificationData;
import com.webengage.sdk.android.actions.render.PushPendingIntent;
import com.webengage.sdk.android.utils.WebEngageConstant;

public class PendingIntentFactory {
    static PendingIntent constructLeaveIntentPendingIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        intent.putExtra(WebEngageReceiver.ACTION, WebEngageReceiver.LEAVE_INTENT_EVENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(context.getApplicationContext(), EventName.WE_WK_LEAVE_INTENT.toString().hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return PendingIntent.getBroadcast(context.getApplicationContext(), EventName.WE_WK_LEAVE_INTENT.toString().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    static PendingIntent constructSessionDestroyPendingIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        intent.putExtra(WebEngageReceiver.ACTION, WebEngageReceiver.SESSION_DESTROY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(context.getApplicationContext(), "sessionDestroy".hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            return PendingIntent.getBroadcast(context.getApplicationContext(), "sessionDestroy".hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    static PendingIntent constructNextSyncPendingIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        intent.putExtra(WebEngageReceiver.ACTION, WebEngageReceiver.SYNC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(context.getApplicationContext(), "next_sync".hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            return PendingIntent.getBroadcast(context.getApplicationContext(), "next_sync".hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    static PendingIntent constructConfigRefreshPendingIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        intent.putExtra(WebEngageReceiver.ACTION, WebEngageReceiver.CONFIG_FETCH);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.CONFIG_FETCH.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.CONFIG_FETCH.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    static PendingIntent constructUserProfileFetchPendingIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        intent.putExtra(WebEngageReceiver.ACTION, WebEngageReceiver.USER_PROFILE);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.USER_PROFILE.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.USER_PROFILE.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    static PendingIntent constructJourneyContextPendingIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        intent.putExtra(WebEngageReceiver.ACTION, WebEngageReceiver.JOURNEY_CONTEXT);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.JOURNEY_CONTEXT.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.JOURNEY_CONTEXT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    static PendingIntent constructSessionDelayPendingIntent(long requestId, Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        intent.putExtra(WebEngageReceiver.ACTION, WebEngageReceiver.SESSION_DELAY_EVENT);
        intent.putExtra(WebEngageReceiver.DELAY_VALUE, requestId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(context.getApplicationContext(), (EventName.WE_WK_SESSION_DELAY.toString() + requestId).hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            return PendingIntent.getBroadcast(context.getApplicationContext(), (EventName.WE_WK_SESSION_DELAY.toString() + requestId).hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    static PendingIntent constructPageDelayPendingIntent(long requestId, Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        intent.putExtra(WebEngageReceiver.ACTION, WebEngageReceiver.PAGE_DELAY_EVENT);
        intent.putExtra(WebEngageReceiver.DELAY_VALUE, requestId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(context.getApplicationContext(), (EventName.WE_WK_PAGE_DELAY.toString() + requestId).hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            return PendingIntent.getBroadcast(context.getApplicationContext(), (EventName.WE_WK_PAGE_DELAY.toString() + requestId).hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    static PendingIntent constructDelayPendingIntent(String eventName, long delay, Context context) {
        if (eventName.equals(EventName.WE_WK_PAGE_DELAY)) {
            return constructPageDelayPendingIntent(delay, context);
        } else if (eventName.equals(EventName.WE_WK_SESSION_DELAY)) {
            return constructSessionDelayPendingIntent(delay, context);
        }
        return null;
    }

    static PendingIntent constructLocationPendingIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.LOCATION.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.LOCATION.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    static boolean doesLocationPendingIntentExists(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.LOCATION.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
        }
        else{
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.LOCATION.hashCode(), intent, PendingIntent.FLAG_NO_CREATE);
        }
        return pendingIntent != null;
    }

    static PendingIntent constructGeoFencePendingIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.GEOFENCE.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else {
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), WebEngageReceiver.GEOFENCE.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    public static PendingIntent constructRerenderPendingIntent(Context context, PushNotificationData pushNotificationData, String requestCodePrefix, Bundle extraData) {
        return new PushPendingIntent.Builder(context, pushNotificationData, requestCodePrefix)
                .setSilentData(extraData)
                .shouldRerender(true)
                .build();
    }

    public static PendingIntent constructCarouselBrowsePendingIntent(Context context, PushNotificationData pushNotificationData, int newIndex, String navigation, String requestCodePrefix, Bundle extraData) {
        pushNotificationData.setCurrentIndex(newIndex);
        if (extraData == null) {
            extraData = new Bundle();
        }
        extraData.putString(WebEngageConstant.NAVIGATION, navigation);
        extraData.putInt(WebEngageConstant.CURRENT, newIndex);
        extraData.putBoolean(WebEngageConstant.SHOULD_AUTOSCROLL, false);
        return constructRerenderPendingIntent(context, pushNotificationData, requestCodePrefix, extraData);
    }

    public static PendingIntent constructPushRatingSubmitPendingIntent(Context context, PushNotificationData pushNotificationData, int rateValue) {
        Bundle eventData = new Bundle();
        eventData.putInt(WebEngageConstant.RATE_VALUE, rateValue);
        return new PushPendingIntent.Builder(context, pushNotificationData, pushNotificationData.getRatingV1().getSubmitCTA())
                .setEventName(EventName.PUSH_NOTIFICATION_RATING_SUBMITTED)
                .setEventData(eventData)
                .setRequestCodePrefix("rating_v1_submit")
                .launchAppIfInvalid(false)
                .build();
    }

    public static PendingIntent constructPushClickPendingIntent(Context context, PushNotificationData pushNotificationData, CallToAction callToAction, boolean autoCancel) {
        return new PushPendingIntent.Builder(context, pushNotificationData, callToAction)
                .shouldDismissOnClick(autoCancel)
                .setEventName(EventName.PUSH_NOTIFICATION_CLICK)
                .build();
    }

    public static PendingIntent constructPushDeletePendingIntent(Context context, PushNotificationData pushNotificationData) {
        return new PushPendingIntent.Builder(context, pushNotificationData, "notification_close")
                .build();
    }

    static PendingIntent constructPushAmplifyPendingIntent(Context context, int reqId, int flag) {
        Intent intent = new Intent(context.getApplicationContext(), WebEngageReceiver.class);
        intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
        intent.putExtra(WebEngageReceiver.ACTION, WebEngageReceiver.AMPLIFY);
        return PendingIntent.getBroadcast(context, reqId, intent, flag);
    }
}
