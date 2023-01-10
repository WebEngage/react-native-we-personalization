package com.webengage.sdk.android.utils.http;

import android.content.Context;

import java.util.Map;

public class RequestObject {
    public static final int FLAG_PERSIST_AFTER_CONFIG_REFRESH = 1;

    private final String url;
    private final RequestMethod method;
    private Map<String, String> headers;
    private final Object params;
    private final String tag;
    private int flags;
    private final int cachePolicy;
    private Context context;

    private RequestObject(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.params = builder.params;
        this.tag = builder.tag;
        this.flags = builder.flags;
        this.cachePolicy = builder.cachePolicy;
        this.context = builder.context;
    }

    public static class Builder {
        private final String url;
        private final RequestMethod method;
        private Map<String, String> headers = null;
        private Object params = null;
        private String tag = null;
        private Context context = null;
        private int flags = 0;
        private int cachePolicy = CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING;

        public Builder(String url, RequestMethod method, Context context) {
            this.url = url;
            this.method = method;
            this.context = context.getApplicationContext();
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder setParams(Object params) {
            this.params = params;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setFlags(int flags) {
            this.flags |= flags;
            return this;
        }

        public Builder setCachePolicy(int policy) {
            this.cachePolicy = policy;
            return this;
        }

        public RequestObject build() {
            return new RequestObject(this);
        }
    }

    public String getURL() {
        return this.url;
    }

    public RequestMethod getRequestMethod() {
        return this.method;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Object getParams() {
        return this.params;
    }

    public String getTag() {
        return this.tag;
    }

    public int getFlags() {
        return this.flags;
    }

    public int getCachePolicy() {
        return this.cachePolicy;
    }

    public Builder getCurrentState() {
        Builder builder = new Builder(this.url, this.method, this.context)
                .setTag(this.tag)
                .setFlags(this.flags)
                .setCachePolicy(this.cachePolicy)
                .setHeaders(this.headers)
                .setParams(params);
        return builder;
    }

    public Response execute() {
        boolean flag = true;
        synchronized (RequestExecutor.interceptors) {
            for (Interceptor interceptor : RequestExecutor.interceptors) {
                flag &= interceptor.onRequest(this, this.context);
            }
        }
        Response response = null;
        if (flag) {
            RequestExecutor requestExecutor = new RequestExecutor(this.context, this);
            response = requestExecutor.applyCachePolicy();
        }
        if (response == null) {
            response = new Response.Builder().build();
        }
        return response;
    }
}
