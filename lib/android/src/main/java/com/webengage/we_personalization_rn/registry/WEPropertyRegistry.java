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
      Logger.d(WEConstants.TAG, "WEPropertyRegistry: setImpressionTrackedDetails - null parameter");
      return;
    }
      String value = targetViewId + "_" + campaignId;
      impressionTrackedForTargetViews.add(value);
  }

  public boolean isImpressionAlreadyTracked(String targetViewId, String campaignId) {
    if (targetViewId == null || campaignId == null) {
      Logger.d(WEConstants.TAG, "WEPropertyRegistry: isImpressionAlreadyTracked - null parameter");
      return false;
    }
      String value = targetViewId + "_" + campaignId;
      boolean flag = impressionTrackedForTargetViews.contains(value);
      Logger.d(WEConstants.TAG, "trackImpression: isImpressionAlreadyTracked " + flag);
      return flag;
  }

  public void clearCacheData() {
    impressionTrackedForTargetViews.clear();
    Logger.d(WEConstants.TAG, "trackImpression: Clear impression tracked data ");

  }
}
