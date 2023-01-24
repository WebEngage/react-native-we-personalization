package com.webengagepersonalization;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.webengage.personalization.WEInlineView;
import com.webengage.personalization.WEPersonalization;
import com.webengage.personalization.callbacks.WECampaignCallback;
import com.webengage.personalization.callbacks.WEPlaceholderCallback;
import com.webengage.personalization.data.WECampaignData;
import com.webengage.personalization.utils.WEUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.HashMap;
import java.util.Map;

public class InlineWidget extends FrameLayout implements WECampaignCallback {
  private static InlineWidget instance = null;
  String TAG = "WebEngage-personalization-Hybrid";
  WEInlineView weInlineView;
  private ReactApplicationContext applicationContext = null;
  int height = 0, width = 0;
  String tagName = "";
  public InlineWidget(@NonNull ReactApplicationContext context, HashMap<String, Object> map, WebengagePersonalizationViewManager ref) {
    super(context);
    this.applicationContext = context;
     WEPersonalization.Companion.get().registerWECampaignCallback(this);
    init(context);
  }
  public void init(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.view_inlinewidget, this, false);
    weInlineView =  view.findViewById(R.id.weinline_widget);
    addView(view);
    weInlineView.requestLayout(); // called when smtg is changed in the UI view
  }

  public static boolean isVisible(final View view) {

    if (view == null) {
      return false;
    }
    if (!view.isShown()) {
      return false;
    }

    final Rect actualPosition = new Rect();
    view.getGlobalVisibleRect(actualPosition);
    final Rect screen = new Rect(0, 0, Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
    return actualPosition.intersect(screen);
  }

//  Fetched from react native docs
public void setupLayout(View view, WECampaignData weCampaignData) {
      Log.d(TAG, "setupLayout called - ");
//      String tagName = weCampaignData.getTargetViewId();

  Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
    @Override
    public void doFrame(long frameTimeNanos) {
      manuallyLayoutChildren(view, weCampaignData);
      ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
      viewTreeObserver.dispatchOnGlobalLayout();
//
      boolean isScreenVisible = isVisible(view);
      Log.d(TAG, tagName+" isisScreenVisible- "+isScreenVisible);
      if(!isScreenVisible) {
        viewTreeObserver.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
          @Override
          public void onScrollChanged() {
            boolean isUserScreenVisible = isVisible(view);
            if(isUserScreenVisible) {
              Log.d(TAG, "Viewed "+tagName);
              weCampaignData.trackImpression(null);
              view.getViewTreeObserver().removeOnScrollChangedListener(this);
            }
            Log.d(TAG, tagName + " isVisible -> " + isUserScreenVisible);
          }
        });
      } else {
        Log.d(TAG, "Viewed "+tagName);
        weCampaignData.trackImpression(null);
      }
    }
  });
}

  public static float dpFromPx(final Context context, final float px) {
    return px / context.getResources().getDisplayMetrics().density;
  }

  public void manuallyLayoutChildren(View view, WECampaignData weCampaignData) {
    String tagName = weCampaignData.getTargetViewId();
    CardView.LayoutParams lp = (FrameLayout.LayoutParams) view.findViewWithTag("INLINE_PERSONALIZATION_TAG").getLayoutParams();
    int lm = lp.leftMargin;
    int rm = lp.rightMargin;
    int tm = lp.topMargin;
    int bm = lp.bottomMargin;

    // propWidth and propHeight coming from react-native props
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

    Log.d("WebEngage", " margin layouts top- "+tm+" \n bottom- "+bm+" \n left- "+lm+"\n right- "+rm);
    Log.d(TAG, "DipToPx original before calculation height-"+height+ "\n updaeted pixel height - "+heightInPixel);

    heightInPixel -= (bm+tm);
    widthInPixel -= (rm+lm);

    Log.d(TAG, "DipToPx original after calculation height-"+height+ "\n updaeted pixel height - "+heightInPixel);

    view.measure(
      View.MeasureSpec.makeMeasureSpec(widthInPixel  , View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(heightInPixel, View.MeasureSpec.EXACTLY));

    view.layout(view.getLeft() +lm  , view.getTop() + tm , widthInPixel + rm, heightInPixel + bm);



    Log.d("WebEngage", "InlineWidget height -  "+this.getHeight()+" \n view -getHeight - "+ dpFromPx(applicationContext, view.getHeight()));
    Log.d("WebEngage", "InlineWidget height layout LEft "+this.getLeft()+" \n this.top- "+this.getTop()+" \n view.getTop- "+view.getTop());
    Log.d("WebEngage", "InlineWidget height layout LEft "+this.getLeft()+" \n this.getY- "+this.getY()+" \n view.getY- "+view.getY());

//    Log.d("WebEngage", "");

  }


  public void updateStyle(int heights, int widths) {
    height = heights;
    width = widths;
    Log.d(TAG, "Style updated height-"+height+ "\n width - "+width);
  }

  public void updateViewTag(String tagName) {
    this.tagName = tagName;
    weInlineView.setTag(tagName);
    weInlineView.load(tagName,new WEPlaceholderCallback() {

      @Override
      public void onDataReceived(WECampaignData weCampaignData) {
        Log.d(TAG, "OnDataReceived from personalization view manager - "+weCampaignData);
        WritableMap params = Arguments.createMap();

//        TODO - Yet to Add weCampaignData.content
//        params.putMap("content",weCampaignData.getContent());

        params.putString("targetViewId", weCampaignData.getTargetViewId());
        params.putString("campaignId",weCampaignData.getCampaignId());
        Utils.sendEvent(applicationContext,"onDataReceived", params );
      }

      @Override
      public void onPlaceholderException(String s, String s1, Exception e) {
        Log.d(TAG, "onPlaceholderException from personalization view manager-> \ns- "+s+"\ns1- "+s1 + "\nerror-"+e);
        WritableMap params = Arguments.createMap();

        Utils.sendEvent(applicationContext,"onPlaceholderException", params );

      }

      @Override
      public void onRendered(WECampaignData weCampaignData) {
        Log.d(TAG, "onRendered from personalization view manager id-> "+weCampaignData.getTargetViewId());
//        WritableMap params = Arguments.createMap();
//        Utils.sendEvent(applicationContext,"onDataReceived", params );

        View view = weInlineView.findViewWithTag("INLINE_PERSONALIZATION_TAG");
        setupLayout(view, weCampaignData);
      }
    });
  }


   @Override
   public boolean onCampaignClicked(@NonNull String s, @NonNull String s1, @NonNull WECampaignData weCampaignData) {
     Log.d(TAG, "onCampaignClicked shown ---- "+weCampaignData);
     weCampaignData.trackClick(null);
     return false;
   }

   @Override
   public void onCampaignException(@Nullable String s, @NonNull String s1, @NonNull Exception e) {
     Log.d(TAG, "onCampaignException shown ---- "+e);

   }

   @Nullable
   @Override
   public WECampaignData onCampaignPrepared(@NonNull WECampaignData weCampaignData) {
     Log.d(TAG, "onCampaignPrepared shown ---- "+weCampaignData);
     return null;
   }

   @Override
   public void onCampaignShown(@NonNull WECampaignData weCampaignData) {
     Log.d(TAG, "Campaign data shown ---- "+weCampaignData);
   }
}
