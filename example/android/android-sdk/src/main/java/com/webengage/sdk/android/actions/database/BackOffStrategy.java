package com.webengage.sdk.android.actions.database;


import com.webengage.sdk.android.EventPayload;

import java.util.ArrayList;

class BackOffStrategy implements Strategy {

    Strategy preferredStrategy = null;
    Strategy backOffStrategy = null;

    BackOffStrategy(Strategy preferredStrategy, Strategy backOffStrategy) {
        this.preferredStrategy = preferredStrategy;
        this.backOffStrategy = backOffStrategy;
    }

    @Override
    public boolean report(EventPayload eventPayload) {
        if (!this.preferredStrategy.report(eventPayload)) {
            this.backOffStrategy.report(eventPayload);
        }
        return true;
    }

    @Override
    public boolean report(ArrayList<EventPayload> eventPayloadList) {
        if (!this.preferredStrategy.report(eventPayloadList)) {
            this.backOffStrategy.report(eventPayloadList);
        }
        return true;
    }
}
