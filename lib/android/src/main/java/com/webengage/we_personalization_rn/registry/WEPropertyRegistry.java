package com.webengage.we_personalization_rn.registry;

import com.webengage.sdk.android.Logger;
import com.webengage.we_personalization_rn.utils.WEConstants;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WEPropertyRegistry {
  private static volatile WEPropertyRegistry instance;
  private final Set<String> impressionTrackedForTargetViews = ConcurrentHashMap.newKeySet();

  private static final Object lock = new Object();


  public static WEPropertyRegistry get() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new WEPropertyRegistry();
        }
      }
    }
    return instance;
  }

  public void setImpressionTrackedDetails(String targetViewId, String campaignId) {
    if (targetViewId == null || campaignId == null) {
      Logger.d(WEConstants.TAG, "[WE-Inline-Android] setImpressionTracked: null parameter");
      return;
    }
      String value = targetViewId + "_" + campaignId;
      impressionTrackedForTargetViews.add(value);
  }

  public boolean isImpressionAlreadyTracked(String targetViewId, String campaignId) {
    if (targetViewId == null || campaignId == null) {
      Logger.d(WEConstants.TAG, "[WE-Inline-Android] isImpressionTracked: null parameter");
      return false;
    }
      String value = targetViewId + "_" + campaignId;
      boolean flag = impressionTrackedForTargetViews.contains(value);
      Logger.d(WEConstants.TAG, "[WE-Inline-Android] isImpressionTracked: property=" + targetViewId + ", campaign=" + campaignId + ", tracked=" + flag);
      return flag;
  }

  public void clearCacheData() {
    int count = impressionTrackedForTargetViews.size();
    impressionTrackedForTargetViews.clear();
    Logger.d(WEConstants.TAG, "[WE-Inline-Android] clearCache: cleared " + count + " impressions");

  }
}
