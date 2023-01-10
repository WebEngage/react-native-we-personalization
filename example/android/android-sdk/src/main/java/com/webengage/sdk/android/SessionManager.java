package com.webengage.sdk.android;


import android.content.Context;
import android.content.Intent;

import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.utils.WebEngageConstant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    Context applicationContext;
    AnalyticsPreferenceManager analyticsPreferenceManager;

    SessionManager(AnalyticsPreferenceManager analyticsPreferenceManager, Context context) {
        this.analyticsPreferenceManager = analyticsPreferenceManager;
        this.applicationContext = context.getApplicationContext();
    }

    void generateSUID() {
        String SUID = new UUID(System.currentTimeMillis(), UUID.randomUUID().getLeastSignificantBits()).toString();
        analyticsPreferenceManager.saveSUID(SUID);
    }

    public void postNewForegroundSession() {
        Map<String, Object> systemdata = new HashMap<String, Object>();
        systemdata.put("session_type", "online");
        Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.VISITOR_NEW_SESSION, systemdata, null, null, applicationContext), applicationContext);
        WebEngage.startService(intent, applicationContext);
    }


    public void postNewBackgroundSession() {
        Map<String, Object> systemdata = new HashMap<String, Object>();
        systemdata.put("session_type", "background");
        Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.VISITOR_NEW_SESSION, systemdata, null, null, applicationContext), applicationContext);
        WebEngage.startService(intent, applicationContext);
    }

    public void postDestroySession() {
        Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.VISITOR_SESSION_CLOSE, null, null, null, applicationContext), applicationContext);
        WebEngage.startService(intent, applicationContext);
    }


    public void createNewForegroundSession() {
        Map<String, Object> systemData = new HashMap<String, Object>();
        systemData.put("session_type", "online");
        try {
            SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EVENT, EventFactory.newSystemEvent(EventName.VISITOR_NEW_SESSION, systemData, null, null, applicationContext));
            CallbackDispatcher.init(applicationContext).onNewSessionStarted();
        } catch (Exception e) {
            try {
                SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EXCEPTION, e);
            } catch (Exception e1) {

            }
        }
    }

    public void createNewBackgroundSession() {
        Map<String, Object> systemData = new HashMap<String, Object>();
        systemData.put("session_type", "background");
        try {
            SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EVENT, EventFactory.newSystemEvent(EventName.VISITOR_NEW_SESSION, systemData, null, null, applicationContext));
        } catch (Exception e) {
            try {
                SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EXCEPTION, e);
            } catch (Exception e1) {

            }
        }
    }


    public void destroyCurrentSession() {
        try {
            SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EVENT, EventFactory.newSystemEvent(EventName.VISITOR_SESSION_CLOSE, null, null, null, applicationContext));
        } catch (Exception e) {
            try {
                SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EXCEPTION, e);
            } catch (Exception e1) {

            }
        }
    }

    public void sentTimeSpentEvent(long timeSpent) {
        Map<String, Object> systemData = new HashMap<>();
        systemData.put(UserDeviceAttribute.TIME_SPENT.toString(), timeSpent);
        try {
            SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EVENT, EventFactory.newSystemEvent(EventName.USER_INCREMENT, systemData, null, null, applicationContext));
        } catch (Exception e) {
            try {
                SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EXCEPTION, e);
            } catch (Exception e1) {

            }
        }
    }


    public void executeSessionAndPageRules() {
        List<WebEngageConstant.RuleCategory> executionChain = new ArrayList<WebEngageConstant.RuleCategory>();
        executionChain.add(WebEngageConstant.RuleCategory.SESSION_RULE);
        executionChain.add(WebEngageConstant.RuleCategory.PAGE_RULE);
        try {
            SubscriberManager.get(this.applicationContext).callSubscribers(Topic.RULE_EXECUTION, executionChain);
        } catch (Exception e) {
            try {
                SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EXCEPTION, e);
            } catch (Exception e1) {

            }
        }
    }

    public void executePageRules() {
        List<WebEngageConstant.RuleCategory> executionChain = new ArrayList<WebEngageConstant.RuleCategory>();
        executionChain.add(WebEngageConstant.RuleCategory.PAGE_RULE);
        try {
            SubscriberManager.get(this.applicationContext).callSubscribers(Topic.RULE_EXECUTION, executionChain);
        } catch (Exception e) {
            try {
                SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EXCEPTION, e);
            } catch (Exception e1) {

            }
        }
    }

}
