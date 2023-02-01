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
import com.webengage.personalization.callbacks.WEPropertyRegistryCallback;
import com.webengage.personalization.data.WECampaignData;

import org.json.JSONObject;

import java.util.HashMap;

public class WEHInlineWidget extends FrameLayout implements WECampaignCallback, ScreenNavigatorCallback {
  private static WEHInlineWidget instance = null;
  String TAG = "WebEngage-personalization-Hybrid";
  WEInlineView weInlineView;
  private ReactApplicationContext applicationContext = null;
  int height = 0, width = 0;
  String tagName = "", screenName = "";

//  @Override
//  protected void onDetachedFromWindow() {
//    super.onDetachedFromWindow();
//    Logger.d(WEGConstants.TAG, "Detached is called");
//
////  TODO -  Detach ->variable to null
////    TODO - This is not being called might be overriden from WeInlineView
//  }

  public WEHInlineWidget(@NonNull ReactApplicationContext context, HashMap<String, Object> map, WEGPersonalizationViewManager ref) {
    super(context);
    this.applicationContext = context;
    Log.d("WebEngage", "INside WEHInlineWidget @@@");
    init(context);
  }
  public void init(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.view_inlinewidget, this, false);
    weInlineView =  view.findViewById(R.id.weinline_widget);
    addView(view);
    weInlineView.requestLayout(); // called when Somethings is changed in the UI view

  }

  @Override
  public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
    super.addOnLayoutChangeListener(listener);
    Logger.d(WEGConstants.TAG, "addOnLayoutChangeListener called from WEHInlineWidger ");
  }

  //  Enforce this to reflect new Changes to the UI
