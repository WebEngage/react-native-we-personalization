package com.webengagepersonalization.regisrty;

import android.os.Build;

import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengagepersonalization.Utils.WEGConstants;

import java.util.ArrayList;

public class DataRegistry {
  private static DataRegistry instance = null;
  ArrayList<String> impressionTrackedForTargetViews = new ArrayList<>();

  private static final Object lock  = new Object();


  public static DataRegistry get() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new DataRegistry();
        }
      }
    }
    return instance;
  }

  public void setImpressionTrackedDetails(String targetViewId, String campaignId) {
    String value = targetViewId + "_" + campaignId;
    Logger.d(WEGConstants.TAG, "DataRegistry: value -> "+value);
    impressionTrackedForTargetViews.add(value);
  }

  public Boolean isImpressionAlreadyTracked(String targetViewId, String campaignId) {
    String value = targetViewId + "_" + campaignId;
    Boolean flag = impressionTrackedForTargetViews.contains(value);
    Logger.d(WEGConstants.TAG,"trackImpression: isImpressionAlreadyTracked "+flag);
    return flag;
  }

  public void clearCacheData() {
    impressionTrackedForTargetViews.clear();
    Logger.d(WEGConstants.TAG,"trackImpression: clearCacheData ");

  }
}
