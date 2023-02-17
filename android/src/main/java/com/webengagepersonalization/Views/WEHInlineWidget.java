package com.webengagepersonalization.Views;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.webengage.personalization.WEInlineView;
import com.webengage.personalization.callbacks.WECampaignCallback;
import com.webengage.personalization.callbacks.WEPlaceholderCallback;
import com.webengage.personalization.data.WECampaignData;
import com.webengagepersonalization.handler.CallbackHandler;
import com.webengagepersonalization.R;
import com.webengagepersonalization.Utils.Logger;
import com.webengagepersonalization.Utils.Utils;
import com.webengagepersonalization.Utils.WEGConstants;
import com.webengagepersonalization.model.ScreenNavigatorCallback;
import com.webengagepersonalization.regisrty.DataRegistry;

import org.json.JSONObject;

import java.util.HashMap;

public class WEHInlineWidget extends FrameLayout implements WECampaignCallback, ScreenNavigatorCallback {
  private static WEHInlineWidget instance = null;
  WEInlineView weInlineView;
  private ReactApplicationContext applicationContext = null;
  int height = 0, width = 0;
  String tagName = "", screenName = "";

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    Logger.d(WEGConstants.TAG, "WEHInlineWidget: onAttachedToWindow: " + tagName);
    CallbackHandler.setScreenNavigatorCallback(this.screenName, this.tagName, this);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    Logger.d(WEGConstants.TAG, "WEHInlineWidget: onDetachedFromWindow: " + tagName);

    CallbackHandler.removeScreenNavigatorCallback(this.screenName, this);
    View view = weInlineView.findViewWithTag("INLINE_PERSONALIZATION_TAG");
    if (view != null) {
      weInlineView.removeView(view);
    }
  }

  public WEHInlineWidget(@NonNull ReactApplicationContext context, HashMap<String, Object> map, WEGPersonalizationViewManager ref) {
    super(context);
    this.applicationContext = context;
    init(context);
  }

  public void init(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.view_inlinewidget, this, false);
    weInlineView = view.findViewById(R.id.weinline_widget);
    Logger.d(WEGConstants.TAG, "WEHInlineWidget: Init called " + tagName);
    addView(view);
  }

  @Override
  public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
    super.addOnLayoutChangeListener(listener);
  }

  //  Enforce this to reflect new Changes to the UI
  public void setupLayout(View view, WECampaignData weCampaignData) {
    Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
      @Override
      public void doFrame(long frameTimeNanos) {
        manuallyLayoutChildren(view, weCampaignData);
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.dispatchOnGlobalLayout();
        boolean isScreenVisible = Utils.isVisible(view);
        if (!isScreenVisible) {
          viewTreeObserver.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
              boolean isUserScreenVisible = Utils.isVisible(view);
              if (isUserScreenVisible) {
                trackImpression(weCampaignData);
                Logger.d(WEGConstants.TAG, "WEHInlineWidget: Impression tracked1 for  " + tagName);
                view.getViewTreeObserver().removeOnScrollChangedListener(this);
              }
            }
          });
        } else {
            trackImpression(weCampaignData);
        }
      }
    });
  }

  public void trackImpression(WECampaignData weCampaignData) {
    String targetViewId = weCampaignData.getTargetViewId();
    String campaignId = weCampaignData.getCampaignId();
    if(!DataRegistry.get().isImpressionAlreadyTracked(targetViewId, campaignId)) {
      weCampaignData.trackImpression(null);
      DataRegistry.get().setImpressionTrackedDetails(targetViewId, campaignId);
      Logger.d(WEGConstants.TAG, "trackImpression: tracked "+targetViewId);
    }
  }

  public void manuallyLayoutChildren(View view, WECampaignData weCampaignData) {
    LayoutParams lp = (LayoutParams) view.getLayoutParams();
    int lm = lp.getMarginStart();
    int rm = lp.getMarginEnd();
    int tm = lp.topMargin;
    int bm = lp.bottomMargin;
    int width = this.width;
    int height = this.height;
    Resources r = getResources();
    int heightInPixel = Math.round((TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      height,
      r.getDisplayMetrics()
    )));
    int widthInPixel = Math.round((TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      width,
      r.getDisplayMetrics()
    )));

