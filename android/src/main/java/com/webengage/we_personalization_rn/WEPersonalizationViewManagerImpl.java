package com.webengage.we_personalization_rn;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.webengage.personalization.WEPersonalization;
import com.webengage.sdk.android.Logger;
import com.webengage.we_personalization_rn.handler.WEPluginCallbackHandler;
import com.webengage.we_personalization_rn.utils.WEConstants;
import com.webengage.we_personalization_rn.views.WEInlineWidget;
import java.util.HashMap;

public final class WEPersonalizationViewManagerImpl {
    private WEPersonalizationViewManagerImpl() {}

    public static final String NAME = "WEPersonalizationView";

    public static ViewGroup createViewInstance(ThemedReactContext context) {
        if (context == null) {
            Logger.d(WEConstants.TAG, "WEPersonalizationViewManagerImpl: createViewInstance - context is null");
            return null;
        }
        
        ReactApplicationContext appContext = context.getReactApplicationContext();
        if (appContext == null) {
            Logger.d(WEConstants.TAG, "WEPersonalizationViewManagerImpl: createViewInstance - appContext is null");
            return null;
        }
        
        try {
            SharedPreferences prefs = appContext.getSharedPreferences(WEConstants.WE_SHARED_STORAGE, Context.MODE_PRIVATE);
            if (prefs != null) {
                prefs.edit()
                    .putBoolean(WEConstants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS, false)
                    .apply();
            }
        } catch (Exception e) {
            Logger.d(WEConstants.TAG, "WEPersonalizationViewManagerImpl: createViewInstance - SharedPreferences failed: " + e.getMessage());
        }
        
            WEPersonalization.get().registerPropertyRegistryCallback(new WEPluginCallbackHandler());
        
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(WEConstants.HEIGHT, 0);
        properties.put(WEConstants.WIDTH, 0);
        
        return new WEInlineWidget(appContext, properties);
    }

    public static void updateProperties(ViewGroup view, String screenName, String propertyId) {
        if (view instanceof WEInlineWidget) {
                ((WEInlineWidget) view).updateProperties(screenName, propertyId);
        }
    }

    public static void setStyle(ViewGroup view, int width, int height) {
        if (view instanceof WEInlineWidget) {
                ((WEInlineWidget) view).updateStyle(height, width);
        }
    }
}