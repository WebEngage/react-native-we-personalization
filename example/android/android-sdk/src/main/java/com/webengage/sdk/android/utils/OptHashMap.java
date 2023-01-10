package com.webengage.sdk.android.utils;


import java.util.HashMap;
import java.util.Map;

public class OptHashMap<K, V> extends HashMap<K, V> {

    public OptHashMap() {
        super();
    }

    public OptHashMap(Map<? extends K, ? extends V> map) {
        super();
        if (map != null) {
            for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                putOpt(entry.getKey(), entry.getValue());
            }
        }
    }

    public OptHashMap(Map<? extends K, ? extends V> map, boolean ignoreNullKeyOrValue) {
        super();
        if (map != null) {
            for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                if (ignoreNullKeyOrValue) {
                    putOpt(entry.getKey(), entry.getValue());
                }
            }
        }
    }


    public V putOpt(K key, V value) {
        if (key == null || value == null) {
            return null;
        }
        return super.put(key, value);
    }

    public V putDefault(K key, V value, V def) {
        return value == null ? super.put(key, def) : super.put(key, value);
    }


    public void putOptAll(Map<? extends K, ? extends V> map) {
        if (map != null) {
            for(Map.Entry<? extends K,? extends V> entry: map.entrySet()){
                putOpt(entry.getKey(),entry.getValue());
            }
        }
    }
}
