package com.webengage.sdk.android;


public enum Channel {
    PUSH("push", UserSystemAttribute.PUSH_OPT_IN.toString()),
    SMS("sms",UserSystemAttribute.SMS_OPT_IN.toString()),
    EMAIL("email",UserSystemAttribute.EMAIL_OPT_IN.toString()),
    IN_APP("in_app", UserDeviceAttribute.OPT_IN_INAPP),
    WHATSAPP("whatsapp", UserSystemAttribute.WHATSAPP_OPT_IN.toString());
    private String channel;
    private String userAttributesKey;

    Channel(String channel, String userAttributesKey) {
        this.channel = channel;
        this.userAttributesKey = userAttributesKey;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getUserAttributeKey() {
        return this.userAttributesKey;
    }

    public String toString() {
        return this.userAttributesKey;
    }
}
