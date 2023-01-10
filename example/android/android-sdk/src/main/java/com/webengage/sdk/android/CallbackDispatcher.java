package com.webengage.sdk.android;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.webengage.sdk.android.actions.render.InAppNotificationData;
import com.webengage.sdk.android.actions.render.PushNotificationData;
import com.webengage.sdk.android.callbacks.CustomPushRender;
import com.webengage.sdk.android.callbacks.CustomPushRerender;
import com.webengage.sdk.android.callbacks.InAppNotificationCallbacks;
import com.webengage.sdk.android.callbacks.LifeCycleCallbacks;
import com.webengage.sdk.android.callbacks.PushNotificationCallbacks;
import com.webengage.sdk.android.callbacks.StateChangeCallbacks;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CallbackDispatcher extends StateChangeCallbacks implements PushNotificationCallbacks,
        LifeCycleCallbacks, InAppNotificationCallbacks, CustomPushRender, CustomPushRerender, InLinePersonalizationListener {

    Context applicationContext = null;
    static volatile CallbackDispatcher instance = null;
    static List<LifeCycleCallbacks> lifeCycleCallbackListeners = null;
    static List<PushNotificationCallbacks> pushNotificationCallbacksListeners = null;
    static CustomPushRender customPushRender = null;
    static CustomPushRerender customPushRerender = null;
    static List<InAppNotificationCallbacks> inAppNotificationCallbacksListeners = null;
    static List<StateChangeCallbacks> stateChangeListeners = null;
    static InLinePersonalizationListener inlinePersonalizationListener = null;
    Handler mainHandler = null;

    private CallbackDispatcher(Context context) {
        this.applicationContext = context.getApplicationContext();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static CallbackDispatcher init(Context context) {
        if (instance == null) {
            synchronized (CallbackDispatcher.class) {
                if (instance == null) {
                    instance = new CallbackDispatcher(context);
                }
            }
        }
        return instance;
    }

    protected static void registerPushNotificationCallback(PushNotificationCallbacks pushNotificationCallbacks) {
        if (pushNotificationCallbacks != null) {
            if (pushNotificationCallbacksListeners == null) {
                pushNotificationCallbacksListeners = new ArrayList<PushNotificationCallbacks>();
            }
            if (!pushNotificationCallbacksListeners.contains(pushNotificationCallbacks)) {
                pushNotificationCallbacksListeners.add(pushNotificationCallbacks);
            }
        }
    }

    protected static void registerCustomPushRenderCallback(CustomPushRender c) {
        if (c != null) {
            customPushRender = c;
//            if (customPushRenderList == null) {
//                customPushRenderList = new ArrayList<CustomPushRender>();
//            }
//            if (!customPushRenderList.contains(customPushRender)) {
//                customPushRenderList.add(customPushRender);
//            }
        }
    }

    protected static void registerCustomPushRerenderCallback(CustomPushRerender c) {
        if (c != null) {
            customPushRerender = c;
//            if (customPushRerenderList == null) {
//                customPushRerenderList = new ArrayList<CustomPushRerender>();
//            }
//            if (!customPushRerenderList.contains(customPushRerender)) {
//                customPushRerenderList.add(customPushRerender);
//            }
        }
    }

    protected static void registerInAppNotificationCallback(InAppNotificationCallbacks inAppNotificationCallbacks) {
        if (inAppNotificationCallbacks != null) {
            if (inAppNotificationCallbacksListeners == null) {
                inAppNotificationCallbacksListeners = new ArrayList<InAppNotificationCallbacks>();
            }
            if (!inAppNotificationCallbacksListeners.contains(inAppNotificationCallbacks)) {
                inAppNotificationCallbacksListeners.add(inAppNotificationCallbacks);
            }
        }
    }

    protected static void registerLifeCycleCallback(LifeCycleCallbacks lifeCycleCallbacks) {
        if (lifeCycleCallbacks != null) {
            if (lifeCycleCallbackListeners == null) {
                lifeCycleCallbackListeners = new ArrayList<LifeCycleCallbacks>();
            }
            if (!lifeCycleCallbackListeners.contains(lifeCycleCallbacks)) {
                lifeCycleCallbackListeners.add(lifeCycleCallbacks);
            }
        }
    }

    protected static void registerStateChangeCallback(StateChangeCallbacks stateChangeCallbacks, Analytics analytics, Context context) {
        if (stateChangeCallbacks != null) {
            if (stateChangeListeners == null) {
                stateChangeListeners = new ArrayList<>();
            }

            if (!stateChangeListeners.contains(stateChangeCallbacks)) {
                stateChangeListeners.add(stateChangeCallbacks);
                if (analytics != null && analytics instanceof AnalyticsImpl && context != null) {
                    String luid = analytics.getPreferenceManager().getLUID();
                    stateChangeCallbacks.onAnonymousIdChanged(context, (luid.isEmpty() ? null : luid));

                }
            }
        }
    }

    protected static void unregisterPushNotificationCallback(PushNotificationCallbacks pushNotificationCallbacks) {
        if (pushNotificationCallbacksListeners != null) {
            pushNotificationCallbacksListeners.remove(pushNotificationCallbacks);
        }
    }

    protected static void unregisterInAppNotificationCalback(InAppNotificationCallbacks inAppNotificationCallbacks) {
        if (inAppNotificationCallbacksListeners != null) {
            inAppNotificationCallbacksListeners.remove(inAppNotificationCallbacks);
        }
    }

    protected static void unregisterLifeCycleCallback(LifeCycleCallbacks lifeCycleCallbacks) {
        if (lifeCycleCallbackListeners != null) {
            lifeCycleCallbackListeners.remove(lifeCycleCallbacks);
        }
    }

    protected static void unregisterStateChangeCallback(StateChangeCallbacks stateChangeCallbacks) {
        if (stateChangeListeners != null) {
            stateChangeListeners.remove(stateChangeCallbacks);
        }
    }

    @Override
    public void onGCMRegistered(Context context, final String regID) {
        if (lifeCycleCallbackListeners != null) {
            for (int i = 0; i < lifeCycleCallbackListeners.size(); i++) {
                final LifeCycleCallbacks lifeCycleCallbacks = lifeCycleCallbackListeners.get(i);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lifeCycleCallbacks != null) {
                            lifeCycleCallbacks.onGCMRegistered(CallbackDispatcher.this.applicationContext, regID);
                        }
                    }
                });
            }
        }
    }


    @Override
    public void onGCMMessageReceived(Context context, final Intent intent) {
        if (lifeCycleCallbackListeners != null) {
            for (int i = 0; i < lifeCycleCallbackListeners.size(); i++) {
                final LifeCycleCallbacks lifeCycleCallbacks = lifeCycleCallbackListeners.get(i);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lifeCycleCallbacks != null) {
                            lifeCycleCallbacks.onGCMMessageReceived(CallbackDispatcher.this.applicationContext, intent);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onAppInstalled(Context context, final Intent intent) {
        if (lifeCycleCallbackListeners != null) {
            for (int i = 0; i < lifeCycleCallbackListeners.size(); i++) {
                final LifeCycleCallbacks lifeCycleCallbacks = lifeCycleCallbackListeners.get(i);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lifeCycleCallbacks != null) {
                            lifeCycleCallbacks.onAppInstalled(CallbackDispatcher.this.applicationContext, intent);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onAppUpgraded(Context context, final int oldVersion, final int newVersion) {
        if (lifeCycleCallbackListeners != null) {
            for (int i = 0; i < lifeCycleCallbackListeners.size(); i++) {
                final LifeCycleCallbacks lifeCycleCallbacks = lifeCycleCallbackListeners.get(i);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lifeCycleCallbacks != null) {
                            lifeCycleCallbacks.onAppUpgraded(CallbackDispatcher.this.applicationContext, oldVersion, newVersion);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onNewSessionStarted() {
        if (lifeCycleCallbackListeners != null) {
            for (int i = 0; i < lifeCycleCallbackListeners.size(); i++) {
                final LifeCycleCallbacks lifeCycleCallbacks = lifeCycleCallbackListeners.get(i);
                mainHandler.post(() -> {
                    if (lifeCycleCallbacks != null) {
                        lifeCycleCallbacks.onNewSessionStarted();
                    }
                });
            }
        }
    }

    @Override
    public PushNotificationData onPushNotificationReceived(Context context, PushNotificationData pushNotificationData) {
        if (pushNotificationCallbacksListeners != null) {
            for (int i = 0; i < pushNotificationCallbacksListeners.size(); i++) {
                PushNotificationCallbacks pushNotificationCallbacks = pushNotificationCallbacksListeners.get(i);
                if (pushNotificationCallbacks != null) {
                    pushNotificationData = pushNotificationCallbacks.onPushNotificationReceived(CallbackDispatcher.this.applicationContext, pushNotificationData);
                }
            }
        }
        return pushNotificationData;
    }

    @Override
    public void onPushNotificationShown(Context context, final PushNotificationData pushNotificationData) {
        if (pushNotificationCallbacksListeners != null) {
            for (int i = 0; i < pushNotificationCallbacksListeners.size(); i++) {
                final PushNotificationCallbacks pushNotificationCallbacks = pushNotificationCallbacksListeners.get(i);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (pushNotificationCallbacks != null) {
                            pushNotificationCallbacks.onPushNotificationShown(CallbackDispatcher.this.applicationContext, pushNotificationData);
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onPushNotificationClicked(Context context, final PushNotificationData pushNotificationData) {
        boolean flag = false;
        if (pushNotificationCallbacksListeners != null) {
            for (int i = 0; i < pushNotificationCallbacksListeners.size(); i++) {
                PushNotificationCallbacks pushNotificationCallbacks = pushNotificationCallbacksListeners.get(i);
                if (pushNotificationCallbacks != null) {
                    flag |= pushNotificationCallbacks.onPushNotificationClicked(CallbackDispatcher.this.applicationContext, pushNotificationData);
                }
            }
        }
        return flag;
    }

    @Override
    public void onPushNotificationDismissed(Context context, final PushNotificationData pushNotificationData) {
        if (pushNotificationCallbacksListeners != null) {
            for (int i = 0; i < pushNotificationCallbacksListeners.size(); i++) {
                final PushNotificationCallbacks pushNotificationCallbacks = pushNotificationCallbacksListeners.get(i);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (pushNotificationCallbacks != null) {
                            pushNotificationCallbacks.onPushNotificationDismissed(CallbackDispatcher.this.applicationContext, pushNotificationData);
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onPushNotificationActionClicked(final Context context, final PushNotificationData pushNotificationData, final String buttonId) {
        boolean flag = false;
        if (pushNotificationCallbacksListeners != null) {
            for (int i = 0; i < pushNotificationCallbacksListeners.size(); i++) {
                PushNotificationCallbacks pushNotificationCallbacks = pushNotificationCallbacksListeners.get(i);
                if (pushNotificationCallbacks != null) {
                    flag |= pushNotificationCallbacks.onPushNotificationActionClicked(CallbackDispatcher.this.applicationContext, pushNotificationData, buttonId);
                }
            }
        }
        return flag;
    }

    @Override
    public InAppNotificationData onInAppNotificationPrepared(Context context, InAppNotificationData inAppNotificationData) {
        if (inAppNotificationCallbacksListeners != null) {
            for (int i = 0; i < inAppNotificationCallbacksListeners.size(); i++) {
                InAppNotificationCallbacks inAppNotificationCallbacks = inAppNotificationCallbacksListeners.get(i);
                if (inAppNotificationCallbacks != null) {
                    inAppNotificationData = inAppNotificationCallbacks.onInAppNotificationPrepared(this.applicationContext, inAppNotificationData);
                }
            }
        }
        return inAppNotificationData;
    }

    @Override
    public void onInAppNotificationShown(Context context, final InAppNotificationData inAppNotificationData) {
        if (inAppNotificationCallbacksListeners != null) {
            for (int i = 0; i < inAppNotificationCallbacksListeners.size(); i++) {
                final InAppNotificationCallbacks inAppNotificationCallbacks = inAppNotificationCallbacksListeners.get(i);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (inAppNotificationCallbacks != null) {
                            inAppNotificationCallbacks.onInAppNotificationShown(CallbackDispatcher.this.applicationContext, inAppNotificationData);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onInAppNotificationDismissed(Context context, final InAppNotificationData inAppNotificationData) {
        if (inAppNotificationCallbacksListeners != null) {
            for (int i = 0; i < inAppNotificationCallbacksListeners.size(); i++) {
                final InAppNotificationCallbacks inAppNotificationCallbacks = inAppNotificationCallbacksListeners.get(i);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (inAppNotificationCallbacks != null) {
                            inAppNotificationCallbacks.onInAppNotificationDismissed(CallbackDispatcher.this.applicationContext, inAppNotificationData);
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onInAppNotificationClicked(Context context, final InAppNotificationData inAppNotificationData, final String buttonId) {
        boolean flag = false;
        if (inAppNotificationCallbacksListeners != null) {
            for (int i = 0; i < inAppNotificationCallbacksListeners.size(); i++) {
                InAppNotificationCallbacks inAppNotificationCallbacks = inAppNotificationCallbacksListeners.get(i);
                if (inAppNotificationCallbacks != null) {
                    flag |= inAppNotificationCallbacks.onInAppNotificationClicked(CallbackDispatcher.this.applicationContext, inAppNotificationData, buttonId);
                }
            }
        }
        return flag;
    }

    @Override
    public void onAnonymousIdChanged(final Context context, final String anonymousId) {
        final List<StateChangeCallbacks> listeners = stateChangeListeners;
        if (listeners != null) {
            for (final StateChangeCallbacks listener : listeners) {
                if (listener != null) {
                    mainHandler.post(new Runnable() {
                        public void run() {
                            listener.onAnonymousIdChanged(context, anonymousId);
                        }
                    });
                }
            }
        }
    }

    public boolean isCustomRenderRegistered() {
        return customPushRender != null;
    }

    public boolean isCustomRerenderRegistered() {
        return customPushRerender != null;
    }

    @Override
    public boolean onRender(Context context, PushNotificationData pushNotificationData) {
        //final List<CustomPushRender> listeners = customPushRenderList;
        boolean result = false;
        if (customPushRender != null) {
            result = customPushRender.onRender(context, pushNotificationData);
        }
//        if (listeners != null) {
//            for (final CustomPushRender listener : listeners) {
//                if (listener != null) {
//                    result = listener.onRender(context, pushNotificationData);
//                }
//            }
//        }
        return result;
    }

    @Override
    public boolean onRerender(Context context, PushNotificationData pushNotificationData, Bundle extras) {
        //final List<CustomPushRerender> listeners = customPushRerenderList;
        boolean result = false;
        if (customPushRerender != null) {
            result = customPushRerender.onRerender(context, pushNotificationData, extras);
        }
//        if (listeners != null) {
//            for (final CustomPushRerender listener : listeners) {
//                if (listener != null) {
//                    result = listener.onRerender(context, pushNotificationData, extras);
//                }
//            }
//        }
        return result;
    }

    protected static void registerInlinePersonalizationListener(InLinePersonalizationListener listener) {
        inlinePersonalizationListener = listener;
    }


    @Override
    public void propertiesReceived(WeakReference<Activity> activityWeakReference, HashMap<String, Object> properties) {
        if (null != inlinePersonalizationListener) {
            inlinePersonalizationListener.propertiesReceived(activityWeakReference, properties);
        } else {
            Logger.d(WebEngageConstant.TAG, "In callback propertiesReceived: No callback set");
        }
    }
}
