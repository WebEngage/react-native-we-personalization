package com.webengage.sdk.android.callbacks;


import android.content.Context;

import com.webengage.sdk.android.actions.render.PushNotificationData;

public interface PushNotificationCallbacks {
    PushNotificationData onPushNotificationReceived(Context context, PushNotificationData pushNotificationData);

    void onPushNotificationShown(Context context, final PushNotificationData pushNotificationData);

    boolean onPushNotificationClicked(Context context, final PushNotificationData pushNotificationData);

    void onPushNotificationDismissed(Context context, final PushNotificationData pushNotificationData);

    boolean onPushNotificationActionClicked(Context context, final PushNotificationData pushNotificationData, final String buttonId);
}
