package com.webengage.sdk.android;


import android.app.Activity;
import android.content.Intent;

import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.IMap;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public abstract class Analytics {

    public abstract void track(String eventName);

    public abstract void track(String eventName, Options options);

    public abstract void track(String eventName, Map<String, ? extends Object> attributes);

    public abstract void track(String eventName, Map<String, ? extends Object> attributes, Options options);

    public abstract void trackSystem(String eventName, Map<String, ? extends Object> systemAttributes, Map<String, ? extends Object> eventAttributes);

    public abstract void start(Activity activity);

    public abstract void stop(Activity activity);

    public abstract void screenNavigated(String screenName);

    public abstract void screenNavigated(String screenName, Map<String, ? extends Object> screenData);

    public abstract void setScreenData(Map<String, ? extends Object> screenData);

    public abstract void installed(Intent intent);

    public abstract WeakReference<Activity> getActivity();

    protected abstract SessionManager getSessionManager();

    protected abstract Scheduler getScheduler();

    protected abstract AnalyticsPreferenceManager getPreferenceManager();

    protected abstract void dispatchEventTopic(Object data);

    protected abstract void dispatchExceptionTopic(Object data);

    public static class Options implements Serializable, IMap {

        private boolean highReportingPriority = false;

        public Options() {
        }

        public Options setHighReportingPriority(boolean highReportingPriority) {
            this.highReportingPriority = highReportingPriority;
            return this;
        }

        public boolean getHighReportingPriority() {
            return highReportingPriority;
        }

        @Override
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(WebEngageConstant.HIGH_REPORTING_PRIORITY, this.highReportingPriority);
            return map;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Options) {
                Options options = (Options) o;
                return options.getHighReportingPriority() == this.getHighReportingPriority();
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            try {
                return DataType.convert(toMap(), DataType.STRING, true).toString();
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        public int hashCode() {
            return (String.valueOf(highReportingPriority)).hashCode();
        }

    }

}
