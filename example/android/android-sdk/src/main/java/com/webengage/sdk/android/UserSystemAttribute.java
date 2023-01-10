package com.webengage.sdk.android;

import com.webengage.sdk.android.actions.database.Operation;

public enum UserSystemAttribute {
    FIRST_NAME(Operation.FORCE_UPDATE),
    LAST_NAME(Operation.FORCE_UPDATE),
    EMAIL(Operation.FORCE_UPDATE),
    HASHED_EMAIL(Operation.FORCE_UPDATE),
    BIRTH_DATE(Operation.FORCE_UPDATE),
    GENDER(Operation.FORCE_UPDATE),
    PHONE(Operation.FORCE_UPDATE),
    HASHED_PHONE(Operation.FORCE_UPDATE),
    COMPANY(Operation.FORCE_UPDATE),
    TIME_SPENT(Operation.FORCE_UPDATE),
    PUSH_OPT_IN(Operation.FORCE_UPDATE),
    SMS_OPT_IN(Operation.FORCE_UPDATE),
    EMAIL_OPT_IN(Operation.FORCE_UPDATE),
    WHATSAPP_OPT_IN(Operation.FORCE_UPDATE),
    LATITUDE(Operation.FORCE_UPDATE),
    LONGITUDE(Operation.FORCE_UPDATE),
    CITY(Operation.FORCE_UPDATE),
    COUNTRY(Operation.FORCE_UPDATE),
    REGION(Operation.FORCE_UPDATE),
    POSTAL_CODE(Operation.FORCE_UPDATE),
    LOCALITY(Operation.FORCE_UPDATE),
    LAST_LOGGED_IN(Operation.FORCE_UPDATE),
    FIRST_LOGGED_IN(Operation.FORCE_UPDATE),
    LAST_SEEN(Operation.FORCE_UPDATE),
    CREATED_AT(Operation.FORCE_UPDATE),
    REFERRER_TYPE(Operation.FORCE_UPDATE),
    SESSION_COUNT(Operation.FORCE_UPDATE),
    CAMPAIGN_ID(Operation.FORCE_UPDATE),
    CAMPAIGN_SOURCE(Operation.FORCE_UPDATE),
    CAMPAIGN_MEDIUM(Operation.FORCE_UPDATE),
    CAMPAIGN_TERM(Operation.FORCE_UPDATE),
    CAMPAIGN_CONTENT(Operation.FORCE_UPDATE),
    CAMPAIGN_GCLID(Operation.FORCE_UPDATE),
    REFERRER(Operation.FORCE_UPDATE);
    private Operation operation;

    UserSystemAttribute(Operation operation) {
        this.operation = operation;
    }

    public String toString() {
        return this.name().toLowerCase();
    }

    public static UserSystemAttribute valueByString(String s) {
        try {
            return UserSystemAttribute.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    public Operation getOperation() {
        return this.operation;
    }

}

