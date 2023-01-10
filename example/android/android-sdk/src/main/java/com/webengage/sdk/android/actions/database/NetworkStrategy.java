package com.webengage.sdk.android.actions.database;


import android.content.Context;
import android.content.Intent;

import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;

import java.util.ArrayList;

class NetworkStrategy implements Strategy {
    Context applicationContext = null;

    NetworkStrategy(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean report(EventPayload eventPayload) {
        ArrayList<EventPayload> list = new ArrayList<EventPayload>();
        list.add(eventPayload);
        return report(list);
    }


    @Override
    public boolean report(ArrayList<EventPayload> eventPayloadList) {
        Intent intent = IntentFactory.newIntent(Topic.SYNC_TO_SERVER, eventPayloadList, applicationContext);
        WebEngage.startService(intent, applicationContext);
        return true;

    }


}
