package com.webengage.sdk.android.callbacks;


import android.content.Context;

import com.webengage.sdk.android.actions.render.InAppNotificationData;

public interface InAppNotificationCallbacks {

    InAppNotificationData onInAppNotificationPrepared(Context context, InAppNotificationData inAppNotificationData);

    void onInAppNotificationShown(Context context, InAppNotificationData inAppNotificationData);

    boolean onInAppNotificationClicked(Context context, InAppNotificationData inAppNotificationData , String actionId);

    void onInAppNotificationDismissed(Context context, InAppNotificationData inAppNotificationData);
}
