package com.webengage.sdk.android.actions.database;


public enum Operation {

    INCREMENT,
    FORCE_UPDATE,
    OPT_UPDATE;

    public static Operation valueByString(String operation) {
        try {
            return Operation.valueOf(operation.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}
