package com.webengage.we_personalization_rn.handler;

import android.os.Build;

import androidx.annotation.NonNull;

import com.webengage.personalization.callbacks.WEPropertyRegistryCallback;
import com.webengage.sdk.android.Logger;
import com.webengage.we_personalization_rn.utils.WEConstants;
import com.webengage.we_personalization_rn.model.ScreenNavigatorCallback;
import com.webengage.we_personalization_rn.registry.WEPropertyRegistry;

import java.util.HashMap;

public class WEPluginCallbackHandler implements WEPropertyRegistryCallback {
  private static volatile String currentScreen = null;
  private static final HashMap<String, HashMap<String, ScreenNavigatorCallback>> mapOfScreenNavigatedCallbacks = new HashMap<>();

  // TODO - Test this scenario properly 
  public static synchronized void setScreenNavigatorCallback(String screenName, String propertyId, ScreenNavigatorCallback screenNavigatedCallback) {
    if (screenName == null || propertyId == null || screenNavigatedCallback == null) {
      Logger.d(WEConstants.TAG, "[WE-Inline-Android] setScreenNavigatorCallback: null parameter");
      return;
    }
    HashMap<String, ScreenNavigatorCallback> callback = mapOfScreenNavigatedCallbacks.get(screenName);
    if (callback == null) {
      callback = new HashMap<>();
    }
    if (!callback.containsKey(propertyId)) {
      callback.put(propertyId, screenNavigatedCallback);
    }
    mapOfScreenNavigatedCallbacks.put(screenName, callback);
    if (screenName.equals(currentScreen)) {
      try {
        screenNavigatedCallback.screenNavigated(screenName);
      } catch (Exception e) {
        Logger.d(WEConstants.TAG, "[WE-Inline-Android] setScreenNavigatorCallback failed: " + e.getMessage());
      }
    }
  }

  public static synchronized void removeScreenNavigatorCallback(String screenName, ScreenNavigatorCallback screenNavigatedCallback) {
    if (screenName == null) {
      Logger.d(WEConstants.TAG, "[WE-Inline-Android] removeScreenNavigatorCallback: screenName is null");
      return;
    }
    mapOfScreenNavigatedCallbacks.remove(screenName);
  }

  @Override
  public synchronized void onPropertyCacheCleared(@NonNull String navigatedScreen) {
    String oldScreen = currentScreen;
    Logger.d(WEConstants.TAG, "[WE-Inline-Android] onPropertyCacheCleared: " + (oldScreen != null ? oldScreen : "null") + "→" + navigatedScreen);
    currentScreen = navigatedScreen;
    try {
      WEPropertyRegistry.get().clearCacheData();
    } catch (Exception e) {
      Logger.d(WEConstants.TAG, "[WE-Inline-Android] clearCacheData failed: " + e.getMessage());
    }
    HashMap<String, ScreenNavigatorCallback> callbacksList = mapOfScreenNavigatedCallbacks.get(navigatedScreen);
    if (callbacksList != null) {
      Logger.d(WEConstants.TAG, "[WE-Inline-Android] notifyWidgets: screen=" + navigatedScreen + ", count=" + callbacksList.size());
      for (String propertyKey : callbacksList.keySet()) {
        ScreenNavigatorCallback propertyCallback = callbacksList.get(propertyKey);
        if (propertyCallback != null) {
          try {
            propertyCallback.screenNavigated(navigatedScreen);
          } catch (Exception e) {
            Logger.d(WEConstants.TAG, "[WE-Inline-Android] callback failed: property=" + propertyKey + ", error=" + e.getMessage());
          }
        }
      }
    }
  }

}
