package com.webengage.sdk.android.actions.render;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.webengage.sdk.android.NotificationClickHandlerService;
import com.webengage.sdk.android.WETransparentActivity;
import com.webengage.sdk.android.WebEngageReceiver;
import com.webengage.sdk.android.utils.WebEngageConstant;

public class PushPendingIntent {
    private final Context context;
    private final PushNotificationData pushNotificationData;
    private final CallToAction callToAction;
    private final String requestCodePrefix;
    private final String eventName;
    private final Bundle eventData;
    private final Bundle extraData;
    private final boolean shouldDismissOnClick;
    private final boolean launchAppIfInvalid;
    private final boolean shouldRerender;

    private PushPendingIntent(Builder builder) {
        this.context = builder.context;
        this.pushNotificationData = builder.pushNotificationData;
        this.callToAction = builder.callToAction;
        this.requestCodePrefix = builder.requestCodePrefix;
        this.eventName = builder.eventName;
        this.eventData = builder.eventData;
        this.extraData = builder.extraData;
        this.shouldDismissOnClick = builder.shouldDismissOnClick;
        this.launchAppIfInvalid = builder.launchAppIfInvalid;
        this.shouldRerender = builder.shouldRerender;
    }

    /**
     * From android 12,
     * WeTransparent activity will used for deeplink CTA actions for the Push Notifications
     * and InApp push Trampoline support.
     * For pre Android12, we will continue using NotificationClickHandlerService Service.
     * For ReRendering notifications like Carousel and Rating, SDK will continue using the NotificationClickHandlerService.
     **/
    private PendingIntent build() {
        Bundle ctaBundle = new Bundle();
        Intent ctaIntent = null;
        boolean isActivity = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (shouldRerender) {
                ctaIntent = new Intent(context.getApplicationContext(), NotificationClickHandlerService.class);
                ctaBundle.putString(WebEngageReceiver.ACTION, NotificationClickHandlerService.PUSH_RERENDER);
            } else {
                ctaIntent = new Intent(context.getApplicationContext(), WETransparentActivity.class);
                ctaBundle.putString(WebEngageReceiver.ACTION, NotificationClickHandlerService.DEEPLINK_ACTION);
                isActivity = true;
            }
        } else {
            ctaIntent = new Intent(context.getApplicationContext(), NotificationClickHandlerService.class);

            if (shouldRerender) {
                ctaBundle.putString(WebEngageReceiver.ACTION, NotificationClickHandlerService.PUSH_RERENDER);
            } else {
                ctaBundle.putString(WebEngageReceiver.ACTION, NotificationClickHandlerService.DEEPLINK_ACTION);
            }
        }
        ctaIntent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);

        if (this.eventName != null) {
            ctaBundle.putString(WebEngageConstant.EVENT, this.eventName);
        }

        ctaBundle.putBoolean(WebEngageConstant.DISMISS_ON_CLICK, shouldDismissOnClick);
        ctaBundle.putBoolean(WebEngageConstant.LAUNCH_APP_IF_INVALID, launchAppIfInvalid);
        ctaBundle.putString(WebEngageConstant.NOTIFICATION_ID, pushNotificationData.getVariationId());
        ctaBundle.putString(WebEngageConstant.EXPERIMENT_ID, pushNotificationData.getExperimentId());
        ctaBundle.putInt(WebEngageConstant.HASHED_NOTIFICATION_ID, pushNotificationData.getVariationId().hashCode());

        if (pushNotificationData.getCustomData() != null) {
            ctaBundle.putBundle(WebEngageConstant.CUSTOM_DATA, pushNotificationData.getCustomData());
        }

        if (this.eventData != null) {
            ctaBundle.putBundle(WebEngageConstant.EVENT_DATA, this.eventData);
        }

        if (this.extraData != null) {
            ctaBundle.putBundle(WebEngageConstant.EXTRA_DATA, this.extraData);
        }

        if (callToAction != null) {
            if (callToAction.isPrimeAction()) {
                ctaBundle.putBoolean(WebEngageConstant.NOTIFICATION_MAIN_INTENT, true);
            } else {
                ctaBundle.putBoolean(WebEngageConstant.NOTIFICATION_MAIN_INTENT, false);
            }

            if (callToAction.getId() != null) {
                ctaBundle.putString(WebEngageConstant.CTA_ID, callToAction.getId());
            }

            ctaBundle.putString(WebEngageConstant.URI, callToAction.getFullActionUri());
        }

        int requestCode;
        if (requestCodePrefix == null) {
            if (callToAction != null && !callToAction.isPrimeAction() && callToAction.getId() != null) {
                requestCode = callToAction.getId().hashCode();
            } else {
                requestCode = pushNotificationData.getVariationId().hashCode();
            }
        } else {
            requestCode = (requestCodePrefix + pushNotificationData.getVariationId()).hashCode();
        }

        ctaIntent.putExtras(ctaBundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isActivity)
                return PendingIntent.getActivity(context.getApplicationContext(), requestCode, ctaIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            else
                return PendingIntent.getService(context.getApplicationContext(), requestCode, ctaIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        } else {
            return PendingIntent.getService(context.getApplicationContext(), requestCode, ctaIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    public static final class Builder {
        private final Context context;
        private final PushNotificationData pushNotificationData;
        private final CallToAction callToAction;
        private String requestCodePrefix = null;
        private String eventName = null;
        private Bundle eventData = null;
        private Bundle extraData = null;
        private boolean shouldDismissOnClick = true;
        private boolean launchAppIfInvalid = true;
        private boolean shouldRerender = false;

        public Builder(Context context, PushNotificationData pushNotificationData, CallToAction callToAction) {
            this.context = context;
            this.pushNotificationData = pushNotificationData;
            this.callToAction = callToAction;
        }

        public Builder(Context context, PushNotificationData pushNotificationData, String requestCodePrefix) {
            this.context = context;
            this.pushNotificationData = pushNotificationData;
            this.callToAction = null;
            this.requestCodePrefix = requestCodePrefix;
        }

        public Builder setEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder setEventData(Bundle eventData) {
            this.eventData = eventData;
            return this;
        }

        public Builder setSilentData(Bundle silentData) {
            this.extraData = silentData;
            return this;
        }

        public Builder shouldDismissOnClick(boolean shouldDismissOnClick) {
            this.shouldDismissOnClick = shouldDismissOnClick;
            return this;
        }

        public Builder launchAppIfInvalid(boolean launchAppIfInvalid) {
            this.launchAppIfInvalid = launchAppIfInvalid;
            return this;
        }

        public Builder shouldRerender(boolean shouldRerender) {
            this.shouldRerender = shouldRerender;
            return this;
        }

        public Builder setRequestCodePrefix(String requestCodePrefix) {
            this.requestCodePrefix = requestCodePrefix;
            return this;
        }

        public PendingIntent build() {
            return (new PushPendingIntent(this)).build();
        }
    }
}
