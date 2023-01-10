package com.webengage.sdk.android.utils.http;


import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Response {
    private int id;
    private Exception exception;
    private Map<String, List<String>> responseHeaders;
    private boolean modifiedState;
    private InputStream inputStream;
    private InputStream errorStream;
    private int responseCode;
    private String tag;
    private int flags;
    private String cacheKey;
    private long timeStamp;

    private Response(Builder builder) {
        this.exception = builder.exception;
        this.responseHeaders = builder.responseHeaders;
        this.modifiedState = builder.modifiedState;
        this.inputStream = builder.inputStream;
        this.errorStream = builder.errorStream;
        this.responseCode = builder.responseCode;
        this.tag = builder.tag;
        this.flags = builder.flags;
        this.cacheKey = builder.cacheKey;
        this.timeStamp = builder.timeStamp;
        this.id = builder.id;
    }

    protected int getId() {
        return this.id;
    }

    public Exception getException() {
        return this.exception;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.responseHeaders;
    }

    public boolean modified() {
        return this.modifiedState;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public InputStream getErrorStream() {
        return this.errorStream;
    }

    public String getTag() {
        return this.tag;
    }

    public int getFlags() {
        return this.flags;
    }

    public boolean isReadable() {
        return this.exception == null && this.inputStream != null &&  this.errorStream == null;
    }

    protected String getCacheKey() {
        return this.cacheKey;
    }

    protected long getTimeStamp() {
        return this.timeStamp;
    }

    public String getURL() {
        return cacheKey;
    }

    public void closeInputStream() {
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (Exception e) {

            }
        }
    }

    public void closeErrorStream() {
        if (this.errorStream != null) {
            try {
                this.errorStream.close();
            } catch (Exception e){

            }
        }
    }

    public Builder getCurrentState() {
        Builder builder = new Builder()
                .setID(this.id)
                .setException(this.exception)
                .setResponseHeaders(this.responseHeaders)
                .setModifiedState(this.modifiedState)
                .setResponseCode(this.responseCode)
                .setInputStream(this.inputStream)
                .setErrorStream(this.errorStream)
                .setTag(this.tag)
                .setFlags(this.flags)
                .setCacheKey(this.cacheKey)
                .setTimeStamp(this.timeStamp);
        return builder;
    }

    public static class Builder {
        private int id = -1;
        private Exception exception = null;
        private Map<String, List<String>> responseHeaders = null;
        private boolean modifiedState = true;
        private InputStream inputStream = null;
        private InputStream errorStream = null;
        private int responseCode = -1;
        private String tag = "";
        private int flags = 0;
        private String cacheKey = null;
        private long timeStamp = 0l;

        protected Builder setID(int id) {
            this.id = id;
            return this;
        }

        public Builder setException(Exception exception) {
            this.exception = exception;
            return this;
        }

        protected Builder setResponseHeaders(Map<String, List<String>> responseHeaders) {
            this.responseHeaders = responseHeaders;
            return this;
        }

        protected Builder setModifiedState(boolean modifiedState) {
            this.modifiedState = modifiedState;
            return this;
        }

        public Builder setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder setErrorStream(InputStream errorStream) {
            this.errorStream = errorStream;
            return this;
        }

        protected Builder setResponseCode(int responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        protected Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        protected Builder setFlags(int flags) {
            this.flags |= flags;
            return this;
        }

        protected Builder setCacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
            return this;
        }

        protected Builder setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}
