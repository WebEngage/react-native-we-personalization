package com.webengage.sdk.android.actions.database;

import com.webengage.sdk.android.EventPayload;

import java.util.ArrayList;

public class BufferFlushStrategy implements Strategy {
    private BufferStrategy bufferStrategy;
    private FlushStrategy flushStrategy;

    BufferFlushStrategy(BufferStrategy bufferStrategy, FlushStrategy flushStrategy) {
        this.bufferStrategy = bufferStrategy;
        this.flushStrategy = flushStrategy;
    }

    @Override
    public boolean report(EventPayload eventPayload) {
        ArrayList<EventPayload> eventPayloadList = new ArrayList<>();
        eventPayloadList.add(eventPayload);
        return this.report(eventPayloadList);
    }

    @Override
    public boolean report(ArrayList<EventPayload> eventPayloadList) {
        return bufferStrategy.report(eventPayloadList) && flushStrategy.report(eventPayloadList);
    }
}
