package com.webengagepersonalization;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
//import android.os.Handler.Callback;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.webengage.personalization.WEInlineView;
import com.webengage.personalization.WEPersonalization;
//import  androidx.annotation..widget.CardView;
import com.webengage.personalization.callbacks.WEPropertyRegistryCallback;
import com.webengage.personalization.utils.ConstantsKt;

import android.view.ViewGroup;

//  TODO  - Test scenario for screen back navigation
public class WEGPersonalizationViewManager extends SimpleViewManager<ViewGroup> {
  private ReactApplicationContext applicationContext = null;

  int width, height;
  String screenName, propertyId;

  public WEGPersonalizationViewManager(ReactApplicationContext reactContext) {
    super();
    Logger.d("WebEngage", "WEGPersonalizationViewManager called @@@");

    this.applicationContext = reactContext;
    SharedPreferences sharedPrefsManager = applicationContext.getSharedPreferences(ConstantsKt.WE_SHARED_STORAGE, Context.MODE_PRIVATE);
    sharedPrefsManager.edit().putBoolean(ConstantsKt.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS, false).apply();
    WEPersonalization.Companion.get().init(); // Initializing Personalization SDK - TODO - Give it to user
    WEPersonalization.Companion.get().registerPropertyRegistryCallback(new Callbacker()); // added callback listener
  }

  @Override
  @NonNull
  public String getName() {
    return WEGConstants.REACT_CLASS;
  }

  @Override
  public void updateProperties(@NonNull ViewGroup viewToUpdate, ReactStylesDiffMap props) {
    super.updateProperties(viewToUpdate, props);
    Logger.d(WEGConstants.TAG, "updateProperties called for View manager "+ (viewToUpdate instanceof WEHInlineWidget));
    WEHInlineWidget simpleUi = ((WEHInlineWidget) viewToUpdate);
    simpleUi.updateProperties(this.screenName, this.propertyId);
  }

  @Override
  @NonNull
  public ViewGroup createViewInstance(ThemedReactContext reactContext) {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("height", height);
    map.put("width", width);
    WEHInlineWidget simpleUi;
    simpleUi = new WEHInlineWidget(reactContext.getReactApplicationContext(),map,this);
    return simpleUi;
  }

  @ReactPropGroup(names = {"width", "height"}, customType = "Style")
  public void setStyle(View view, int index, int value) {
    if (index == 0) {
      width = value;
    }

    if (index == 1) {
      height = value;
    }
    WEHInlineWidget simpleUi = ((WEHInlineWidget) view);
    simpleUi.updateStyle(height, width);
  }

  @ReactProp(name = "propertyId")
  public void setPropertyId(View view, String propertyId) {
    this.propertyId = propertyId;

  }

  @ReactProp(name = "screenName")
  public void setScreenName(View view, String screenName) {
    this.screenName = screenName;
  }

  @ReactMethod
  public void myMethod(String viewTag) {
    // Method implementation
    Logger.d("Akshay", "myMethod called");
  }

}
