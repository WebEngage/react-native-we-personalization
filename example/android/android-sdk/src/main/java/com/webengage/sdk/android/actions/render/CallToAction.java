package com.webengage.sdk.android.actions.render;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URLEncoder;
import java.util.List;

public class CallToAction {

    public enum TYPE {
        LAUNCH_ACTIVITY("start_activity"),
        LINK("open_url_in_browser");
        private String s;

        TYPE(String s) {
            this.s = s;
        }

        public String toString() {
            return this.s;
        }

        public static TYPE valueFromString(String s) {
            try {
                if (LAUNCH_ACTIVITY.toString().equals(s)) {
                    return LAUNCH_ACTIVITY;
                } else if (LINK.toString().equals(s)) {
                    return LINK;
                }
            } catch (Exception e) {

            }
            return null;
        }
    }

    private String id = null;
    private String text = null;
    private String action = null;
    private boolean isPrime = false;
    private TYPE type = null;
    private boolean isNative = false;


    public CallToAction(String id, String text, String action, boolean isPrime,boolean isNative) {
        this.id = id;
        this.text = text;
        this.action = action;
        this.isPrime = isPrime;
        this.isNative = isNative;
        if (this.action != null) {
            List<String> params = Uri.parse(this.action).getPathSegments();
            if (params.size() > 0) {
                String str = params.get(0);
                this.type = TYPE.valueFromString(str);
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public String getAction() {
        if (action != null) {
            List<String> params = Uri.parse(this.action).getPathSegments();
            if (params.size() > 1) {
                return params.get(1);//return the decoded action
            }
        }
        return null;
    }

    protected String getFullActionUri() {
        return action;
    }

    public boolean isPrimeAction() {
        return this.isPrime;
    }

    public boolean isNative(){
        return this.isNative;
    }

    public TYPE getType() {
        return this.type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAction(String action, TYPE type, Context context) {
        try {
            this.action = "webengage://" + context.getPackageName() + "/" + type.toString() + "/" + URLEncoder.encode(action, "UTF-8");
        } catch (Exception e) {
            this.action = "webengage://" + context.getPackageName() + "/" + TYPE.LAUNCH_ACTIVITY.toString() + "/" + context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().getClassName();
        }
        this.type = type;
    }
}

