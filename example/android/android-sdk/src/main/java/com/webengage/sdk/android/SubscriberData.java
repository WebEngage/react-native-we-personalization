package com.webengage.sdk.android;

import java.io.Serializable;

public class SubscriberData implements Serializable {

    private String contextID = null;
    private Object data = null;


    private SubscriberData() {

    }

    public SubscriberData(String contextID, Object data) {
        this.contextID = contextID;
        this.data = data;
    }

    public String getContextID() {
        return this.contextID;
    }

    public Object getData() {
        return this.data;
    }

    @Override
    public int hashCode() {
        if(this.contextID != null && this.data != null) {
            return (this.contextID + this.data).hashCode();
        } else {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
