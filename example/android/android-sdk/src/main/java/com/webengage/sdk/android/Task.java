package com.webengage.sdk.android;


/**
 * Created by shahrukhimam on 05/10/17.
 */

interface Task<T> {

    void execute(T actual);
}
