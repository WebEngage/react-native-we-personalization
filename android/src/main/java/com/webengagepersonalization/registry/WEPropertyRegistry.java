package com.webengagepersonalization.regisrty;

import com.webengage.sdk.android.Logger;
import com.webengagepersonalization.utils.WEConstants;

import java.util.ArrayList;

public class WEPropertyRegistry {
  private static WEPropertyRegistry instance = null;
  ArrayList<String> impressionTrackedForTargetViews = new ArrayList<>();

  private static final Object lock  = new Object();


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
    String value = targetViewId + "_" + campaignId;
    impressionTrackedForTargetViews.add(value);
  }

  public Boolean isImpressionAlreadyTracked(String targetViewId, String campaignId) {
    String value = targetViewId + "_" + campaignId;
    Boolean flag = impressionTrackedForTargetViews.contains(value);
    Logger.d(WEConstants.TAG,"trackImpression: isImpressionAlreadyTracked "+flag);
    return flag;
  }

  public void clearCacheData() {
    impressionTrackedForTargetViews.clear();
    Logger.d(WEConstants.TAG,"trackImpression: Clear impression tracked data ");

  }
}
