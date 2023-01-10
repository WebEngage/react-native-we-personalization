package com.webengage.sdk.android;

import static com.webengage.sdk.android.NotificationClickHandlerService.DEEPLINK_ACTION;
import static com.webengage.sdk.android.NotificationClickHandlerService.PUSH_RERENDER;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.webengage.sdk.android.utils.WebEngageConstant;

public class WETransparentActivity extends Activity {

    //For Android12 and up, Activities cannot be launched through Notifications via services or broadcast receivers. WETransparent activity will be used for managing pendingintents related to DeepLinks for devices using Android12 and up.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            WebEngage.get();
            Intent intent = getIntent();
            String action = intent.getAction();
            Logger.d(WebEngageConstant.TAG, "WETransparentActivity received intent with action : " + action);
            if (WebEngageReceiver.WEBENGAGE_ACTION.equals(action)) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String task = extras.getString(WebEngageReceiver.ACTION);
                    Logger.d(WebEngageConstant.TAG, "WETransparentActivity received intent with task : " + task);
                    if (DEEPLINK_ACTION.equals(task)) {
                        WebEngage.get().dispatchDeeplinkIntent(intent, null);
                    } else if (PUSH_RERENDER.equals(task)) {
                        WebEngage.get().dispatchPushNotificationRerender(intent);
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Exception while executing push click", e);
        } finally {
            finish();
        }
    }
}
