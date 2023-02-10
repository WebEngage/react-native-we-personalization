package com.webengagepersonalization;

import android.content.Context;
import android.os.Build;
import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;

import com.webengage.personalization.callbacks.WEPropertyRegistryCallback;
import com.webengage.sdk.android.AmplifyController;
import com.webengage.sdk.android.CallbackDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Callbacker implements WEPropertyRegistryCallback {
  private static String currentScreen = null;
  static HashMap<String, HashMap<String, ScreenNavigatorCallback>> mapOfScreenNavigatedCallbacks = new HashMap<>();

  public static void setScreenNavigatorCallback(String screenName, String propertyId, ScreenNavigatorCallback screenNavigatedCallback) {
    HashMap<String, ScreenNavigatorCallback> callback = new HashMap<>();
    if (mapOfScreenNavigatedCallbacks.containsKey(screenName)) {
      callback = mapOfScreenNavigatedCallbacks.get(screenName);
      if(!callback.containsKey(propertyId)) {
        callback.put(propertyId, screenNavigatedCallback);
      }
    } else {
      callback.put(propertyId, screenNavigatedCallback);
    }
    if(screenName.equals(currentScreen)){
      screenNavigatedCallback.screenNavigated(screenName);
    }

    mapOfScreenNavigatedCallbacks.put(screenName, callback);
    Logger.d(WEGConstants.TAG, "Callbacker: setScreenNavigatorCallback called for screen- "+screenName+" propertyName "+propertyId);
  }

  public static void removeScreenNavigatorCallback(String screenName, ScreenNavigatorCallback screenNavigatedCallback) {
    if(mapOfScreenNavigatedCallbacks.containsKey(screenName)) {
      Logger.d(WEGConstants.TAG, "mapOfScreenNavigatedCallbacks contains screenKey going to remove it");
      mapOfScreenNavigatedCallbacks.remove(screenName);
    }
  }

  @Override
  public void onPropertyCacheCleared(@NonNull String navigatedScreen) {
    Logger.d(WEGConstants.TAG, "\n\n");
    Logger.d(WEGConstants.TAG, "\n\n################################# \n\n");
    Logger.d(WEGConstants.TAG, "onPropertyCacheCleared: Screen changed! onPropertyCacheCleared called inside callbacker - "+navigatedScreen);
    currentScreen = navigatedScreen;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        HashMap<String, ScreenNavigatorCallback> callbacksList = mapOfScreenNavigatedCallbacks.get(navigatedScreen);
        try {
          if(callbacksList != null) {
            for (String propertyKey : callbacksList.keySet()) {
              Logger.d(WEGConstants.TAG, "PropertyKey - " + propertyKey);
              ScreenNavigatorCallback propertyCallback = callbacksList.get(propertyKey);
              propertyCallback.screenNavigated(navigatedScreen);
            }
          } else {
            Logger.d(WEGConstants.TAG, "No Properties registered for the screen name - "+navigatedScreen);
          }
        } catch (Exception e) {
          Logger.d(WEGConstants.TAG, "Exception caught - "+e.toString());
        }
    }
  }
}
