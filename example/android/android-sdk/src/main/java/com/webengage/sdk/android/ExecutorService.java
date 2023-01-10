package com.webengage.sdk.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.ArrayList;
import java.util.HashMap;

public class ExecutorService extends YetAnotherIntentService {

    public static final String ACTION_NAME = "action_name";
    public static final int SESSION_DESTROY = 1;

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = this.getApplicationContext();
        try {
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    Topic topic = (Topic) extras.get(WebEngageConstant.TOPIC);
                    if (topic != null) {
                        switch (topic) {
                            case BOOT_UP:
                                SubscriberManager.get(context).callSubscribers(topic, null);
                                break;

                            case INTERNAL_EVENT:
                            case EVENT:
                                EventPayload eventPayload = (EventPayload) extras.getSerializable(WebEngageConstant.DATA);
                                SubscriberManager.get(context).callSubscribers(topic, eventPayload);
                                break;

                            case DATA:
                                HashMap<String, Object> data = (HashMap<String, Object>) extras.getSerializable(WebEngageConstant.DATA);
                                SubscriberManager.get(context).callSubscribers(topic, data);
                                break;

                            case GCM_MESSAGE:
                                Bundle bundle = extras.getBundle(WebEngageConstant.DATA);
                                SubscriberManager.get(context).callSubscribers(topic, bundle);
                                break;

                            case DEEPLINK:
                                Intent deeplinkIntent = extras.getParcelable(WebEngageConstant.DATA);
                                SubscriberManager.get(context).callSubscribers(topic, deeplinkIntent);
                                break;

                            case EXCEPTION:
                                try {
                                    Exception exception = (Exception) extras.getSerializable(WebEngageConstant.DATA);
                                    SubscriberManager.get(context).callSubscribers(topic, exception);
                                } catch (Exception e) {
                                }
                                break;

                            case RENDER:
                                SubscriberData subscriberData = (SubscriberData) extras.getSerializable(WebEngageConstant.DATA);
                                if (AnalyticsFactory.getAnalytics(this.getApplicationContext()).getPreferenceManager().getLUID().equals(subscriberData.getContextID())) {
                                   // ArrayList<String> ids = (ArrayList<String>) subscriberData.getData();
                                    SubscriberManager.get(context).callSubscribers(topic, subscriberData.getData());
                                }
                                break;

                            case CONFIG_REFRESH:
                                SubscriberManager.get(context).callSubscribers(topic, null);
                                break;

                            case RULE_EXECUTION:
                                ArrayList<WebEngageConstant.RuleCategory> list = (ArrayList<WebEngageConstant.RuleCategory>) extras.getSerializable(WebEngageConstant.DATA);
                                SubscriberManager.get(context).callSubscribers(topic, list);
                                break;

                            case FETCH_PROFILE:
                            case JOURNEY_CONTEXT:
                            case AMPLIFY:
                                SubscriberManager.get(context).callSubscribers(topic, null);
                                break;

                            case REPORT:
                                SubscriberManager.get(context).callSubscribers(topic, extras.get(WebEngageConstant.DATA));
                                break;
                        }
                    } else {
                        handleIntent(context, intent);
                    }
                }
            }
        } catch (Exception e) {
            try {
                e.printStackTrace();
                SubscriberManager.get(context).callSubscribers(Topic.EXCEPTION, e);
            } catch (Exception e1) {
            }
        }
    }

    private void handleIntent(Context context, Intent intent) throws Exception {
        Bundle extras = intent.getExtras();
        int action = extras.getInt(ACTION_NAME);
        switch (action) {
            case SESSION_DESTROY:
                Logger.d(WebEngageConstant.TAG, "Ending Background Task From Expiration Handler SESSION_DESTROY");
                Analytics analytics = AnalyticsFactory.getAnalytics(context);
                analytics.getSessionManager().postDestroySession();
                analytics.getSessionManager().postNewBackgroundSession();
                break;
        }
    }
}
