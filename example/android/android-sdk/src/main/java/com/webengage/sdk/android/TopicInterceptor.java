package com.webengage.sdk.android;

public interface TopicInterceptor {
    boolean preCall(Topic topic, Object data);

    void postCall(Topic topic, Object data);
}
