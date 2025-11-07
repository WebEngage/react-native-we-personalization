package com.webengage.we_personalization_rn;

import android.content.Context;
import android.view.ViewGroup;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.webengage.personalization.WEPersonalization;
import com.webengage.we_personalization_rn.handler.WEPluginCallbackHandler;
import com.webengage.we_personalization_rn.utils.WEConstants;
import com.webengage.we_personalization_rn.views.WEInlineWidget;
import java.util.HashMap;

public class WEPersonalizationViewManagerImpl {

    public static final String NAME = "WEPersonalizationView";

    public static ViewGroup createViewInstance(ThemedReactContext context) {
        ReactApplicationContext appContext = context.getReactApplicationContext();
        
        // Initialize WebEngage settings
        appContext.getSharedPreferences(WEConstants.WE_SHARED_STORAGE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(WEConstants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS, false)
            .apply();
        
        WEPersonalization.get().registerPropertyRegistryCallback(new WEPluginCallbackHandler());
        
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(WEConstants.HEIGHT, 0);
        properties.put(WEConstants.WIDTH, 0);
        
        return new WEInlineWidget(appContext, properties, null);
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