package com.webengage.sdk.android;


import android.content.Intent;
import android.os.Bundle;

import com.webengage.sdk.android.actions.database.ReportingStatistics;
import com.webengage.sdk.android.utils.WebEngageConstant;

public class EventLogService extends YetAnotherIntentService {

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    Topic topic = (Topic) extras.get(WebEngageConstant.TOPIC);
                    if (topic != null) {
                        switch (topic) {
                            case SYNC_TO_SERVER:
                                Object data = extras.get(WebEngageConstant.DATA);
                                SubscriberManager.get(this.getApplicationContext()).callSubscribers(topic, data);
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            try {
                SubscriberManager.get(this.getApplicationContext()).callSubscribers(Topic.EXCEPTION, e);
            } catch (Exception e1) {
            }
        }
    }

    @Override
    public void onDestroy() {
        ReportingStatistics.setShouldReport(true);
        super.onDestroy();
    }
}
