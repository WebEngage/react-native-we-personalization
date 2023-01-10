package com.webengage.sdk.android;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

@TargetApi(14)
public class WebEngageActivityLifeCycleCallbacks implements Application.ActivityLifecycleCallbacks {

    public WebEngageActivityLifeCycleCallbacks(Context context) {
        this(context, null);
    }

    public WebEngageActivityLifeCycleCallbacks(Context context, WebEngageConfig webEngageConfig) {
        WebEngage.engage(context, webEngageConfig);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    //From android 12 , WeTransparent activity is used for deeplink CTA actions for the pushNotifications and InApp for Push Trampoline Issue. No need to Start/Stop the analytics for the WETransparentActivity.
    @Override
    public void onActivityStarted(Activity activity) {
        if (!(activity instanceof WETransparentActivity))
            WebEngage.get().analytics().start(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    //From android 12 , WeTransparent activity is used for deeplink CTA actions for the pushNotifications and InApp for Push Trampoline Issue. No need to Start/Stop the analytics for the WETransparentActivity.
    @Override
    public void onActivityStopped(Activity activity) {
        if (!(activity instanceof WETransparentActivity))
            WebEngage.get().analytics().stop(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