//    Removing Margins for horizontal and vertical
    heightInPixel -= (bm + tm);
    widthInPixel -= (rm + lm);

//    Size of the view
    view.measure(
      View.MeasureSpec.makeMeasureSpec(widthInPixel, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(heightInPixel, View.MeasureSpec.EXACTLY));

//    Positioning of the view - including the margin
   view.layout(weInlineView.getLeft() + lm, weInlineView.getTop() + tm,
     widthInPixel + rm, heightInPixel + bm);

  }

  public void updateProperties(String screenName, String propertyId) {
    this.screenName = screenName;
    this.tagName = propertyId;
  }


  public void updateStyle(int heights, int widths) {
    height = heights;
    width = widths;
  }

  public void loadView(String tagName) {
    Logger.d(WEGConstants.TAG, "loadView called for - " + tagName);
    weInlineView.load(tagName, new WEPlaceholderCallback() {
      @Override
      public void onDataReceived(WECampaignData weCampaignData) {
        Logger.d(WEGConstants.TAG,"WEHInlineWidget: onDataReceived called " + weCampaignData.getTargetViewId() );
        WritableMap params = Arguments.createMap();
        params = Utils.generateParams(weCampaignData);
        Utils.sendEvent(applicationContext, "onDataReceived", params);
      }

      @Override
      public void onPlaceholderException(String campaignId, String targetViewId, Exception e) {
        Logger.d(WEGConstants.TAG, "WEHInlineWidget: onPlaceholderException from personalization view manager-> \ncampaignId- " + campaignId + "\ntargetViewId- " + targetViewId + "\nerror-" + e);
        WritableMap params = Arguments.createMap();
        params = Utils.generateParams(campaignId, targetViewId, e);
        Utils.sendEvent(applicationContext, "onPlaceholderException", params);

      }

      @Override
      public void onRendered(WECampaignData weCampaignData) {
        Logger.d(WEGConstants.TAG,"WEHInlineWidget: onRendered called " + weCampaignData.getTargetViewId() );
        WritableMap params = Arguments.createMap();
        params = Utils.generateParams(weCampaignData);
        Utils.sendEvent(applicationContext, "onRendered", params);
        View view = weInlineView.findViewWithTag("INLINE_PERSONALIZATION_TAG");
        if (view != null) {
          setupLayout(view, weCampaignData);
        }
      }
    });
  }

//  TODO - Check if this has to be kept for custom callback
  public void registerCallback(String tagName) {
//    WEPersonalization.Companion.get().registerWEPlaceholderCallback(tagName, this);
  }


  //  TODO - Check with  Milind if it has to be visible at hybrid for the below methods
  @Override
  public boolean onCampaignClicked(@NonNull String s, @NonNull String s1, @NonNull WECampaignData weCampaignData) {
    Logger.d(WEGConstants.TAG, "onCampaignClicked shown ---- " + weCampaignData);
    weCampaignData.trackClick(null);
    return false;
  }

  @Override
  public void onCampaignException(@Nullable String s, @NonNull String s1, @NonNull Exception e) {
    Logger.d(WEGConstants.TAG, "onCampaignException shown ---- " + e);
  }

  @Nullable
  @Override
  public WECampaignData onCampaignPrepared(@NonNull WECampaignData weCampaignData) {
    Logger.d(WEGConstants.TAG, "onCampaignPrepared shown ---- " + weCampaignData);
    return null;
  }

  @Override
  public void onCampaignShown(@NonNull WECampaignData weCampaignData) {
    Logger.d(WEGConstants.TAG, "Campaign data shown ---- " + weCampaignData);
  }

  @Override
  public void screenNavigated(String screenName) {
    Logger.d(WEGConstants.TAG, "WEHInlineWidget1: screenNavigated  called for screen - " + screenName + " for tagName- " + this.tagName);
    if (!this.tagName.equals("")) {
      loadView(this.tagName);
    }
  }

}
