package com.webengage.sdk.android.actions.database;


import android.content.Context;

import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.ArrayList;

class BufferNetworkStrategy implements Strategy {
    BufferStrategy bufferStrategy = null;
    NetworkStrategy networkStrategy = null;
    Context applicationContext = null;

    BufferNetworkStrategy(BufferStrategy bufferStrategy, NetworkStrategy networkStrategy, Context applicationContext) {
        this.bufferStrategy = bufferStrategy;
        this.networkStrategy = networkStrategy;
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean report(ArrayList<EventPayload> eventPayloadList) {
        if (eventPayloadList != null) {
            for (EventPayload eventPayload : eventPayloadList) {
                report(eventPayload);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean report(EventPayload eventPayload) {
        this.bufferStrategy.report(eventPayload);
        int rows = this.bufferStrategy.getEventCount();
        boolean shouldReportToNetwork = (rows >= WebEngageConstant.ROWS_THRESHOLD) && ReportingStatistics.getShouldReport();
        if (shouldReportToNetwork) {
            ArrayList<EventPayload> eventPayloadList = this.bufferStrategy.getEventData(WebEngageConstant.ROWS_THRESHOLD);
            this.networkStrategy.report(eventPayloadList);
        }
        return true;
    }
}
