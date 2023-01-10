package com.webengage.sdk.android;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PushUtils {
    public static Map<String, String> prepareMap(String data) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject jsonObject = new JSONObject(data);
            Iterator<String> keysItr = jsonObject.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                String value = jsonObject.getString(key);
                map.put(key, value);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
        return map;
    }
}
