package com.webengagepersonalization.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.HashMap;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.webengage.personalization.WEPersonalization;
import com.webengagepersonalization.utils.WEConstants;
import com.webengagepersonalization.handler.WEPluginCallbackHandler;
import android.view.ViewGroup;

public class WEPersonalizationViewManager extends SimpleViewManager<ViewGroup> {
  private ReactApplicationContext applicationContext = null;
  int width, height;
  String screenName, propertyId;

  public WEPersonalizationViewManager(ReactApplicationContext reactContext) {
    super();
    this.applicationContext = reactContext;
    SharedPreferences sharedPrefsManager = applicationContext.getSharedPreferences(WEConstants.WE_SHARED_STORAGE, Context.MODE_PRIVATE);
    sharedPrefsManager.edit().putBoolean(WEConstants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS, false).apply();
    WEPersonalization.Companion.get().registerPropertyRegistryCallback(new WEPluginCallbackHandler());
  }

  @Override
  @NonNull
  public String getName() {
    return WEConstants.REACT_CLASS;
  }

  @Override
  public void updateProperties(@NonNull ViewGroup viewToUpdate, ReactStylesDiffMap props) {
    super.updateProperties(viewToUpdate, props);
    WEInlineWidget simpleUi = ((WEInlineWidget) viewToUpdate);
    simpleUi.updateProperties(this.screenName, this.propertyId);
  }

  @Override
  @NonNull
  public ViewGroup createViewInstance(ThemedReactContext reactContext) {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put(WEConstants.HEIGHT, height);
    map.put(WEConstants.WIDTH, width);
    WEInlineWidget simpleUi;
    simpleUi = new WEInlineWidget(reactContext.getReactApplicationContext(),map,this);
    return simpleUi;
  }

  @ReactPropGroup(names = {WEConstants.WIDTH, WEConstants.HEIGHT}, customType = WEConstants.STYLE)
  public void setStyle(View view, int index, int value) {
    if (index == 0) {
      width = value;
    }
    if (index == 1) {
      height = value;
    }
    WEInlineWidget simpleUi = ((WEInlineWidget) view);
    simpleUi.updateStyle(height, width);
  }

  @ReactProp(name = WEConstants.PROPERTY_ID)
  public void setPropertyId(View view, String propertyId) {
    this.propertyId = propertyId;

  }

  @ReactProp(name = WEConstants.SCREEN_NAME)
  public void setScreenName(View view, String screenName) {
    this.screenName = screenName;
  }

}