public void setupLayout(View view, WECampaignData weCampaignData) {
    Logger.d(WEGConstants.TAG, "Setup layout called");
  Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {

    @Override
    public void doFrame(long frameTimeNanos) {
      manuallyLayoutChildren(view, weCampaignData);
      ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
      viewTreeObserver.dispatchOnGlobalLayout();
     boolean isScreenVisible = Utils.isVisible(view);
     Logger.d(WEGConstants.TAG, tagName+" isisScreenVisible- "+isScreenVisible);
     if(!isScreenVisible) {
       viewTreeObserver.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
         @Override
         public void onScrollChanged() {
           boolean isUserScreenVisible = Utils.isVisible(view);
           if(isUserScreenVisible) {
             Logger.d(WEGConstants.TAG, "Viewed "+tagName);
             weCampaignData.trackImpression(null);
             view.getViewTreeObserver().removeOnScrollChangedListener(this);
           }
         }
       });
     } else {
       Logger.d(WEGConstants.TAG, "Viewed "+tagName);
       weCampaignData.trackImpression(null);
     }
    }
  });
}

  public void manuallyLayoutChildren(View view, WECampaignData weCampaignData) {
//    Accessing cardView margins - Left, Right, Top, Bottom
    CardView.LayoutParams lp = (FrameLayout.LayoutParams) view.findViewWithTag("INLINE_PERSONALIZATION_TAG").getLayoutParams();
    int lm = lp.leftMargin;
    int rm = lp.rightMargin;
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
    heightInPixel -= (bm+tm);
    widthInPixel -= (rm+lm);

//    Size of the view
    view.measure(
      View.MeasureSpec.makeMeasureSpec(widthInPixel  , View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(heightInPixel, View.MeasureSpec.EXACTLY));

//    Positioning of the view - including the margin
    view.layout(view.getLeft() + lm  , view.getTop() + tm , widthInPixel + rm, heightInPixel + bm);
    Logger.d(WEGConstants.TAG, "Manual Layout completed");

  }


  public void updateStyle(int heights, int widths) {
    height = heights;
    width = widths;
  }

  public void updateProperties(String screenName, String propertyId) {
   // screenName
    this.screenName = screenName;
    Callbacker.setScreenNavigatorCallback(this.screenName, this);
//    propertyId
    this.tagName = propertyId;
    weInlineView.setTag(tagName);
    loadView(tagName);
  }
  public  void setScreenName(String screenName) {
    this.screenName = screenName;
    Callbacker.setScreenNavigatorCallback(this.screenName, this);
  }


  public void updateViewTag(String tagName) {
    Logger.d(WEGConstants.TAG, " updateViewTag is called for "+tagName);
    this.tagName = tagName;
    weInlineView.setTag(tagName);
    loadView(tagName);
  }

  public void loadView(String tagName) {
    Logger.d(WEGConstants.TAG, " loadView called for - "+tagName);
    weInlineView.load(tagName,new WEPlaceholderCallback() {
      @Override
      public void onDataReceived(WECampaignData weCampaignData) {
        WritableMap params = Arguments.createMap();
//        TODO - Yet to Add weCampaignData.content should use parse to JSON
//        params.putMap("content",weCampaignData.getContent());
//        convertToJSon
        JSONObject jsonObject = new JSONObject();
        params.putString("targetViewId", weCampaignData.getTargetViewId());
        params.putString("campaignId",weCampaignData.getCampaignId());
        Utils.sendEvent(applicationContext,"onDataReceived", params );
      }

      @Override
      public void onPlaceholderException(String s, String s1, Exception e) {
        Logger.d(WEGConstants.TAG, "onPlaceholderException from personalization view manager-> \ns- "+s+"\ns1- "+s1 + "\nerror-"+e);
        WritableMap params = Arguments.createMap();
        Utils.sendEvent(applicationContext,"onPlaceholderException", params );

      }

      @Override
      public void onRendered(WECampaignData weCampaignData) {
        Logger.d(WEGConstants.TAG, "onRendered from personalization view manager id-> "+weCampaignData.getTargetViewId());
        // TODO- Uncommenting below view will fix height issue but navigating back will make the screen to blank
//        View view = weInlineView.findViewWithTag("INLINE_PERSONALIZATION_TAG");

        if(weInlineView != null) {
          setupLayout(weInlineView, weCampaignData);
        }
      }
    });
  }

  public void registerCallback(String tagName) {
//    WEPersonalization.Companion.get().registerWEPlaceholderCallback(tagName, this);
  }


//  TODO - Check with sarthak if it has to be visible at hybrid for the below methods
   @Override
   public boolean onCampaignClicked(@NonNull String s, @NonNull String s1, @NonNull WECampaignData weCampaignData) {
     Logger.d(WEGConstants.TAG, "onCampaignClicked shown ---- "+weCampaignData);
     weCampaignData.trackClick(null);
     return false;
   }

   @Override
   public void onCampaignException(@Nullable String s, @NonNull String s1, @NonNull Exception e) {
     Logger.d(WEGConstants.TAG, "onCampaignException shown ---- "+e);
   }

   @Nullable
   @Override
   public WECampaignData onCampaignPrepared(@NonNull WECampaignData weCampaignData) {
     Logger.d(WEGConstants.TAG, "onCampaignPrepared shown ---- "+weCampaignData);
     return null;
   }

   @Override
   public void onCampaignShown(@NonNull WECampaignData weCampaignData) {
     Logger.d(WEGConstants.TAG, "Campaign data shown ---- "+weCampaignData);
   }

  @Override
  public void screenNavigated(String screenName) {
    Logger.d(WEGConstants.TAG, "screenNavigated of WEHInline called for screen - "+screenName+" for tagName- "+this.tagName);
    // TODO - Adding loadView here will work but onRendered is called twice. Fix this and make it work only once
    // TODO - Current issue of 2 loadView is bcz one is for direct launch second is for the navigating back
    if(!this.tagName.equals("")) {
     loadView(this.tagName);
    }
  }


//  @Override
//  public void onDataReceived(@NonNull WECampaignData weCampaignData) {
//
//  }
//
//  @Override
//  public void onPlaceholderException(@Nullable String s, @NonNull String s1, @NonNull Exception e) {
//
//  }
//
//  @Override
//  public void onRendered(@NonNull WECampaignData weCampaignData) {
//    Logger.d(WEGConstants.TAG, "onRendered - from WEPlaceHolderCallback");
//
//  }
}
