package com.webengagepersonalization;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
//import android.os.Handler.Callback;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.webengage.personalization.WEPersonalization;
import com.webengage.personalization.callbacks.WEPlaceholderCallback;
import com.webengage.personalization.data.WECampaignData;
//import  androidx.annotation..widget.CardView;
import com.webengage.personalization.utils.ConstantsKt;
import com.webengage.sdk.android.WebEngage;
import com.webengagepersonalization.Views.Registry;
import com.webengagepersonalization.InlineWidget;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import javax.annotation.Nullable;


public class WebengagePersonalizationViewManager extends SimpleViewManager<ViewGroup> implements WEPlaceholderCallback {
  public static final String REACT_CLASS = "WebengagePersonalizationView";
  private ReactApplicationContext applicationContext = null;

  private InlineWidget simpleUi;
  int width, height;

  public WebengagePersonalizationViewManager(ReactApplicationContext reactContext) {
    super();
    this.applicationContext = reactContext;
    WebEngage.get().analytics().screenNavigated("ET_home");
    SharedPreferences sharedPrefsManager = applicationContext.getSharedPreferences(ConstantsKt.WE_SHARED_STORAGE, Context.MODE_PRIVATE);
    sharedPrefsManager.edit().putBoolean(ConstantsKt.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS, false).apply();
    WEPersonalization.Companion.get().init(); // Initializing Personalization SDK
  }

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  @NonNull
  public ViewGroup createViewInstance(ThemedReactContext reactContext) {
    Log.d("Ak","Instance created");
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("height", height);
    map.put("width", width);

     simpleUi = new InlineWidget(reactContext.getReactApplicationContext(),map,this);
    return simpleUi;
//    return new LinearLayout(reactContext) {
//    };
  }


  @ReactPropGroup(names = {"width", "height"}, customType = "Style")
  public void setStyle(View view, int index, int value) {
Log.d("Ak1","index - "+index+ "\n Val - "+value);
    if (index == 0) {
      width = value;
    }

    if (index == 1) {
      height = value;
    }
    simpleUi.updateStyle(height, width);
  }


  @ReactProp(name = "color")
  public void setColor(View view, String color) {
    Log.d("WebEngage", "inside color data -> ");
//    WebEngage.get().analytics().screenNavigated("list-screen");
//    WEPersonalization.Companion.get().registerWEPlaceholderCallback("ak_test_2", this); // Custom View
//    WEPersonalization.Companion.get().registerWEPlaceholderCallback("placeholder_1", this);  // Text View
//    WEPersonalization.Companion.get().registerWEPlaceholderCallback("1", this);  // Text View
//    view.setBackgroundColor(Color.parseColor(color));
  }

  @ReactProp(name = "screenName")
  public void setScreenName(View view, String screenName) {
    Log.d("WebEngage", "Screen Name to register -> "+screenName);
    Registry.getInstance().setScreenName(screenName);
    Map<String, String> data = Registry.getInstance().getRegistryData();
    Log.d("Akshay", data.toString());
  }

  @ReactProp(name = "propertyId")
  public void setPropertyId(View view, String propertyId) {
    Log.d("WebEngage", "PropertyId to register -> "+propertyId);
    Registry.getInstance().setPropertyId(propertyId);
    simpleUi.updateViewTag(propertyId);

  }




  public Map getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.builder().put(
      "topChange",
      MapBuilder.of(
        "phasedRegistrationNames",
        MapBuilder.of("bubbled", "onChange")
      )
    ).build();
  }

  @Override
  public @Nullable Map getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.of(
      "personalizationCallback",
      MapBuilder.of("registrationName", "personalizationCallback")
      );
  }

  @Override
  public void onDataReceived(WECampaignData weCampaignData) {
    Log.d("WebEngage1", "OnDataReceived from personalization view manager - "+weCampaignData);
  }

  @Override
  public void onPlaceholderException(String s, String s1, Exception e) {
    Log.d("WebEngage1", "onPlaceholderException from personalization view manager-> \ns- "+s+"\ns1- "+s1 + "\nerror-"+e);

  }

  @Override
  public void onRendered(WECampaignData weCampaignData) {
    Log.d("WebEngage1", "onRendered from personalization view manager");

  }
}
