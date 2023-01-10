package com.webengage.sdk.android.utils;


public enum Gender {

    MALE,
    FEMALE,
    OTHER;

    public static Gender valueByString(String gender) {
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}
