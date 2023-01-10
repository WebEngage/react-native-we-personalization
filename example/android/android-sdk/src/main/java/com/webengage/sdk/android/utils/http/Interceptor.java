package com.webengage.sdk.android.utils.http;

import android.content.Context;

import java.io.InputStream;

public interface Interceptor {

    boolean onRequest(RequestObject requestObject,Context context);

    Response onResponse(Response response,Context context);
}
