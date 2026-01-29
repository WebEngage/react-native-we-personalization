package com.webengage.we_personalization_rn.bridge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.webengage.personalization.WEPersonalization;
import com.webengage.personalization.callbacks.WECampaignCallback;
import com.webengage.personalization.callbacks.WEPlaceholderCallback;
import com.webengage.personalization.data.WECampaignData;
import com.webengage.sdk.android.Logger;
import com.webengage.we_personalization_rn.registry.WECustomPropertyRegistry;
import com.webengage.we_personalization_rn.utils.WEConstants;
import com.webengage.we_personalization_rn.utils.WEUtils;

@ReactModule(name = WEConstants.PERSONALIZATION_BRIDGE)
public class WEPersonalizationBridge extends ReactContextBaseJavaModule implements WEPlaceholderCallback, WECampaignCallback {

  private final ReactApplicationContext applicationContext;

  public WEPersonalizationBridge(ReactApplicationContext reactContext) {
    super(reactContext);
    this.applicationContext = reactContext;
      WEPersonalization.Companion.get().init();
  }

  @ReactMethod
  public void initWePersonalization() {}
  
  @ReactMethod
  public void registerProperty(String tagName, String screenName) {
    if (tagName != null) {
      WEPersonalization.Companion.get().registerWEPlaceholderCallback(tagName, this);
    } else {
      Logger.d(WEConstants.TAG, "WEPersonalizationBridge: registerProperty - tagName not found");
    }
  }

  @ReactMethod
  public void deregisterProperty(String tagName) {
    if (tagName != null) {
      WEPersonalization.Companion.get().unregisterWEPlaceholderCallback(tagName);
    } else {
      Logger.d(WEConstants.TAG, "WEPersonalizationBridge: deregisterProperty - tagName not found");
    }
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
    if (propertyId == null) {
      Logger.d(WEConstants.TAG, "WEPersonalizationBridge: trackImpression - propertyId not found");
      return;
    }
      WECampaignData weCampaignData = WECustomPropertyRegistry.get().getMapData(propertyId);
      if (weCampaignData != null) {
        weCampaignData.trackImpression(WEUtils.convertHybridMapToNativeMap(attributes));
      } else {
        Logger.d(WEConstants.TAG, "WEPersonalizationBridge: trackImpression - weCampaignData not found for propertyId: " + propertyId);
    }
  }

  @ReactMethod
  public void trackClick(String propertyId, ReadableMap attributes) {
    if (propertyId == null) {
      Logger.d(WEConstants.TAG, "WEPersonalizationBridge: trackClick - propertyId not found");
      return;
    }
      WECampaignData weCampaignData = WECustomPropertyRegistry.get().getMapData(propertyId);
      if (weCampaignData != null) {
        weCampaignData.trackClick(WEUtils.convertHybridMapToNativeMap(attributes));
      } else {
        Logger.d(WEConstants.TAG, "WEPersonalizationBridge: trackClick - weCampaignData not found for propertyId: " + propertyId);
    }
  }

  @Override
  public boolean onCampaignClicked(@NonNull String actionId, @NonNull String deepLink, @NonNull WECampaignData weCampaignData) {
      Logger.d(WEConstants.TAG, "WEPersonalizationBridge: onCampaignClicked actionId- " + actionId + " \n deepLink- " + deepLink);
      WritableMap params = WEUtils.generateParams(actionId, deepLink, weCampaignData);
      WEUtils.sendEventToHybrid(applicationContext, "onCampaignClicked", params);
    return true;
  }

  @Override
  public void onCampaignException(@Nullable String campaignId, @NonNull String targetViewId, @NonNull Exception e) {
      Logger.d(WEConstants.TAG, "WEPersonalizationBridge: onCampaignException " + targetViewId);
      WritableMap params = WEUtils.generateParams(campaignId, targetViewId, e);
      WEUtils.sendEventToHybrid(applicationContext, "onCampaignException", params);
  }

  @Nullable
  @Override
  public WECampaignData onCampaignPrepared(@NonNull WECampaignData weCampaignData) {
      Logger.d(WEConstants.TAG, "WEPersonalizationBridge: onCampaignPrepared " + weCampaignData.getTargetViewId());
      WritableMap params = WEUtils.generateParams(weCampaignData);
      WEUtils.sendEventToHybrid(applicationContext, "onCampaignPrepared", params);
    return null;
  }

  @Override
  public void onCampaignShown(@NonNull WECampaignData weCampaignData) {
      Logger.d(WEConstants.TAG, "WEPersonalizationBridge: onCampaignShown " + weCampaignData.getTargetViewId());
      WritableMap params = WEUtils.generateParams(weCampaignData);
      WEUtils.sendEventToHybrid(applicationContext, "onCampaignShown", params);
  }

  @Override
  @NonNull
  public String getName() {
    return WEConstants.PERSONALIZATION_BRIDGE;
  }

  @Override
  public void onDataReceived(WECampaignData weCampaignData) {
    if (weCampaignData == null) {
      Logger.d(WEConstants.TAG, "WEPersonalizationBridge: onDataReceived - weCampaignData not found");
      return;
    }
      WritableMap params = WEUtils.generateParams(weCampaignData);
      String targetView = weCampaignData.getTargetViewId();
      if (targetView != null) {
        WECustomPropertyRegistry.get().registerProperty(targetView, weCampaignData);
      } else {
        Logger.d(WEConstants.TAG, "WEPersonalizationBridge: onDataReceived - targetView not found");
      }
      WEUtils.sendEventToHybrid(applicationContext, "onCustomDataReceived", params);
  }

  @Override
  public void onPlaceholderException(String campaignId, String targetViewId, Exception e) {
      WritableMap params = WEUtils.generateParams(campaignId, targetViewId, e);
      WEUtils.sendEventToHybrid(applicationContext, "onCustomPlaceholderException", params);
  }

  @Override
  public void onRendered(WECampaignData weCampaignData) {
    // No implementation needed as custom rendering is handled by client
  }
}
