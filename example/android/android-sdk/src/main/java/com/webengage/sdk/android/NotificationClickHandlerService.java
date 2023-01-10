package com.webengage.sdk.android;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.webengage.sdk.android.utils.WebEngageConstant;

/**
 * NotificationClickHandlerService is added as a solution to a bug in Oxygen OS (found in OnePlus, Coolpad devices)
 * which did not allowed to launch the application if PendingIntent in push notification targets BroadcastReceiver.
 */
public class NotificationClickHandlerService extends Service {
    public static final String DEEPLINK_ACTION = "WebEngageDeeplink";
    public static final String CAROUSEL_BROWSED = "carousel_browsed";
    public static final String RATING_STAR_CLICKED = "rating_star_clicked";
    public static final String PUSH_RERENDER = "push_rerender";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            WebEngage.get();
            String action = intent.getAction();
            Logger.d(WebEngageConstant.TAG, "NotificationClickHandlerService received intent with action : " + action);
            if (WebEngageReceiver.WEBENGAGE_ACTION.equals(action)) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String task = extras.getString(WebEngageReceiver.ACTION);
                    Logger.d(WebEngageConstant.TAG, "NotificationClickHandlerService received intent with task : " + task);
                    if (DEEPLINK_ACTION.equals(task)) {
                        WebEngage.get().dispatchDeeplinkIntent(intent, null);
//                    } else if (CAROUSEL_BROWSED.equals(task)) {
//                        WebEngage.get().dispatchPushNotificationBrowsed(intent, null);
//                    } else if (RATING_STAR_CLICKED.equals(task)) {
//                        WebEngage.get().dispatchRatingStarClicked(intent, null);
                    } else if (PUSH_RERENDER.equals(task)) {
                        WebEngage.get().dispatchPushNotificationRerender(intent);
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Exception while executing push click", e);
        }
        stopSelf(startId);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
