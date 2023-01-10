package com.webengage.sdk.android.actions.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;

import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.utils.WebEngageConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PushNotificationData extends BaseNotificationDataHolder {

    private JSONObject bigNotificationData = null;
    private BigPictureStyle bigPictureStyle = null;
    private BigTextStyle bigTextStyle = null;
    private InboxStyle inboxStyle = null;
    private CarouselV1 carouselV1 = null;
    private RatingV1 ratingV1 = null;
    private String style = null;
    private Bitmap largeIcon = null;
    private int smallIcon = -1;
    private String appName = null;
    private boolean autoExpand = true;
    private List<CallToAction> callToActions;
    private int accentColor = -1;
    private JSONObject cta;
    private int currentIndex = 0;
    private boolean isSticky = false;
    private int backgroundColor = Color.parseColor("#00000000");

    public PushNotificationData(JSONObject json, Context context) throws JSONException {
        super(json, WebEngageConstant.SYSTEM_TRAY, context);
        this.bigNotificationData = json.isNull("expandableDetails") ? null : json.optJSONObject("expandableDetails");
        this.callToActions = readCallToActions(json, context);
        this.accentColor = WebEngage.get().getWebEngageConfig().getAccentColor();
        this.smallIcon = WebEngage.get().getWebEngageConfig().getPushSmallIcon();

        if (this.smallIcon == -1) {
            this.smallIcon = context.getApplicationContext().getApplicationInfo().icon;
        }

        int largeIconId = WebEngage.get().getWebEngageConfig().getPushLargeIcon();
        if (largeIconId == -1) {
            this.largeIcon = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), context.getApplicationContext().getApplicationInfo().icon);
        } else {
            this.largeIcon = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), largeIconId);
        }
        if (this.largeIcon == null) {
            Logger.d(WebEngageConstant.TAG, "large icon is not available in the resources. Creating Bitmap from app icon");
            this.largeIcon = (createBitmapFromDrawable(context.getPackageManager().getApplicationIcon(context.getApplicationInfo())));
        }

        if (appName == null) {
            try {
                appName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
            } catch (Exception e) {
                Logger.e(WebEngageConstant.TAG, e.toString() + " loading app name");
            }
        }

        this.isSticky = json.optBoolean(WebEngageConstant.IS_STICKY_KEY, false);

        if (!json.isNull("bckColor")) {
            try {
                if (!TextUtils.isEmpty(json.getString("bckColor"))) {
                    this.backgroundColor = Color.parseColor(json.getString("bckColor"));
                    Logger.d(WebEngageConstant.TAG, "BackGround color : " + this.backgroundColor);
                }
            } catch (Exception e) {
                Logger.e(WebEngageConstant.TAG, "Exception while parsing bckColor " + e.toString());
                this.backgroundColor = Color.parseColor("#00000000");
                Logger.d(WebEngageConstant.TAG, "BackGround color : " + this.backgroundColor);
            }
        }


        if (this.bigNotificationData != null) {
            if (!bigNotificationData.isNull("style")) {
                this.style = bigNotificationData.optString("style");
                if (("BIG_TEXT").equalsIgnoreCase(style)) {
                    bigTextStyle = new BigTextStyle(bigNotificationData);
                } else if (("BIG_PICTURE").equalsIgnoreCase(style)) {
                    bigPictureStyle = new BigPictureStyle(bigNotificationData);
                } else if (("INBOX").equalsIgnoreCase(style)) {
                    inboxStyle = new InboxStyle(bigNotificationData);
                } else if ("CAROUSEL_V1".equalsIgnoreCase(style)) {
                    carouselV1 = new CarouselV1(bigNotificationData);
                    this.callToActions.addAll(carouselV1.getCallToActions());
                } else if ("RATING_V1".equalsIgnoreCase(style)) {
                    this.ratingV1 = new RatingV1(bigNotificationData);
                    if (this.ratingV1.getSubmitCTA() != null) {
                        this.callToActions.add(this.ratingV1.getSubmitCTA());
                    }
                }
            }
        }
    }

    private List<CallToAction> readCallToActions(JSONObject jsonObject, Context context) {
        JSONObject bigNotificationData = jsonObject.isNull("expandableDetails") ? null : jsonObject.optJSONObject("expandableDetails");
        List<CallToAction> callToActions = new ArrayList<CallToAction>();
        cta = jsonObject.isNull("cta") ? null : jsonObject.optJSONObject("cta");
        if (cta != null) {
            if (!cta.isNull("id")) {
                callToActions.add(new CallToAction(cta.optString("id"), getContentText(), cta.optString("actionLink"), true, true));
            }
        } else {
            callToActions.add(new CallToAction(null, getContentText(), null, true, true));
        }
        if (bigNotificationData != null) {
            cta = bigNotificationData.isNull("cta1") ? null : bigNotificationData.optJSONObject("cta1");
            if (cta != null) {
                if (!cta.isNull("id") && !cta.isNull("rat")) {
                    callToActions.add(new CallToAction(cta.optString("id"), cta.optString("rat"), cta.optString("actionLink"), false, true));
                } else if (!cta.isNull("id") && !cta.isNull("actionText")) {
                    callToActions.add(new CallToAction(cta.optString("id"), cta.optString("actionText"), cta.optString("actionLink"), false, true));
                }
            }
            cta = bigNotificationData.isNull("cta2") ? null : bigNotificationData.optJSONObject("cta2");
            if (cta != null) {
                if (!cta.isNull("id") && !cta.isNull("rat")) {
                    callToActions.add(new CallToAction(cta.optString("id"), cta.optString("rat"), cta.optString("actionLink"), false, true));
                } else if (!cta.isNull("id") && !cta.isNull("actionText")) {
                    callToActions.add(new CallToAction(cta.optString("id"), cta.optString("actionText"), cta.optString("actionLink"), false, true));
                }
            }
            cta = bigNotificationData.isNull("cta3") ? null : bigNotificationData.optJSONObject("cta3");
            if (cta != null) {
                if (!cta.isNull("id") && !cta.isNull("rat")) {
                    callToActions.add(new CallToAction(cta.optString("id"), cta.optString("rat"), cta.optString("actionLink"), false, true));
                } else if (!cta.isNull("id") && !cta.isNull("actionText")) {
                    callToActions.add(new CallToAction(cta.optString("id"), cta.optString("actionText"), cta.optString("actionLink"), false, true));
                }
            }
        }
        return callToActions;

    }

    public List<CallToAction> getCallToActions() {
        return this.callToActions;
    }

    public CallToAction getPrimeCallToAction() {
        if (this.callToActions != null && this.callToActions.size() > 0) {
            for (CallToAction callToAction : callToActions) {
                if (callToAction != null && callToAction.isPrimeAction()) {
                    return callToAction;
                }
            }
        }
        return null;
    }

    public CallToAction getCallToActionById(String id) {
        if (this.callToActions != null && this.callToActions.size() > 0 && id != null && !id.isEmpty()) {
            for (CallToAction callToAction : callToActions) {
                if (callToAction != null && id.equals(callToAction.getId())) {
                    return callToAction;
                }
            }
        }
        return null;
    }

    public List<CallToAction> getActions() {
        if (this.callToActions != null && this.callToActions.size() > 0) {
            List<CallToAction> list = null;
            for (CallToAction cta : this.callToActions) {
                if (cta != null && !cta.isPrimeAction() && cta.isNative()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(cta);
                }
            }
            return list;
        }
        return null;
    }

    public boolean getAutoExpand() {
        return this.autoExpand;
    }

    public void setAutoExpand(boolean autoExpand) {
        this.autoExpand = autoExpand;
    }

    public boolean isBigNotification() {
        return this.bigNotificationData != null;
    }

    public Bitmap getLargeIcon() {
        return this.largeIcon;
    }

    public int getAccentColor() {
        return this.accentColor;
    }

    public void setAccentColor(int accentColor) {
        this.accentColor = accentColor;
    }

    public void setSmallIcon(int resourceId) {
        this.smallIcon = resourceId;
    }

    public int getSmallIcon() {
        return this.smallIcon;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setLargerIcon(Bitmap largeIcon) {
        this.largeIcon = largeIcon;
    }

    public WebEngageConstant.STYLE getStyle() {
        try {
            return WebEngageConstant.STYLE.valueOf(this.style);
        } catch (Exception e) {
            return null;
        }
    }

    public BigTextStyle getBigTextStyleData() {
        return this.bigTextStyle;
    }

    public BigPictureStyle getBigPictureStyleData() {
        return this.bigPictureStyle;
    }

    public InboxStyle getInboxStyleData() {
        return this.inboxStyle;
    }

    public CarouselV1 getCarouselV1Data() {
        return this.carouselV1;
    }

    public RatingV1 getRatingV1() {
        return this.ratingV1;
    }

    public class BaseStyleData {
        private String bigContentTitle = null;
        private String message = null;
        private String summary = null;

        public BaseStyleData(JSONObject json) {
            this.bigContentTitle = json.isNull("rt") ? getTitle() : json.optString("rt");
            this.message = json.isNull("rm") ? getContentText() : json.optString("rm");
            this.summary = json.isNull("rst") ? getContentSummary() : json.optString("rst");
        }

        public void setBigContentTitle(String bigContentTitle) {
            this.bigContentTitle = bigContentTitle;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getBigContentTitle() {
            return this.bigContentTitle;
        }

        public String getMessage() {
            return this.message;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
    }

    public class BigTextStyle extends BaseStyleData {
        private String bigText = null;

        public BigTextStyle(JSONObject json) {
            super(json);
            this.bigText = json.isNull("rm") ? getContentText() : json.optString("rm");

        }

        public void setBigText(String bigText) {
            this.bigText = bigText;
        }

        public String getBigText() {
            return this.bigText;
        }

    }

    public class BigPictureStyle extends BaseStyleData {
        private String bigPictureUrl = null;

        public BigPictureStyle(JSONObject json) {
            super(json);
            this.bigPictureUrl = json.isNull("image") ? null : json.optString("image");
        }

        public void setBigPictureUrl(String url) {
            this.bigPictureUrl = url;
        }

        public String getBigPictureUrl() {
            return this.bigPictureUrl;
        }

    }

    public class InboxStyle extends BaseStyleData {
        private List<String> lines = null;

        public InboxStyle(JSONObject json) {
            super(json);
            JSONArray inboxLines = json.isNull("lines") ? null : json.optJSONArray("lines");
            if (inboxLines != null) {
                lines = new LinkedList<String>();
                for (int i = 0; i < inboxLines.length(); i++) {
                    lines.add(inboxLines.optString(i));
                }
            }
        }

        public void setLines(List<String> lines) {
            this.lines = lines;
        }

        public List<String> getInboxLines() {
            return this.lines;
        }


    }

    public class CarouselV1 extends BaseStyleData {

        private List<CarouselV1CallToAction> callToActions = null;
        private final String MODE;
        private int size = 0;
        private int autoScrollTime = -1;

        public CarouselV1(JSONObject jsonObject) {
            super(jsonObject);
            callToActions = new ArrayList<CarouselV1CallToAction>();
            JSONArray ctas = jsonObject.optJSONArray("items");
            if (ctas != null) {
                this.size = ctas.length();
                for (int i = 0; i < ctas.length(); i++) {
                    JSONObject cta = ctas.optJSONObject(i);
                    if (cta != null) {
                        if (!cta.isNull("id") && !cta.isNull("image")) {
                            callToActions.add(new CarouselV1CallToAction(cta.optString("id"), cta.optString("actionText"), cta.optString("actionLink"), cta.optString("image")));
                        }
                    }
                }
            }
            MODE = jsonObject.optString("mode", WebEngageConstant.LANDSCAPE);

            autoScrollTime = jsonObject.optInt(WebEngageConstant.AUTOSCROLL_TIMER_KEY, -1);

        }

        public List<CarouselV1CallToAction> getCallToActions() {
            return callToActions;
        }

        public void setCallToActions(List<CarouselV1CallToAction> callToActions) {
            this.callToActions = callToActions;
        }

        public String getMODE() {
            return this.MODE;
        }

        protected int getSize() {
            return this.size;
        }

        public int getAutoScrollTime() {
            return autoScrollTime;
        }

        public void setAutoScrollTime(int autoScrollTime) {
            this.autoScrollTime = autoScrollTime;
        }

    }

    public class RatingV1 extends BaseStyleData {

        private int contentBackgroundColor = Color.parseColor("#00000000");
        private String imageUrl = null;
        private String contentTitle = null;
        private String contentMessage = null;
        private String iconUrl = null;
        private int rateScale = 5;
        private int contentTextColor = Color.parseColor("#000000");
        private CallToAction submitCTA = null;
        private int rateValue = -1;

        public RatingV1(JSONObject jsonObject) {
            super(jsonObject);
            if (jsonObject != null) {
                imageUrl = jsonObject.isNull("image") ? null : jsonObject.optString("image");
                iconUrl = jsonObject.isNull("icon") ? null : jsonObject.optString("icon");
                rateScale = jsonObject.optInt("rateScale", 5);

                JSONObject content = jsonObject.optJSONObject("content");
                if (content != null) {
                    contentTitle = content.isNull("title") ? null : content.optString("title");
                    contentMessage = content.isNull("message") ? null : content.optString("message");
                    if (!content.isNull("textColor")) {
                        contentTextColor = Color.parseColor(content.optString("textColor"));
                    }

                    if (!content.isNull("bckColor")) {
                        try {
                            contentBackgroundColor = Color.parseColor(content.optString("bckColor"));
                        } catch (Exception e) {
                            Logger.e(WebEngageConstant.TAG, "Error parsing bckColor. Not setting background color");
                        }
                    }
                }

                JSONObject submit = jsonObject.optJSONObject("submitCTA");
                if (submit != null) {
                    submitCTA = new CallToAction(submit.optString("id"), submit.isNull("actionText") ? "Submit" : submit.optString("actionText"), submit.optString("actionLink"), false, false);
                } else {
                    submitCTA = new CallToAction(null, "Submit", null, false, false);
                }

            }
        }

        public void setRateValue(int rateValue) {
            this.rateValue = rateValue;
        }

        public int getRateValue() {
            return this.rateValue;
        }

        public String getImageUrl() {
            return this.imageUrl;
        }

        public String getContentTitle() {
            return this.contentTitle;
        }

        public String getContentMessage() {
            return this.contentMessage;
        }

        public String getIconUrl() {
            return this.iconUrl;
        }

        public int getRateScale() {
            return this.rateScale;
        }

        public int getContentTextColor() {
            return this.contentTextColor;
        }

        public int getContentBackgroundColor() {
            return this.contentBackgroundColor;
        }

        public CallToAction getSubmitCTA() {
            return this.submitCTA;
        }


    }

    public boolean isCustomRender() {
        Bundle customData = getCustomData();
        return customData != null && customData.containsKey(WebEngageConstant.WE_CUSTOM_RENDER) && Boolean.parseBoolean(customData.getString(WebEngageConstant.WE_CUSTOM_RENDER));
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isSticky() {
        return isSticky;
    }

    public void setSticky(boolean sticky) {
        this.isSticky = sticky;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PushNotificationData)) {
            return false;
        }
        PushNotificationData pushNotificationData = (PushNotificationData) o;
        return this.getExperimentId().equals(pushNotificationData.getExperimentId());
    }

    @Override
    public int hashCode() {
        return this.getExperimentId().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        sb.append(this.getClass().getName() + " Object { " + NEW_LINE);
        sb.append(" Experiment Id : " + getExperimentId() + NEW_LINE);
        sb.append(" Variation Id : " + getVariationId() + NEW_LINE);
        sb.append(" Title : " + getTitle() + NEW_LINE);
        sb.append(" Message : " + getContentText() + NEW_LINE);
        sb.append("Summary : " + (getContentSummary() != null ? getContentSummary() : "null") + NEW_LINE);
        if (getPrimeCallToAction() != null) {
            sb.append(" Main CTA : " + getPrimeCallToAction() + " ID : " + getPrimeCallToAction().getId() + NEW_LINE);
        }
        sb.append(" Custom Data : " + (getCustomData() != null ? getCustomData().toString() : "null") + NEW_LINE);
        if (this.bigNotificationData != null) {
            sb.append(" Expandable Details : " + this.bigNotificationData.toString() + NEW_LINE);
        }
        sb.append(" Amplified: " + this.isAmplified() + NEW_LINE);
        sb.append("}");
        return sb.toString();

    }

    private Bitmap createBitmapFromDrawable(Drawable drawable) {
        if (drawable != null) {
            final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bmp);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bmp;
        }
        return null;
    }

}
