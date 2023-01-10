package com.webengage.sdk.android.actions.database;


import com.webengage.sdk.android.EventPayload;

import java.util.ArrayList;

public interface Strategy {

    boolean report(EventPayload eventPayload);

    boolean report(ArrayList<EventPayload> eventPayloadList);
}
