package com.webengagepersonalization.handler;

import android.os.Build;

import androidx.annotation.NonNull;

import com.webengage.personalization.callbacks.WEPropertyRegistryCallback;
import com.webengage.sdk.android.Logger;
import com.webengagepersonalization.utils.WEConstants;
import com.webengagepersonalization.model.ScreenNavigatorCallback;
import com.webengagepersonalization.regisrty.WEPropertyRegistry;

import java.util.HashMap;

public class WEPluginCallbackHandler implements WEPropertyRegistryCallback {
  private static String currentScreen = null;
  static HashMap<String, HashMap<String, ScreenNavigatorCallback>> mapOfScreenNavigatedCallbacks = new HashMap<>();

  public static void setScreenNavigatorCallback(String screenName, String propertyId, ScreenNavigatorCallback screenNavigatedCallback) {
    HashMap<String, ScreenNavigatorCallback> callback = new HashMap<>();
    if (mapOfScreenNavigatedCallbacks.containsKey(screenName)) {
      callback = mapOfScreenNavigatedCallbacks.get(screenName);
      if (!callback.containsKey(propertyId)) {
        callback.put(propertyId, screenNavigatedCallback);
      }
    } else {
      callback.put(propertyId, screenNavigatedCallback);
    }
    if (screenName.equals(currentScreen)) {
      screenNavigatedCallback.screenNavigated(screenName);
    }
    mapOfScreenNavigatedCallbacks.put(screenName, callback);
  }

  public static void removeScreenNavigatorCallback(String screenName, ScreenNavigatorCallback screenNavigatedCallback) {
    if (mapOfScreenNavigatedCallbacks.containsKey(screenName)) {
      mapOfScreenNavigatedCallbacks.remove(screenName);
    }
  }

  @Override
  public void onPropertyCacheCleared(@NonNull String navigatedScreen) {
    Logger.d(WEConstants.TAG, "WEPluginCallbackHandler: onPropertyCacheCleared: Screen changed! to " + navigatedScreen);
    currentScreen = navigatedScreen;
    WEPropertyRegistry.get().clearCacheData();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

      HashMap<String, ScreenNavigatorCallback> callbacksList = mapOfScreenNavigatedCallbacks.get(navigatedScreen);
      try {
        if (callbacksList != null) {
          for (String propertyKey : callbacksList.keySet()) {
            ScreenNavigatorCallback propertyCallback = callbacksList.get(propertyKey);
            propertyCallback.screenNavigated(navigatedScreen);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}
