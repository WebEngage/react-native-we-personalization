package com.webengage.sdk.android;


import android.content.Context;

import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.util.HashMap;
import java.util.Map;

public class EventFactory {

    public static EventPayload newSystemEvent(String eventName,
                                              Map<String, Object> systemData,
                                              Map<String, Object> eventData,
                                              Map<String, Object> extraData,
                                              Context context) {
        if (eventName == null) {
            return null;
        }
        EventPayload eventPayload = new EventPayload();
        eventPayload.setEventName(WebEngageUtils.truncate(eventName, 50));
        if (systemData == null) {
            systemData = new HashMap<String, Object>();
            if (eventData == null || eventData.isEmpty()) {
                Logger.d(WebEngageConstant.TAG, "Processing event: " + eventName);
            } else {
                Logger.d(WebEngageConstant.TAG, "Processing event: " + eventName + ", data: " + eventData);
            }
        } else {
            if (eventData == null || eventData.isEmpty()) {
                Logger.d(WebEngageConstant.TAG, "Processing event: " + eventName + ", data: " + systemData);
            } else {
                Logger.d(WebEngageConstant.TAG, "Processing event: " + eventName + ", system-data: " + systemData + ", event-data: " + eventData);
            }
        }
        systemData.put("sdk_id", BuildConfig.SDK_ID);
        systemData.put("sdk_version", BuildConfig.FEATURE_VERSION);
        systemData.put("app_id", context.getApplicationContext().getPackageName());
        Map<String, Object> clonedSystemData = null;
        try {
            clonedSystemData = (Map<String, Object>) DataType.cloneExternal(eventName, systemData);
        } catch (Exception e) {

        }
        eventPayload.setSystemData(clonedSystemData);
        if (eventData == null) {
            eventData = new HashMap<String, Object>();
        }
        Map<String, Object> clonedEventData = null;
        if (eventData != null) {
            try {
                clonedEventData = (Map<String, Object>) DataType.cloneExternal(eventName, eventData);
            } catch (Exception e) {

            }
        }
        eventPayload.setEventData(clonedEventData);
        eventPayload.setCategory(WebEngageConstant.SYSTEM);
        eventPayload.setExtraData(extraData);
        return eventPayload;
    }


    public static EventPayload newApplicationEvent(String eventName, Map<String, Object> systemData, Map<String, Object> eventData, Map<String, Object> extraData, Context context) {
        if (eventName == null) {
            return null;
        }
        EventPayload eventPayload = new EventPayload();
        eventPayload.setEventName(WebEngageUtils.truncate(eventName, 50));
        if (systemData == null) {
            systemData = new HashMap<String, Object>();
        }
        systemData.put("sdk_id", BuildConfig.SDK_ID);
        systemData.put("sdk_version", BuildConfig.FEATURE_VERSION);
        systemData.put("app_id", context.getApplicationContext().getPackageName());
        Map<String, Object> clonedSystemData = null;
        try {
            clonedSystemData = (Map<String, Object>) DataType.cloneExternal(eventName, systemData);
        } catch (Exception e) {

        }
        eventPayload.setSystemData(clonedSystemData);

        if (eventData == null) {
            eventData = new HashMap<String, Object>();
            Logger.d(WebEngageConstant.TAG, "Processing event: " + eventName);
        } else {
            Logger.d(WebEngageConstant.TAG, "Processing event: " + eventName + ", data: " + eventData);
        }
        Map<String, Object> clonedEventData = null;
        try {
            clonedEventData = (Map<String, Object>) DataType.cloneExternal(eventName, eventData);
        } catch (Exception e) {

        }
        Map<String, Object> clonedExtraData = null;
        if (extraData != null) {
            try {
                clonedExtraData = (Map<String, Object>) DataType.cloneExternal(eventName, extraData);
            } catch (Exception e) {

            }
        }
        eventPayload.setEventData(clonedEventData);
        eventPayload.setCategory(WebEngageConstant.APPLICATION);
        eventPayload.setExtraData(clonedExtraData);
        return eventPayload;
    }
}
