package com.webengage.sdk.android.actions.database;


public enum DataContainer {

    PAGE,
    EVENT,
    LATEST_EVENT,
    USER,
    ANDROID,
    WEB,
    IOS,
    EVENT_CRITERIA,
    JOURNEY,
    ATTR,
    SCOPES;

    public static DataContainer valueByString(String event) {
        try {
            return DataContainer.valueOf(event.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        return this.name().toLowerCase();
    }

    public boolean isKnownUsersContainer() {
        return this.equals(USER) || this.equals(ANDROID) || this.equals(WEB) || this.equals(IOS) || this.equals(EVENT_CRITERIA) || this.equals(ATTR) || this.equals(JOURNEY) || this.equals(SCOPES);
    }

    public boolean canBeStored() {
        return this.equals(USER) || this.equals(ANDROID) || this.equals(WEB) || this.equals(IOS) || this.equals(EVENT_CRITERIA) || this.equals(ATTR) || this.equals(SCOPES);
    }


    public boolean isAnonymousUserContainer() {
        return this.equals(EVENT_CRITERIA) || this.equals(JOURNEY) || this.equals(SCOPES);
    }

    public String getSDKID() {
        if (this.equals(WEB)) {
            return "1";
        } else if (this.equals(ANDROID)) {
            return "2";
        } else if (this.equals(IOS)) {
            return "3";
        } else return null;
    }
}
