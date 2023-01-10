package com.webengage.sdk.android.actions.database;


import java.util.List;

public interface OnDataHolderChangeListener {
    void onChange(List<Object> key, Object value, String userIdentifier, Operation operation);
}
