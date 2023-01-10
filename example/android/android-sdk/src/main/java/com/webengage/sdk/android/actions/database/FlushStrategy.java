package com.webengage.sdk.android.actions.database;

import android.content.Context;

import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.ArrayList;

public class FlushStrategy implements Strategy {
    private Context applicationContext;
    private NetworkStrategy networkStrategy;

    FlushStrategy(Context applicationContext, NetworkStrategy networkStrategy) {
        this.applicationContext = applicationContext;
        this.networkStrategy = networkStrategy;
    }

    @Override
    public boolean report(EventPayload eventPayload) {
        return report();
    }

    @Override
    public boolean report(ArrayList<EventPayload> eventPayloadList) {
        return report();
    }

    private boolean report() {
        EventDataManager eventDataManager = EventDataManager.getInstance(applicationContext);
        int rows = eventDataManager.getEventCount();
        final int limit = WebEngageConstant.ROWS_MAX_LIMIT;
        while (rows > 0) {
            if (ReportingStatistics.getShouldReport()) {
                networkStrategy.report(eventDataManager.getEventData(limit));
                rows -= limit;
            } else {
                break;
            }
        }
        return true;
    }
}
