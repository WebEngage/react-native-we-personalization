package com.webengage.sdk.android;


import android.content.Context;

import java.util.Map;

public interface Subscriber {

     interface Factory {
         Subscriber initialize(Context context);
    }

    void createAction(Topic topic, Object data);

    boolean validateData(Object data);

    Map<String, Object> getActionAttributes(Topic topic, Object data);

}
