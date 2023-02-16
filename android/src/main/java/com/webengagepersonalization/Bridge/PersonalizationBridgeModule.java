package com.webengagepersonalization.Bridge;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.webengage.personalization.WEPersonalization;
import com.webengage.personalization.callbacks.WECampaignCallback;
import com.webengage.personalization.callbacks.WEPlaceholderCallback;
import com.webengage.personalization.data.WECampaignData;
import com.webengage.sdk.android.WebEngage;
import com.webengagepersonalization.Utils.Logger;
import com.webengagepersonalization.Utils.Utils;
import com.webengagepersonalization.Utils.WEGConstants;
import com.webengagepersonalization.handler.CallbackHandler;

import androidx.annotation.Nullable;

import android.os.Handler;
import android.util.Log;

// TODO - This might be required for custom part
@ReactModule(name = WEGConstants.PERSONALIZATION_BRIDGE)
public class PersonalizationBridgeModule extends ReactContextBaseJavaModule implements WEPlaceholderCallback, WECampaignCallback {
  private ReactApplicationContext applicationContext = null;
  Boolean doesUserHandelCallbacks = false;

  public PersonalizationBridgeModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.applicationContext = reactContext;
    Logger.d("AKC", "PersonalizationBridgeModule");
    WEPersonalization.Companion.get().init();
  }
  @ReactMethod
  public void registerCallback(String tagName) {
    Logger.d(WEGConstants.TAG,"PersonalizationBridgeModule: registercallback "+tagName);
    WEPersonalization.Companion.get().registerWEPlaceholderCallback(tagName, this);
  }

  @ReactMethod
  public void unRegisterCallback(String tagName) {
    Logger.d(WEGConstants.TAG,"PersonalizationBridgeModule: unRegistercallback "+tagName);
    WEPersonalization.Companion.get().unregisterWEPlaceholderCallback(tagName);
  }


  @ReactMethod
  public void registerCampaignCallback(Boolean doesUserHandelCallback) {
    WEPersonalization.Companion.get().registerWECampaignCallback(this);
    this.doesUserHandelCallbacks = doesUserHandelCallback;
  }

  @ReactMethod
  public void unRegisterCampaignCallback() {
    Logger.d(WEGConstants.TAG,"PersonalizationBridgeModule: unRegisterCampaignCallback ");
    WEPersonalization.Companion.get().unregisterWECampaignCallback(this);
  }

  @Override
  public boolean onCampaignClicked(@NonNull String actionId, @NonNull String deepLink, @NonNull WECampaignData weCampaignData) {
    Logger.d(WEGConstants.TAG,"PersonalizationBridgeModule: onCampaignClicked actionId- "+actionId+ " \n deepLink- "+deepLink);
    WritableMap params = Arguments.createMap();
    params = Utils.generateParams(actionId, deepLink, weCampaignData);
    Utils.sendEvent(applicationContext, "onCampaignClicked", params);
    return this.doesUserHandelCallbacks;
  }

  @Override
  public void onCampaignException(@Nullable String campaignId, @NonNull String targetViewId, @NonNull Exception e) {
    Logger.d(WEGConstants.TAG,"PersonalizationBridgeModule: onCampaignException "+targetViewId);
    WritableMap params = Arguments.createMap();
    params = Utils.generateParams(campaignId, targetViewId, e);
    Utils.sendEvent(applicationContext, "onCampaignException", params);
  }

  @Nullable
  @Override
  public WECampaignData onCampaignPrepared(@NonNull WECampaignData weCampaignData) {
    Logger.d(WEGConstants.TAG,"PersonalizationBridgeModule: onCampaignPrepared "+weCampaignData.getTargetViewId());
    WritableMap params = Arguments.createMap();
    params = Utils.generateParams(weCampaignData);
    Utils.sendEvent(applicationContext, "onCampaignPrepared", params);
    return null;
  }

  @Override
  public void onCampaignShown(@NonNull WECampaignData weCampaignData) {
    Logger.d(WEGConstants.TAG,"PersonalizationBridgeModule: onCampaignShown "+weCampaignData.getTargetViewId());
    WritableMap params = Arguments.createMap();
    params = Utils.generateParams(weCampaignData);
    Utils.sendEvent(applicationContext, "onCampaignShown", params);
  }

  @Override
  @NonNull
  public String getName() {
    return WEGConstants.PERSONALIZATION_BRIDGE;
  }

  @Override
  public void onDataReceived(WECampaignData weCampaignData) {
    Log.d("WebEngage1", "OnDataReceived from personalization view manager - "+weCampaignData);
    WritableMap params = Arguments.createMap();
    params = Utils.generateParams(weCampaignData);
    Utils.sendEvent(applicationContext, "onCustomDataReceived", params);
  }

  @Override
  public void onPlaceholderException(String campaignId, String targetViewId, Exception e) {
    WritableMap params = Arguments.createMap();
    params = Utils.generateParams(campaignId, targetViewId, e);
    Utils.sendEvent(applicationContext, "onCustomPlaceholderException", params);
    Log.d("WebEngage1", "onCustomPlaceholderException from personalization view manager-> \ncampaignId- "+campaignId+"\ntargetViewId- "+targetViewId + "\nerror-"+e);
  }

  @Override
  public void onRendered(WECampaignData weCampaignData) {
    Log.d("WebEngage1", "onRendered from personalization view manager");
  }
}
