package com.webengagepersonalization.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.webengagepersonalization.regisrty.WEPropertyRegistry;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.webengage.personalization.WEInlineView;
import com.webengage.personalization.callbacks.WEPlaceholderCallback;
import com.webengage.personalization.data.WECampaignData;
import com.webengage.sdk.android.Logger;
import com.webengagepersonalization.handler.WEPluginCallbackHandler;
import com.webengagepersonalization.R;
import com.webengagepersonalization.utils.WEUtils;
import com.webengagepersonalization.utils.WEConstants;
import com.webengagepersonalization.model.ScreenNavigatorCallback;

import java.util.HashMap;

public class WEInlineWidget extends FrameLayout implements ScreenNavigatorCallback {
  WEInlineView weInlineView;
  private ReactApplicationContext applicationContext = null;
  int height = 0, width = 0;
  String tagName = "", screenName = "";

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    Logger.d(WEConstants.TAG, "WEInlineWidget: onAttachedToWindow: " + tagName);
    WEPluginCallbackHandler.setScreenNavigatorCallback(this.screenName, this.tagName, this);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    Logger.d(WEConstants.TAG, "WEInlineWidget: onDetachedFromWindow: " + tagName);
    WEPluginCallbackHandler.removeScreenNavigatorCallback(this.screenName, this);
    View view = weInlineView.findViewWithTag("INLINE_PERSONALIZATION_TAG");
    if (view != null) {
      weInlineView.removeView(view);
    }
  }

  public WEInlineWidget(@NonNull ReactApplicationContext context, HashMap<String, Object> map, WEPersonalizationViewManager ref) {
    super(context);
    this.applicationContext = context;
    init(context);
  }

  public void init(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.view_inlinewidget, this, false);
    weInlineView = view.findViewById(R.id.weinline_widget);
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
        boolean isScreenVisible = WEUtils.isInlineWidgetVisible(view);
        if (!isScreenVisible) {
          viewTreeObserver.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
              boolean isUserScreenVisible = WEUtils.isInlineWidgetVisible(view);
              if (isUserScreenVisible) {
                trackImpression(weCampaignData);
                Logger.d(WEConstants.TAG, "WEInlineWidget: Impression tracked for  " + tagName);
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
    if (!WEPropertyRegistry.get().isImpressionAlreadyTracked(targetViewId, campaignId)) {
      weCampaignData.trackImpression(null);
      WEPropertyRegistry.get().setImpressionTrackedDetails(targetViewId, campaignId);
      Logger.d(WEConstants.TAG, "trackImpression: tracked " + targetViewId);
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

//    Size of the view - applied to WEInlineWidget
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
    Logger.d(WEConstants.TAG, "WEInlineWidget: loadView called for - " + tagName);
    weInlineView.load(tagName, new WEPlaceholderCallback() {
      @Override
      public void onDataReceived(WECampaignData weCampaignData) {
        Logger.d(WEConstants.TAG, "WEInlineWidget: onDataReceived called " + weCampaignData.getTargetViewId());
        WritableMap params = Arguments.createMap();
        params = WEUtils.generateParams(weCampaignData);
        WEUtils.sendEventToHybrid(applicationContext, "onDataReceived", params);
      }

      @Override
      public void onPlaceholderException(String campaignId, String targetViewId, Exception e) {
        Logger.d(WEConstants.TAG, "WEInlineWidget: onPlaceholderException from personalization view manager-> \ncampaignId- " + campaignId + "\ntargetViewId- " + targetViewId + "\nerror-" + e);
        WritableMap params = Arguments.createMap();
        params = WEUtils.generateParams(campaignId, targetViewId, e);
        WEUtils.sendEventToHybrid(applicationContext, "onPlaceholderException", params);
      }

      @Override
      public void onRendered(WECampaignData weCampaignData) {
        Logger.d(WEConstants.TAG, "WEInlineWidget: onRendered called " + weCampaignData.getTargetViewId());
        WritableMap params = Arguments.createMap();
        params = WEUtils.generateParams(weCampaignData);
        WEUtils.sendEventToHybrid(applicationContext, "onRendered", params);
        View view = null;
        if (weInlineView.getChildCount() > 1) {
          view = weInlineView.getChildAt(weInlineView.getChildCount() - 1);
        } else {
          view = weInlineView.findViewWithTag("INLINE_PERSONALIZATION_TAG");
        }
        if (view != null) {
          setupLayout(view, weCampaignData);
        }
      }
    });
  }

  @Override
  public void screenNavigated(String screenName) {
    Logger.d(WEConstants.TAG, "WEInlineWidget: screenNavigated  called for screen - " + screenName + " for tagName- " + this.tagName);
    if (!this.tagName.equals("")) {
      loadView(this.tagName);
    }
  }

}
