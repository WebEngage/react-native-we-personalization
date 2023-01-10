package com.webengage.sdk.android.utils.http;

public class CachePolicy {
    //this policy first tries to connect to network and stores the cachedResponse on cache
    //if exception occurs during establishing connection then cachedResponse will be pulled from cache
    //no cache validation is done
    public static final int GET_DATA_FROM_NETWORK_FIRST_ELSE_FROM_CACHE = 1;


    //this policy first attempts to retrieve data from cache.if data does not exist in cache or if data
    //is invalidated then network connection will
    //established and date is stored on cache.
    //cache validation is not done if device is offline.
    public static final int GET_VALIDATED_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE = 2;


    //this policy will execute every request online, and data will not be stored on cache
    public static final int GET_DATA_FROM_NETWORK_ONLY_NO_CACHING = 3;


    //this policy will only retrieve data from cache,and will not use any network connection
    //no cache validation is done
    public static final int GET_DATA_FROM_CACHE_ONLY = 4;


    //same as  policy but cache validation is done.if device is offline and data has been
    //invalidated then it will not be returned
    public static final int GET_VALIDATED_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE_RETURNS_NULL_IF_OFFLINE_AND_DATA_EXPIRED = 5;


    //this policy first attempts to retrieve data from cache.if data exists(no validation is done even if
    //device is online) then it will be returned
    //otherwise data will be downloaded
    public static final int GET_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE = 6;

}