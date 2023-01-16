package com.webengagepersonalization;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.webengage.personalization.WEInlineView;
import com.webengage.personalization.callbacks.WEPlaceholderCallback;
import com.webengage.personalization.data.WECampaignData;

import java.util.HashMap;
import java.util.Map;

public class InlineWidget extends FrameLayout {
  private static InlineWidget instance = null;
  WEInlineView weInlineView;
  private ReactApplicationContext applicationContext = null;

  int height = 0, width = 0;


//  private Object WEInlineView;

  public InlineWidget(@NonNull ReactApplicationContext context, HashMap<String, Object> map, WebengagePersonalizationViewManager ref) {
    super(context);
    this.applicationContext = context;
    Log.d("Ak1", "Map inside widget -> "+map);
    init(context);
  }
  public void init(Context context) {
//    super();
    Log.d("WebEngage1", "inside Init method");
    View view = LayoutInflater.from(context).inflate(R.layout.view_inlinewidget, this, false);
    weInlineView =  view.findViewById(R.id.weinline_widget);
    addView(view);
  }

  public void updateStyle(int heights, int widths) {
    height = heights;
    width = widths;
    Log.d("Ak1", "LinearLayout updated vals -"+height+ "\n width - "+width);

    weInlineView.measure(
      View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));

    weInlineView.layout(0,0, width, height);
    String tagName = weInlineView.getTransitionName();
    Log.d("Ak1", "Update style height-"+height+ "\n width - "+width);
    Log.d("Ak1", "Update tagName-"+tagName);
  }

  public void updateViewTag(String tagName) {
    Log.d("Ak1", "Update tagName in view-"+tagName);
    weInlineView.setTag(tagName);
    weInlineView.load(new WEPlaceholderCallback() {

      @Override
      public void onDataReceived(WECampaignData weCampaignData) {
        Log.d("WebEngage12", "OnDataReceived from personalization view manager - "+weCampaignData);
        WritableMap params = Arguments.createMap();

//        TODO - Yet to Add weCampaignData.content
//        params.putMap("content",weCampaignData.getContent());

        params.putString("targetViewId", weCampaignData.getTargetViewId());
        params.putString("campaignId",weCampaignData.getCampaignId());
        Utils.sendEvent(applicationContext,"onDataReceived", params );
      }

      @Override
      public void onPlaceholderException(String s, String s1, Exception e) {
        Log.d("WebEngage12", "onPlaceholderException from personalization view manager-> \ns- "+s+"\ns1- "+s1 + "\nerror-"+e);
        WritableMap params = Arguments.createMap();

        Utils.sendEvent(applicationContext,"onPlaceholderException", params );

      }

      @Override
      public void onRendered(WECampaignData weCampaignData) {
        Log.d("WebEngage12", "onRendered from personalization view manager");
        WritableMap params = Arguments.createMap();
        Utils.sendEvent(applicationContext,"onDataReceived", params );
      }
    });
  }



}
