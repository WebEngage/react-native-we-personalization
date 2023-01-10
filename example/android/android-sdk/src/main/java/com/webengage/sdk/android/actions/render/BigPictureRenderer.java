package com.webengage.sdk.android.actions.render;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.webengage.sdk.android.utils.htmlspanner.WEHtmlParserInterface;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.R;
import com.webengage.sdk.android.callbacks.CustomPushRender;
import com.webengage.sdk.android.utils.ImageOptimizationUtil;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;
import com.webengage.sdk.android.utils.http.CachePolicy;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;

import java.util.HashMap;

public class BigPictureRenderer extends PushRenderer implements CustomPushRender {

    private boolean isAndroid12 = false;
    private boolean hasBackgroundColor = false;


    @Override
    public boolean onRender(Context context, PushNotificationData pushNotificationData) {
        if (pushNotificationData.getBackgroundColor() != Color.parseColor("#00000000"))
            hasBackgroundColor = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.S)
            isAndroid12 = true;

        return super.onRender(context, pushNotificationData);
    }

    @Override
    void downloadImages() {
    }

    @Override
    protected void loadImages() {
        if (pushNotificationData.getBigPictureStyleData() != null) {
            String URL = pushNotificationData.getBigPictureStyleData().getBigPictureUrl();
            if (URL != null) {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("accept", "image/webp");
                RequestObject requestObject = new RequestObject.Builder(URL, RequestMethod.GET, applicationContext)
                        .setCachePolicy(CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING)
                        .setTag(WebEngageConstant.LANDSCAPE)
                        .setHeaders(headers)
                        .build();
                Bitmap bitmap = loadImage(loadImageResponse(requestObject));
                if (bitmap != null) {
                    validImages.add(bitmap);
                }
            }
        }
    }

    private RemoteViews constructExpandedPushBase() {
        RemoteViews pushBase = getCommonExpandedPushBase();
        if (isAndroid12) {
            pushBase.setBoolean(R.id.custom_title, "setSingleLine", false);
            pushBase.setBoolean(R.id.custom_title_native, "setSingleLine", false);
            pushBase.setInt(R.id.custom_title, "setMaxLines", 2);
            pushBase.setInt(R.id.custom_title_native, "setMaxLines", 2);
            pushBase.setInt(R.id.custom_message, "setMaxLines", 2);
            pushBase.setInt(R.id.custom_message_native, "setMaxLines", 2);
        }

        pushBase.setViewVisibility(R.id.custom_base_container, View.VISIBLE);
        PushNotificationData.BigPictureStyle bigPictureStyle = pushNotificationData.getBigPictureStyleData();
        if (bigPictureStyle != null) {
            pushBase.setTextViewText(R.id.custom_title, new WEHtmlParserInterface().fromHtml(bigPictureStyle.getBigContentTitle()));
            pushBase.setTextViewText(R.id.custom_message, new WEHtmlParserInterface().fromHtml(bigPictureStyle.getMessage()));
            pushBase.setTextViewText(R.id.custom_title_native, new WEHtmlParserInterface().fromHtml(bigPictureStyle.getBigContentTitle()));
            pushBase.setTextViewText(R.id.custom_message_native, new WEHtmlParserInterface().fromHtml(bigPictureStyle.getMessage()));

            if (!TextUtils.isEmpty(bigPictureStyle.getSummary())) {
                pushBase.setTextViewText(R.id.custom_summary, new WEHtmlParserInterface().fromHtml(bigPictureStyle.getSummary()));
                pushBase.setTextViewText(R.id.custom_summary_native, new WEHtmlParserInterface().fromHtml(bigPictureStyle.getSummary()));
            } else {
                pushBase.setViewVisibility(R.id.custom_summary, View.GONE);
                pushBase.setViewVisibility(R.id.custom_summary_native, View.GONE);
            }
            pushBase.setImageViewResource(R.id.small_icon, pushNotificationData.getSmallIcon());
        }
        if (!areButtonsPresent()) {
            pushBase.setViewVisibility(R.id.push_base_margin_view, View.VISIBLE);
        }

        return pushBase;
    }

    @Override
    protected void buildExpandedPush() {
        if (pushNotificationData.isBigNotification() && pushNotificationData.getStyle() != null && Build.VERSION.SDK_INT >= 16) {
            if (pushNotificationData.getBigPictureStyleData() != null) {
                PushNotificationData.BigPictureStyle bigPictureData = pushNotificationData.getBigPictureStyleData();
                if (!buildCustomPush && (pushNotificationData.getBackgroundColor() == Color.parseColor("#00000000"))) {
                    Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle();
                    bigPictureStyle.setBigContentTitle(new WEHtmlParserInterface().fromHtml(bigPictureData.getBigContentTitle()));
                    bigPictureStyle.setSummaryText(new WEHtmlParserInterface().fromHtml(bigPictureData.getMessage()));
                    try {
                        if (validImages.size() > 0) {
                            bigPictureStyle.bigPicture(validImages.get(0));
                            mBuilder.setStyle(bigPictureStyle);
                        }
                    } catch (Exception e) {

                    }
                } else {
                    customBigView = constructExpandedPushBase();
                    RemoteViews bigPictureView = new RemoteViews(this.applicationContext.getPackageName(), R.layout.big_picture);
                    if (validImages.size() > 0) {
                        int offset = ImageOptimizationUtil.getImageByteCount(pushNotificationData.getLargeIcon()) + 20000;
                        int maxPossibleSizeOfBitmap = (5000000 - offset);
                        long totalSizeOfImages = 0;
                        for (int i = 0; i < validImages.size(); i++) {
                            totalSizeOfImages += ImageOptimizationUtil.getImageByteCount(validImages.get(i));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && totalSizeOfImages > maxPossibleSizeOfBitmap) {
                            Logger.d(WebEngageConstant.TAG, "Big picture: Is Optimization required? true because totalSizeOfImages: " + totalSizeOfImages + " is greater than maxPossibleSizeOfBitmap: " + maxPossibleSizeOfBitmap);
                            validImages = ImageOptimizationUtil.getOptimizedRenderableImageLists(validImages,
                                    maxPossibleSizeOfBitmap, validImages.size());
                        }
                        bigPictureView.setImageViewBitmap(R.id.big_picture_image, validImages.get(0));
                    }
                    customBigView.removeAllViews(R.id.custom_base_container);
                    customBigView.addView(R.id.custom_base_container, bigPictureView);

                    if (areButtonsPresent()) {
                        customBigView.setInt(R.id.big_picture_image, "setMaxHeight",
                                WebEngageUtils.dpToPixels(161, this.applicationContext));
                    }
                    if (!hasBackgroundColor) {
                        showTextViews();
                    } else {
                        showNativeTextViews();
                    }

                }
            }
        }
    }


    private void showTextViews() {
        customBigView.setViewVisibility(R.id.app_name_native, View.GONE);
        customBigView.setViewVisibility(R.id.custom_summary_native, View.GONE);
        customBigView.setViewVisibility(R.id.custom_notification_time_native, View.GONE);
        customBigView.setViewVisibility(R.id.custom_title_native, View.GONE);
        customBigView.setViewVisibility(R.id.custom_message_native, View.GONE);
    }

    private void showNativeTextViews() {
        customBigView.setViewVisibility(R.id.app_name, View.GONE);
        customBigView.setViewVisibility(R.id.custom_summary, View.GONE);
        customBigView.setViewVisibility(R.id.custom_notification_time, View.GONE);
        customBigView.setViewVisibility(R.id.custom_title, View.GONE);
        customBigView.setViewVisibility(R.id.custom_message, View.GONE);
    }
}
