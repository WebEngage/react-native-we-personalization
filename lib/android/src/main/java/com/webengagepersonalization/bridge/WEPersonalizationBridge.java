package com.webengagepersonalization.bridge;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.webengage.personalization.WEPersonalization;
import com.webengage.personalization.callbacks.WECampaignCallback;
import com.webengage.personalization.callbacks.WEPlaceholderCallback;
import com.webengage.personalization.data.WECampaignData;
import com.webengage.sdk.android.Logger;
import com.webengagepersonalization.utils.WEUtils;
import com.webengagepersonalization.utils.WEConstants;
import com.webengagepersonalization.registry.WECustomPropertyRegistry;

import androidx.annotation.Nullable;

@ReactModule(name = WEConstants.PERSONALIZATION_BRIDGE)
public class WEPersonalizationBridge extends ReactContextBaseJavaModule implements WEPlaceholderCallback, WECampaignCallback {
  private ReactApplicationContext applicationContext = null;

  public WEPersonalizationBridge(ReactApplicationContext reactContext) {
    super(reactContext);
    this.applicationContext = reactContext;
    WEPersonalization.Companion.get().init();
  }

  @ReactMethod
  public void initWePersonalization() {}
  
  @ReactMethod
  public void registerProperty(String tagName, String screenName) {
    WEPersonalization.Companion.get().registerWEPlaceholderCallback(tagName, this);
  }

  @ReactMethod
  public void deregisterProperty(String tagName) {
    WEPersonalization.Companion.get().unregisterWEPlaceholderCallback(tagName);
  }


  @ReactMethod
  public void registerWECampaignCallback() {
    WEPersonalization.Companion.get().registerWECampaignCallback(this);
  }

  @ReactMethod
  public void deregisterWECampaignCallback() {
    WEPersonalization.Companion.get().unregisterWECampaignCallback(this);
  }

  @ReactMethod
  public void trackImpression(String propertyId, ReadableMap attributes) {
    WECampaignData weCampaignData = WECustomPropertyRegistry.get().getMapData(propertyId);
    if (weCampaignData != null) {
      weCampaignData.trackImpression(WEUtils.convertHybridMapToNativeMap(attributes));
    }
  }

  @ReactMethod
  public void trackClick(String propertyId, ReadableMap attributes) {
    WECampaignData weCampaignData = WECustomPropertyRegistry.get().getMapData(propertyId);
    if (weCampaignData != null) {
      weCampaignData.trackClick(WEUtils.convertHybridMapToNativeMap(attributes));
    }
  }

  @Override
  public boolean onCampaignClicked(@NonNull String actionId, @NonNull String deepLink, @NonNull WECampaignData weCampaignData) {
    Logger.d(WEConstants.TAG, "WEPersonalizationBridge: onCampaignClicked actionId- " + actionId + " \n deepLink- " + deepLink);
    WritableMap params = Arguments.createMap();
    params = WEUtils.generateParams(actionId, deepLink, weCampaignData);
    WEUtils.sendEventToHybrid(applicationContext, "onCampaignClicked", params);
    return true;
  }

  @Override
  public void onCampaignException(@Nullable String campaignId, @NonNull String targetViewId, @NonNull Exception e) {
    Logger.d(WEConstants.TAG, "WEPersonalizationBridge: onCampaignException " + targetViewId);
    WritableMap params = Arguments.createMap();
    params = WEUtils.generateParams(campaignId, targetViewId, e);
    WEUtils.sendEventToHybrid(applicationContext, "onCampaignException", params);
  }

  @Nullable
  @Override
  public WECampaignData onCampaignPrepared(@NonNull WECampaignData weCampaignData) {
    Logger.d(WEConstants.TAG, "WEPersonalizationBridge: onCampaignPrepared " + weCampaignData.getTargetViewId());
    WritableMap params = Arguments.createMap();
    params = WEUtils.generateParams(weCampaignData);
    WEUtils.sendEventToHybrid(applicationContext, "onCampaignPrepared", params);
    return null;
  }

  @Override
  public void onCampaignShown(@NonNull WECampaignData weCampaignData) {
    Logger.d(WEConstants.TAG, "WEPersonalizationBridge: onCampaignShown " + weCampaignData.getTargetViewId());
    WritableMap params = Arguments.createMap();
    params = WEUtils.generateParams(weCampaignData);
    WEUtils.sendEventToHybrid(applicationContext, "onCampaignShown", params);
  }

  @Override
  @NonNull
  public String getName() {
    return WEConstants.PERSONALIZATION_BRIDGE;
  }

  @Override
  public void onDataReceived(WECampaignData weCampaignData) {
    WritableMap params = Arguments.createMap();
    params = WEUtils.generateParams(weCampaignData);
    String targetView = weCampaignData.getTargetViewId();
    WECustomPropertyRegistry.get().registerProperty(targetView, weCampaignData);
    WEUtils.sendEventToHybrid(applicationContext, "onCustomDataReceived", params);
  }

  @Override
  public void onPlaceholderException(String campaignId, String targetViewId, Exception e) {
    WritableMap params = Arguments.createMap();
    params = WEUtils.generateParams(campaignId, targetViewId, e);
    WEUtils.sendEventToHybrid(applicationContext, "onCustomPlaceholderException", params);
  }

  @Override
  public void onRendered(WECampaignData weCampaignData) {
  }
}
