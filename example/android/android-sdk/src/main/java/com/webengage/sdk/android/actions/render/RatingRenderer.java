package com.webengage.sdk.android.actions.render;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.webengage.sdk.android.utils.htmlspanner.WEHtmlParserInterface;
import com.webengage.sdk.android.PendingIntentFactory;
import com.webengage.sdk.android.R;
import com.webengage.sdk.android.actions.exception.ImageLoadException;
import com.webengage.sdk.android.callbacks.CustomPushRender;
import com.webengage.sdk.android.callbacks.CustomPushRerender;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.http.CachePolicy;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import java.util.HashMap;

class RatingRenderer extends PushRenderer implements CustomPushRender, CustomPushRerender {
    private Long when;
    private Integer prevCurrentIndex = 0;

    private boolean hasBackgroundColor = false;
    private boolean isAndroid12 = false;
    @Override
    public boolean onRender(Context context, PushNotificationData pushNotificationData) {

        if (pushNotificationData.getBackgroundColor() != Color.parseColor("#00000000"))
            this.hasBackgroundColor = true;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.S)
            this.isAndroid12 = true;

        return super.onRender(context, pushNotificationData);

    }

    @Override
    public boolean onRerender(Context context, PushNotificationData pushNotificationData, Bundle extras) {
        if (extras != null) {
            when = extras.getLong(WebEngageConstant.WHEN);
            prevCurrentIndex = extras.getInt(WebEngageConstant.CURRENT);
        }

        if (pushNotificationData.getBackgroundColor() != Color.parseColor("#00000000"))
            this.hasBackgroundColor = true;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.S)
            this.isAndroid12 = true;

        return super.onRerender(context, pushNotificationData, extras);
    }

    @Override
    void downloadImages() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("accept", "image/webp");
        if (pushNotificationData.getRatingV1() != null) {
            if (pushNotificationData.getRatingV1().getImageUrl() != null) {
                RequestObject requestObject = new RequestObject.Builder(pushNotificationData.getRatingV1().getImageUrl(), RequestMethod.GET, this.applicationContext)
                        .setCachePolicy(CachePolicy.GET_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE)
                        .setTag(WebEngageConstant.LANDSCAPE)
                        .setHeaders(headers)
                        .setFlags(RequestObject.FLAG_PERSIST_AFTER_CONFIG_REFRESH)
                        .build();
                Response response = loadImageResponse(requestObject);
                if (!response.isReadable()) {
                    dispatchException(new ImageLoadException("Exception: " + response.getException() + "\nURL: " + response.getURL() + "\nResponseCode: " + response.getResponseCode() + "\nIsInputStreamNull: " + (response.getInputStream() == null)));
                    response.closeErrorStream();
                } else {
                    response.closeInputStream();
                }
            }

            if (pushNotificationData.getRatingV1().getIconUrl() != null) {
                RequestObject requestObject = new RequestObject.Builder(pushNotificationData.getRatingV1().getIconUrl(), RequestMethod.GET, this.applicationContext)
                        .setCachePolicy(CachePolicy.GET_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE)
                        .setTag(WebEngageConstant.PORTRAIT)
                        .setHeaders(headers)
                        .setFlags(RequestObject.FLAG_PERSIST_AFTER_CONFIG_REFRESH)
                        .build();
                Response response = loadImageResponse(requestObject);
                if (!response.isReadable()) {
                    dispatchException(new ImageLoadException("Exception: " + response.getException() + "\nURL: " + response.getURL() + "\nResponseCode: " + response.getResponseCode() + "\nIsInputStreamNull: " + (response.getInputStream() == null)));
                    response.closeErrorStream();
                } else {
                    response.closeInputStream();
                }
            }
        }
    }

    @Override
    void loadImages() {
        if (pushNotificationData.getRatingV1().getImageUrl() != null) {
            RequestObject requestObject = new RequestObject.Builder(pushNotificationData.getRatingV1().getImageUrl(), RequestMethod.GET, this.applicationContext)
                    .setCachePolicy(CachePolicy.GET_DATA_FROM_CACHE_ONLY)
                    .build();
            Bitmap bitmap = loadImage(loadImageResponse(requestObject));
            validImages.add(bitmap);
        } else {
            validImages.add(null);
        }

        if (pushNotificationData.getRatingV1().getIconUrl() != null) {
            RequestObject requestObject = new RequestObject.Builder(pushNotificationData.getRatingV1().getIconUrl(), RequestMethod.GET, this.applicationContext)
                    .setCachePolicy(CachePolicy.GET_DATA_FROM_CACHE_ONLY)
                    .build();
            Bitmap bitmap = loadImage(loadImageResponse(requestObject));
            validImages.add(bitmap);
        } else {
            validImages.add(null);
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
        pushBase.setOnClickPendingIntent(R.id.custom_base_container, null);
        PushNotificationData.RatingV1 ratingData = pushNotificationData.getRatingV1();
        pushBase.setOnClickPendingIntent(R.id.custom_head_container, null);
        if (ratingData != null) {
            pushBase.setTextViewText(R.id.custom_title, new WEHtmlParserInterface().fromHtml(ratingData.getBigContentTitle()));
            pushBase.setTextViewText(R.id.custom_message, new WEHtmlParserInterface().fromHtml(ratingData.getMessage()));

            pushBase.setTextViewText(R.id.custom_title_native, new WEHtmlParserInterface().fromHtml(ratingData.getBigContentTitle()));
            pushBase.setTextViewText(R.id.custom_message_native, new WEHtmlParserInterface().fromHtml(ratingData.getMessage()));

            if (TextUtils.isEmpty(ratingData.getSummary())) {
                pushBase.setViewVisibility(R.id.custom_summary_native, View.GONE);
                pushBase.setViewVisibility(R.id.custom_summary, View.GONE);
            } else {
                pushBase.setTextViewText(R.id.custom_summary, new WEHtmlParserInterface().fromHtml(ratingData.getSummary()));
                pushBase.setTextViewText(R.id.custom_summary_native, new WEHtmlParserInterface().fromHtml(ratingData.getSummary()));
            }

            pushBase.setInt(R.id.custom_container, "setBackgroundColor", pushNotificationData.getBackgroundColor());
        }

        return pushBase;
    }

    @Override
    void buildExpandedPush() {
        if (Build.VERSION.SDK_INT >= 16) {
            PushNotificationData.RatingV1 ratingV1 = pushNotificationData.getRatingV1();
            customBigView = constructExpandedPushBase();
            RemoteViews npsView = new RemoteViews(this.applicationContext.getPackageName(), R.layout.rating_v1);
            npsView.setInt(R.id.rating_v1_star_body, "setBackgroundColor", pushNotificationData.getBackgroundColor());
            if (when == null) {
                when = System.currentTimeMillis();
            }
            mBuilder.setWhen(when);

            if ((validImages.size() > 0 && validImages.get(0) != null) || (pushNotificationData.getRatingV1().getContentMessage() != null || pushNotificationData.getRatingV1().getContentTitle() != null)) { // not checking null for title as it can be optional in future
                npsView.setViewVisibility(R.id.rating_v1_frame, View.VISIBLE);
            }
            if (validImages.size() > 0 && validImages.get(0) != null) {
                npsView.setViewVisibility(R.id.rating_v1_image, View.VISIBLE);
                npsView.setImageViewBitmap(R.id.rating_v1_image, validImages.get(0));
            } else {
                npsView.setInt(R.id.rating_v1_frame, "setBackgroundColor", ratingV1.getContentBackgroundColor());
            }

            if (validImages.size() > 1 && validImages.get(1) != null) {
                npsView.setViewVisibility(R.id.rating_v1_icon, View.VISIBLE);
                npsView.setImageViewBitmap(R.id.rating_v1_icon, validImages.get(1));
            }

            if (pushNotificationData.getRatingV1().getContentTitle() != null) {
                npsView.setViewVisibility(R.id.rating_v1_title, View.VISIBLE);
                npsView.setTextViewText(R.id.rating_v1_title, new WEHtmlParserInterface().fromHtml(ratingV1.getContentTitle()));
                npsView.setTextColor(R.id.rating_v1_title, pushNotificationData.getRatingV1().getContentTextColor());
            }

            if (pushNotificationData.getRatingV1().getContentMessage() != null) {
                npsView.setViewVisibility(R.id.rating_v1_message, View.VISIBLE);
                npsView.setTextViewText(R.id.rating_v1_message, new WEHtmlParserInterface().fromHtml(ratingV1.getContentMessage()));
                npsView.setTextColor(R.id.rating_v1_message, pushNotificationData.getRatingV1().getContentTextColor());
            }

            if (!TextUtils.isEmpty(ratingV1.getSummary())) {
                npsView.setTextViewText(R.id.custom_summary, new WEHtmlParserInterface().fromHtml(ratingV1.getSummary()));
                npsView.setTextViewText(R.id.custom_summary_native, new WEHtmlParserInterface().fromHtml(ratingV1.getSummary()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    mBuilder.setSubText(new WEHtmlParserInterface().fromHtml(ratingV1.getSummary()));
                }
            } else {
                npsView.setViewVisibility(R.id.custom_summary, View.GONE);
                npsView.setViewVisibility(R.id.custom_summary_native, View.GONE);
            }
            npsView.setTextViewText(R.id.rating_v1_submit, new WEHtmlParserInterface().fromHtml(pushNotificationData.getRatingV1().getSubmitCTA().getText()));
            npsView.setTextViewText(R.id.rating_v1_submit_native, new WEHtmlParserInterface().fromHtml(pushNotificationData.getRatingV1().getSubmitCTA().getText()));

            if (prevCurrentIndex <= 0) {
                npsView.setOnClickPendingIntent(R.id.rating_v1_submit, null);
                npsView.setOnClickPendingIntent(R.id.rating_v1_submit_native, null);

                //npsView.setTextColor(R.id.rating_v1_submit, Color.parseColor("#DDDDDD"));
            } else {
                PendingIntent submitPendingIntent = PendingIntentFactory.constructPushRatingSubmitPendingIntent(this.applicationContext, this.pushNotificationData, prevCurrentIndex);
                //PendingIntent submitPendingIntent = PendingIntentFactory.constructPushRatingSubmitPendingIntent(prevCurrentIndex, false, true, hashedNotificationID, ("rating_v1_submit" + pushNotificationData.getVariationId()).hashCode(), pushNotificationData.getRatingV1().getSubmitCTA().getFullActionUri(), pushNotificationData.getRatingV1().getSubmitCTA(), pushNotificationData, this.applicationContext);
                npsView.setOnClickPendingIntent(R.id.rating_v1_submit, submitPendingIntent);
                npsView.setOnClickPendingIntent(R.id.rating_v1_submit_native, submitPendingIntent);
                //npsView.setTextColor(R.id.rating_v1_submit, Color.parseColor("#000000"));
            }
            Bitmap gold = BitmapFactory.decodeResource(this.applicationContext.getResources(), R.drawable.star_selected);
            Bitmap normal = BitmapFactory.decodeResource(this.applicationContext.getResources(), R.drawable.star_unselected);

            for (int i = 1; i <= pushNotificationData.getRatingV1().getRateScale(); i++) {
                Bundle starClickExtraData = new Bundle();
                starClickExtraData.putInt(WebEngageConstant.CURRENT, i);
                starClickExtraData.putLong(WebEngageConstant.WHEN, when);
                starClickExtraData.putBoolean(WebEngageConstant.WE_RENDER, true);
                PendingIntent clickIntent = PendingIntentFactory.constructRerenderPendingIntent(this.applicationContext, this.pushNotificationData, "rating_v1_star" + i, starClickExtraData);
                //PendingIntent clickIntent = PendingIntentFactory.constructRatingStarClickPendingIntent(i, ("rating_v1_star" + i + pushNotificationData.getVariationId()).hashCode(), this.applicationContext, pushNotificationData.getVariationId(), pushNotificationData.getExperimentId(), when);
                switch (i) {
                    case 1:
                        npsView.setViewVisibility(R.id.rating_v1_star1, View.VISIBLE);
                        npsView.setOnClickPendingIntent(R.id.rating_v1_star1, clickIntent);
                        if (i <= prevCurrentIndex) {
                            npsView.setImageViewBitmap(R.id.rating_v1_star1, gold);
                        } else {
                            npsView.setImageViewBitmap(R.id.rating_v1_star1, normal);
                        }
                        break;

                    case 2:
                        npsView.setViewVisibility(R.id.rating_v1_star2, View.VISIBLE);
                        npsView.setOnClickPendingIntent(R.id.rating_v1_star2, clickIntent);
                        if (i <= prevCurrentIndex) {
                            npsView.setImageViewBitmap(R.id.rating_v1_star2, gold);
                        } else {
                            npsView.setImageViewBitmap(R.id.rating_v1_star2, normal);
                        }
                        break;

                    case 3:
                        npsView.setViewVisibility(R.id.rating_v1_star3, View.VISIBLE);
                        npsView.setOnClickPendingIntent(R.id.rating_v1_star3, clickIntent);
                        if (i <= prevCurrentIndex) {
                            npsView.setImageViewBitmap(R.id.rating_v1_star3, gold);
                        } else {
                            npsView.setImageViewBitmap(R.id.rating_v1_star3, normal);
                        }
                        break;

                    case 4:
                        npsView.setViewVisibility(R.id.rating_v1_star4, View.VISIBLE);
                        npsView.setOnClickPendingIntent(R.id.rating_v1_star4, clickIntent);
                        if (i <= prevCurrentIndex) {
                            npsView.setImageViewBitmap(R.id.rating_v1_star4, gold);
                        } else {
                            npsView.setImageViewBitmap(R.id.rating_v1_star4, normal);
                        }
                        break;

                    case 5:
                        npsView.setViewVisibility(R.id.rating_v1_star5, View.VISIBLE);
                        npsView.setOnClickPendingIntent(R.id.rating_v1_star5, clickIntent);
                        if (i <= prevCurrentIndex) {
                            npsView.setImageViewBitmap(R.id.rating_v1_star5, gold);
                        } else {
                            npsView.setImageViewBitmap(R.id.rating_v1_star5, normal);
                        }
                        break;

                    case 6:
                        npsView.setViewVisibility(R.id.rating_v1_star6, View.VISIBLE);
                        npsView.setOnClickPendingIntent(R.id.rating_v1_star6, clickIntent);
                        if (i <= prevCurrentIndex) {
                            npsView.setImageViewBitmap(R.id.rating_v1_star6, gold);
                        } else {
                            npsView.setImageViewBitmap(R.id.rating_v1_star6, normal);
                        }
                        break;

                    case 7:
                        npsView.setViewVisibility(R.id.rating_v1_star7, View.VISIBLE);
                        npsView.setOnClickPendingIntent(R.id.rating_v1_star7, clickIntent);
                        if (i <= prevCurrentIndex) {
                            npsView.setImageViewBitmap(R.id.rating_v1_star7, gold);
                        } else {
                            npsView.setImageViewBitmap(R.id.rating_v1_star7, normal);
                        }
                        break;

                    case 8:
                        npsView.setViewVisibility(R.id.rating_v1_star8, View.VISIBLE);
                        npsView.setOnClickPendingIntent(R.id.rating_v1_star8, clickIntent);
                        if (i <= prevCurrentIndex) {
                            npsView.setImageViewBitmap(R.id.rating_v1_star8, gold);
                        } else {
                            npsView.setImageViewBitmap(R.id.rating_v1_star8, normal);
                        }
                        break;

                    case 9:
                        npsView.setViewVisibility(R.id.rating_v1_star9, View.VISIBLE);
                        npsView.setOnClickPendingIntent(R.id.rating_v1_star9, clickIntent);
                        if (i <= prevCurrentIndex) {
                            npsView.setImageViewBitmap(R.id.rating_v1_star9, gold);
                        } else {
                            npsView.setImageViewBitmap(R.id.rating_v1_star9, normal);
                        }
                        break;

                    case 10:
                        npsView.setViewVisibility(R.id.rating_v1_star10, View.VISIBLE);
                        npsView.setOnClickPendingIntent(R.id.rating_v1_star10, clickIntent);
                        if (i <= prevCurrentIndex) {
                            npsView.setImageViewBitmap(R.id.rating_v1_star10, gold);
                        } else {
                            npsView.setImageViewBitmap(R.id.rating_v1_star10, normal);
                        }
                        break;
                }
            }


            customBigView.removeAllViews(R.id.custom_base_container);
            customBigView.addView(R.id.custom_base_container, npsView);
            customBigView.setInt(R.id.custom_base_container, "setBackgroundColor", pushNotificationData.getBackgroundColor());
            if (Color.parseColor("#00000000") == pushNotificationData.getBackgroundColor()) {
                showTextViews();
            } else {
                showNativeTextViews();
            }
        }

    }

    private void showTextViews() {
        customBigView.setViewVisibility(R.id.app_name_native, View.GONE);
        customBigView.setViewVisibility(R.id.custom_summary_native, View.GONE);
        customBigView.setViewVisibility(R.id.custom_notification_time_native, View.GONE);
        customBigView.setViewVisibility(R.id.custom_title_native, View.GONE);
        customBigView.setViewVisibility(R.id.custom_message_native, View.GONE);
        customBigView.setViewVisibility(R.id.rating_v1_submit_native, View.GONE);
    }

    private void showNativeTextViews() {
        customBigView.setViewVisibility(R.id.app_name, View.GONE);
        customBigView.setViewVisibility(R.id.custom_summary, View.GONE);
        customBigView.setViewVisibility(R.id.custom_notification_time, View.GONE);
        customBigView.setViewVisibility(R.id.custom_title, View.GONE);
        customBigView.setViewVisibility(R.id.custom_message, View.GONE);
        customBigView.setViewVisibility(R.id.rating_v1_submit, View.GONE);
    }
}
