package com.webengage.sdk.android;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.ArrayList;
import java.util.HashMap;

public class IntentFactory {

    public static Intent newIntent(Topic topic, Object data, Context applicationContext) {
        Intent intent = new Intent();
        intent.putExtra(WebEngageConstant.TOPIC, topic);
        switch (topic) {
            case BOOT_UP:
                intent.setClass(applicationContext, ExecutorService.class);
                break;

            case EVENT:
                intent.putExtra(WebEngageConstant.DATA, (EventPayload) data);
                intent.setClass(applicationContext, ExecutorService.class);
                break;

            case GCM_MESSAGE:
                Bundle bundle = (Bundle) data;
                intent.putExtra(WebEngageConstant.DATA, bundle);
                intent.setClass(applicationContext, ExecutorService.class);
                break;

            case DEEPLINK:
                Intent deeplinkIntent = (Intent) data;
                intent.putExtra(WebEngageConstant.DATA, deeplinkIntent);
                intent.setClass(applicationContext, ExecutorService.class);
                break;

            case SYNC_TO_SERVER:
                intent.setClass(applicationContext, EventLogService.class);
                intent.putExtra(WebEngageConstant.DATA, (ArrayList<EventPayload>) data);
                break;

            case EXCEPTION:
                intent.setClass(applicationContext, ExecutorService.class);
                intent.putExtra(WebEngageConstant.DATA, (Exception) data);
                break;

            case INTERNAL_EVENT:
                intent.putExtra(WebEngageConstant.DATA, (EventPayload) data);
                intent.setClass(applicationContext, ExecutorService.class);
                break;

            case RENDER:
                intent.putExtra(WebEngageConstant.DATA, (SubscriberData) data);
                intent.setClass(applicationContext, ExecutorService.class);
                break;

            case CONFIG_REFRESH:
                intent.setClass(applicationContext, ExecutorService.class);
                break;

            case RULE_EXECUTION:
                intent.putExtra(WebEngageConstant.DATA, (ArrayList<WebEngageConstant.RuleCategory>) data);
                intent.setClass(applicationContext, ExecutorService.class);
                break;

            case DATA:
                intent.putExtra(WebEngageConstant.DATA, (HashMap<String, Object>) data);
                intent.setClass(applicationContext, ExecutorService.class);
                break;

            case JOURNEY_CONTEXT:
            case FETCH_PROFILE:
            case AMPLIFY:
                intent.setClass(applicationContext, ExecutorService.class);
                break;

            case REPORT:
                intent.putExtra(WebEngageConstant.DATA, (ArrayList<EventPayload>) data);
                intent.setClass(applicationContext, ExecutorService.class);
                break;
        }
        return intent;
    }
}
