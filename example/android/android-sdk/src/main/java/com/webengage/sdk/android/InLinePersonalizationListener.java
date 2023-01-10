package com.webengage.sdk.android;


import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public interface InLinePersonalizationListener {
     void propertiesReceived(WeakReference<Activity> activityWeakReference,
                             HashMap<String, Object> properties);
}
