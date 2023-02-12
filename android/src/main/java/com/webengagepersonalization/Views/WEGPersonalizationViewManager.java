package com.webengagepersonalization.Views;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.HashMap;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
//import android.os.Handler.Callback;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.webengage.personalization.WEPersonalization;
//import  androidx.annotation..widget.CardView;
import com.webengage.personalization.utils.ConstantsKt;
import com.webengagepersonalization.Utils.Logger;
import com.webengagepersonalization.Utils.WEGConstants;
import com.webengagepersonalization.Views.WEHInlineWidget;
import com.webengagepersonalization.handler.Callbacker;

import android.view.ViewGroup;

public class WEGPersonalizationViewManager extends SimpleViewManager<ViewGroup> {
  private ReactApplicationContext applicationContext = null;
  int width, height;
  String screenName, propertyId;

  public WEGPersonalizationViewManager(ReactApplicationContext reactContext) {
    super();
    this.applicationContext = reactContext;
    SharedPreferences sharedPrefsManager = applicationContext.getSharedPreferences(ConstantsKt.WE_SHARED_STORAGE, Context.MODE_PRIVATE);
    sharedPrefsManager.edit().putBoolean(ConstantsKt.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS, false).apply();
    WEPersonalization.Companion.get().init();
    WEPersonalization.Companion.get().registerPropertyRegistryCallback(new Callbacker());
  }

  @Override
  @NonNull
  public String getName() {
    return WEGConstants.REACT_CLASS;
  }

//  Called After all the props update
  @Override
  public void updateProperties(@NonNull ViewGroup viewToUpdate, ReactStylesDiffMap props) {
    super.updateProperties(viewToUpdate, props);
    WEHInlineWidget simpleUi = ((WEHInlineWidget) viewToUpdate);
    simpleUi.updateProperties(this.screenName, this.propertyId);
  }

  @Override
  @NonNull
  public ViewGroup createViewInstance(ThemedReactContext reactContext) {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put(WEGConstants.HEIGHT, height);
    map.put(WEGConstants.WIDTH, width);
    WEHInlineWidget simpleUi;
    simpleUi = new WEHInlineWidget(reactContext.getReactApplicationContext(),map,this);
    return simpleUi;
  }

  @ReactPropGroup(names = {WEGConstants.WIDTH, WEGConstants.HEIGHT}, customType = WEGConstants.STYLE)
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

  @ReactProp(name = WEGConstants.PROPERTY_ID)
  public void setPropertyId(View view, String propertyId) {
    this.propertyId = propertyId;

  }

  @ReactProp(name = WEGConstants.SCREEN_NAME)
  public void setScreenName(View view, String screenName) {
    this.screenName = screenName;
  }


}
