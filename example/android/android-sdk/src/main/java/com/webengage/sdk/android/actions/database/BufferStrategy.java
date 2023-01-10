package com.webengage.sdk.android.actions.database;

import android.content.Context;

import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.ArrayList;

class BufferStrategy implements Strategy {
    Context applicationContext;

    BufferStrategy(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean report(ArrayList<EventPayload> eventPayloadList) {
        if (eventPayloadList != null) {
            for (EventPayload eventPayload : eventPayloadList) {
                report(eventPayload);
            }
        }
        return true;
    }

    @Override
    public boolean report(EventPayload eventPayload) {
        if (eventPayload == null) {
            return false;
        }
        EventDataManager.getInstance(applicationContext).saveEventData(eventPayload);
        if (eventPayload.getCategory().equals(WebEngageConstant.APPLICATION)) {
            Logger.d(WebEngageConstant.TAG, "Event : " + eventPayload.getEventName() + " successfully Logged");
        }
        return true;
    }


    int getEventCount() {
        return EventDataManager.getInstance(applicationContext).getEventCount();
    }

    ArrayList<EventPayload> getEventData(int count) {
        return EventDataManager.getInstance(applicationContext).getEventData(count);
    }


}
