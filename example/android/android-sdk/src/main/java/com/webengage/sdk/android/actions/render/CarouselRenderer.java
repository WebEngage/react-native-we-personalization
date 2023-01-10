package com.webengage.sdk.android.actions.render;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.RemoteViews;

import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.PendingIntentFactory;
import com.webengage.sdk.android.R;
import com.webengage.sdk.android.actions.exception.ImageLoadException;
import com.webengage.sdk.android.callbacks.CustomPushRender;
import com.webengage.sdk.android.callbacks.CustomPushRerender;
import com.webengage.sdk.android.utils.ImageOptimizationUtil;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.http.CachePolicy;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;
import com.webengage.sdk.android.utils.htmlspanner.WEHtmlParserInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class CarouselRenderer extends PushRenderer implements CustomPushRender, CustomPushRerender {
    private Long when = null;

    private int prevCurrentIndex = 0;
    private String navigation = WebEngageConstant.RIGHT;
    private int currentCenterIndex = 0;
    private int rightImageIndex = 0;
    private int leftImageIndex = 0;
    private int limit = 0;
    private boolean shouldAutoScroll = false;
    private boolean hasBackgroundColor = false;
    private boolean isAndroid12 = false;

    @Override
    public boolean onRender(Context context, PushNotificationData pushNotificationData) {
        if (pushNotificationData.getCarouselV1Data().getAutoScrollTime() != -1)
            shouldAutoScroll = true;

        if (pushNotificationData.getBackgroundColor() != Color.parseColor("#00000000"))
            hasBackgroundColor = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.S)
            isAndroid12 = true;

        return super.onRender(context, pushNotificationData);
    }

    @Override
    public boolean onRerender(Context context, PushNotificationData pushNotificationData, Bundle extras) {
        if (extras != null) {
            when = extras.getLong(WebEngageConstant.WHEN);
            prevCurrentIndex = extras.getInt(WebEngageConstant.CURRENT);
            navigation = extras.getString(WebEngageConstant.NAVIGATION);
            shouldAutoScroll = extras.getBoolean(WebEngageConstant.SHOULD_AUTOSCROLL);
        }

        if (pushNotificationData.getBackgroundColor() != Color.parseColor("#00000000"))
            hasBackgroundColor = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.S)
            isAndroid12 = true;

        return super.onRerender(context, pushNotificationData, extras);
    }

    @Override
    void downloadImages() {
        if (pushNotificationData.getCarouselV1Data() != null) {
            String mode = pushNotificationData.getCarouselV1Data().getMODE();
            List<CarouselV1CallToAction> carouselV1CallToActions = pushNotificationData.getCarouselV1Data().getCallToActions();
            if (carouselV1CallToActions != null) {
                for (CarouselV1CallToAction carouselV1CallToAction : carouselV1CallToActions) {
                    String imageURL = carouselV1CallToAction.getImageURL();
                    if (imageURL != null && !imageURL.isEmpty()) {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("accept", "image/webp");
                        RequestObject requestObject = new RequestObject.Builder(imageURL, RequestMethod.GET, this.applicationContext)
                                .setCachePolicy(CachePolicy.GET_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE)
                                .setTag(mode)
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

            prevCurrentIndex = pushNotificationData.getCarouselV1Data().getSize() - 1;
        }
    }

    private Pair<Integer, Bitmap> tryAndLoadImage(int index, int direction, int size) {
        Pair<Integer, Bitmap> result = null;
        if (pushNotificationData.getCarouselV1Data() != null) {
            List<CarouselV1CallToAction> carouselV1CallToActions = pushNotificationData.getCarouselV1Data().getCallToActions();
            if (carouselV1CallToActions != null) {
                int i = index;
                do {
                    String imageURL = carouselV1CallToActions.get(i).getImageURL();
                    RequestObject requestObject = new RequestObject.Builder(imageURL, RequestMethod.GET, this.applicationContext)
                            .setCachePolicy(CachePolicy.GET_DATA_FROM_CACHE_ONLY)
                            .build();
                    Bitmap bitmap = loadImage(loadImageResponse(requestObject));
                    if (bitmap != null) {
                        result = Pair.create(i, bitmap);
                        break;
                    }
                    if (direction == 1) {
                        i = (i + 1) % size;
                    } else {
                        i = (i - 1 + size) % size;
                    }
                } while (i != index);
            }
        }
        return result;
    }

    @Override
    void loadImages() {
        String mode = pushNotificationData.getCarouselV1Data().getMODE();
        int size = pushNotificationData.getCarouselV1Data().getSize();
        List<CarouselV1CallToAction> carouselV1CallToActions = pushNotificationData.getCarouselV1Data().getCallToActions();
        if (shouldAutoScroll) {

            for (int i = 0; i < size; i++) {
                Logger.d(WebEngageConstant.TAG, "Adding " + size + " images to validImages");
                limit = size;
                validImages.add(tryAndLoadImage(i, -1, size).second);
            }
        } else if (WebEngageConstant.PORTRAIT.equals(mode)) {
            limit = 3;
            if (navigation.equals(WebEngageConstant.LEFT)) {
                Pair<Integer, Bitmap> rightImagePair = tryAndLoadImage(prevCurrentIndex, -1, size);
                if (rightImagePair != null) {
                    rightImageIndex = rightImagePair.first;
                }
                Pair<Integer, Bitmap> centerImagePair = tryAndLoadImage((rightImageIndex - 1 + size) % size, -1, size);
                if (centerImagePair != null) {
                    currentCenterIndex = centerImagePair.first;
                }
                Pair<Integer, Bitmap> leftImagePair = tryAndLoadImage((currentCenterIndex - 1 + size) % size, -1, size);
                if (leftImagePair != null) {
                    leftImageIndex = leftImagePair.first;
                    validImages.add(leftImagePair.second);
                }
                if (centerImagePair != null) {
                    validImages.add(centerImagePair.second);
                }
                if (rightImagePair != null) {
                    validImages.add(rightImagePair.second);
                }
            } else if (navigation.equals(WebEngageConstant.RIGHT)) {
                Pair<Integer, Bitmap> leftImagePair = tryAndLoadImage(prevCurrentIndex, 1, size);
                if (leftImagePair != null) {
                    leftImageIndex = leftImagePair.first;
                }
                Pair<Integer, Bitmap> centerImagePair = tryAndLoadImage((leftImageIndex + 1) % size, 1, size);
                if (centerImagePair != null) {
                    currentCenterIndex = centerImagePair.first;
                }
                Pair<Integer, Bitmap> rightImagePair = tryAndLoadImage((currentCenterIndex + 1) % size, 1, size);
                if (leftImagePair != null) {
                    validImages.add(leftImagePair.second);
                }
                if (centerImagePair != null) {
                    validImages.add(centerImagePair.second);
                }
                if (rightImagePair != null) {
                    validImages.add(rightImagePair.second);
                    rightImageIndex = rightImagePair.first;
                }
            }
        } else if (WebEngageConstant.LANDSCAPE.equals(mode)) {
            limit = 1;
            if (WebEngageConstant.LEFT.equals(navigation)) {
                Pair<Integer, Bitmap> centerImagePair = tryAndLoadImage((prevCurrentIndex - 1 + size) % size, -1, size);
                if (centerImagePair != null) {
                    currentCenterIndex = centerImagePair.first;
                    validImages.add(centerImagePair.second);
                }
            } else if (WebEngageConstant.RIGHT.equals(navigation)) {
                Pair<Integer, Bitmap> centerImagePair = tryAndLoadImage((prevCurrentIndex + 1) % size, 1, size);
                if (centerImagePair != null) {
                    currentCenterIndex = centerImagePair.first;
                    validImages.add(centerImagePair.second);
                }
            }
        }
    }

    private RemoteViews constructExpandedPushBase() {
        RemoteViews pushBase = getCommonExpandedPushBase();
        if (isAndroid12) {
            pushBase.setInt(R.id.custom_message, "setMaxLines", 2);
            pushBase.setInt(R.id.custom_message_native, "setMaxLines", 2);

            pushBase.setBoolean(R.id.custom_title, "setSingleLine", false);
            pushBase.setBoolean(R.id.custom_title_native, "setSingleLine", false);
            pushBase.setInt(R.id.custom_title, "setMaxLines", 2);
            pushBase.setInt(R.id.custom_title_native, "setMaxLines", 2);
        }
        pushBase.setViewVisibility(R.id.custom_base_container, View.VISIBLE);
        pushBase.setOnClickPendingIntent(R.id.custom_base_container, null);
        PushNotificationData.CarouselV1 carouselData = pushNotificationData.getCarouselV1Data();
        if (carouselData != null) {
            pushBase.setTextViewText(R.id.custom_title, new WEHtmlParserInterface().fromHtml(carouselData.getBigContentTitle()));
            pushBase.setTextViewText(R.id.custom_message, new WEHtmlParserInterface().fromHtml(carouselData.getMessage()));

            pushBase.setTextViewText(R.id.custom_title_native, new WEHtmlParserInterface().fromHtml(carouselData.getBigContentTitle()));
            pushBase.setTextViewText(R.id.custom_message_native, new WEHtmlParserInterface().fromHtml(carouselData.getMessage()));

            if (!TextUtils.isEmpty(carouselData.getSummary())) {
                pushBase.setTextViewText(R.id.custom_summary, new WEHtmlParserInterface().fromHtml(carouselData.getSummary()));
                pushBase.setTextViewText(R.id.custom_summary_native, new WEHtmlParserInterface().fromHtml(carouselData.getSummary()));
            } else {
                pushBase.setViewVisibility(R.id.custom_summary, View.GONE);
                pushBase.setViewVisibility(R.id.custom_summary_native, View.GONE);
            }
        }

        //For Android12 , template is provided for all notifications. So hide the View used for showing the app name in custom notifications
        if (isAndroid12) {
            pushBase.setViewVisibility(R.id.custom_small_head_container, View.GONE);
            int leftPadding = applicationContext.getResources().getDimensionPixelSize(R.dimen.we_push_content_margin_colorbg);

            if (!hasBackgroundColor)
                leftPadding = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                pushBase.setViewPadding(R.id.custom_head_container, leftPadding, 0, 0, 0);
            }
        }
        if (hasBackgroundColor) {
            pushBase.setInt(R.id.push_base_container, "setBackgroundColor", pushNotificationData.getBackgroundColor());
        }

        return pushBase;
    }

    @Override
    void buildExpandedPush() {
        if (Build.VERSION.SDK_INT >= 16 && validImages.size() == limit) {
            PushNotificationData.CarouselV1 carouselV1 = pushNotificationData.getCarouselV1Data();
            if (carouselV1 != null) {
                Bundle customData = pushNotificationData.getCustomData();

                if (when == null) {
                    when = System.currentTimeMillis();
                }
                mBuilder.setWhen(when);

                boolean shouldDismissOnClick = false;
                if (customData != null) {
                    String weDismiss = customData.getString(WebEngageConstant.WE_DISMISS_ON_CLICK);
                    shouldDismissOnClick = Boolean.parseBoolean(weDismiss);
                }

                List<CarouselV1CallToAction> callToActions = carouselV1.getCallToActions();
                customBigView = constructExpandedPushBase();

                RemoteViews carousel_v1 = new RemoteViews(this.applicationContext.getPackageName(), R.layout.carousel_v1);

                Bundle browseExtraData = new Bundle();
                browseExtraData.putLong(WebEngageConstant.WHEN, when);
                browseExtraData.putBoolean(WebEngageConstant.WE_RENDER, true);
                PendingIntent nextPendingIntent = PendingIntentFactory.constructCarouselBrowsePendingIntent(this.applicationContext, this.pushNotificationData, currentCenterIndex, WebEngageConstant.RIGHT, "carousel_v1_right", browseExtraData);
                PendingIntent prevPendingIntent = PendingIntentFactory.constructCarouselBrowsePendingIntent(this.applicationContext, this.pushNotificationData, currentCenterIndex, WebEngageConstant.LEFT, "carousel_v1_left", browseExtraData);

                carousel_v1.setOnClickPendingIntent(R.id.next, nextPendingIntent);
                carousel_v1.setOnClickPendingIntent(R.id.prev, prevPendingIntent);
                carousel_v1.setOnClickPendingIntent(R.id.carousel_portrait_2_container, nextPendingIntent);
                carousel_v1.setOnClickPendingIntent(R.id.carousel_portrait_0_container, prevPendingIntent);

                List<Bitmap> renderableImagesList = new ArrayList<>();
                renderableImagesList = validImages;

                int offset = ImageOptimizationUtil.getImageByteCount(pushNotificationData.getLargeIcon()) + 20000;
                int maxPossibleSizeOfBitmap = (5000000 - offset);
                Logger.d(WebEngageConstant.TAG, "maxPossible " + maxPossibleSizeOfBitmap);

                //For Android 11+ the bitmaps rendered in remoteViews need to be under 5mb. check if sum of all images to be rendered are below the maxSize limit. If not, decrease the size based on priority.
                int totalSizeOfImages = 0;
                for (int i = 0; i < limit; i++) {
                    totalSizeOfImages += ImageOptimizationUtil.getImageByteCount(validImages.get(i));
                }
                if (WebEngageConstant.PORTRAIT.equals(carouselV1.getMODE())) {
                    Logger.d(WebEngageConstant.TAG, "rendering manual carousel portrait mode ");

                    if (pushNotificationData.getCarouselV1Data().getAutoScrollTime() == -1 || !shouldAutoScroll) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && totalSizeOfImages > maxPossibleSizeOfBitmap)
                            renderableImagesList = ImageOptimizationUtil.getOptimizedPriorityRenderableImageLists(validImages, maxPossibleSizeOfBitmap, limit);
                        for (int i = 0; i < limit; i++) {
                            switch (i) {
                                case 0:
                                    carousel_v1.setImageViewBitmap(R.id.carousel_portrait_0_image, renderableImagesList.get(i));
                                    carousel_v1.setTextViewText(R.id.carousel_portrait_0_desc, new WEHtmlParserInterface().fromHtml(callToActions.get(leftImageIndex).getText()));
                                    break;

                                case 1:
                                    carousel_v1.setImageViewBitmap(R.id.carousel_portrait_1_image, renderableImagesList.get(i));
                                    carousel_v1.setTextViewText(R.id.carousel_portrait_1_desc, new WEHtmlParserInterface().fromHtml(callToActions.get(currentCenterIndex).getText()));
                                    PendingIntent pendingIntent = PendingIntentFactory.constructPushClickPendingIntent(this.applicationContext, this.pushNotificationData, callToActions.get(currentCenterIndex), shouldDismissOnClick);
                                    carousel_v1.setOnClickPendingIntent(R.id.carousel_portrait_1_container, pendingIntent);
                                    customBigView.setOnClickPendingIntent(R.id.custom_head_container, pendingIntent);
                                    break;

                                case 2:
                                    carousel_v1.setImageViewBitmap(R.id.carousel_portrait_2_image, renderableImagesList.get(i));
                                    carousel_v1.setTextViewText(R.id.carousel_portrait_2_desc, new WEHtmlParserInterface().fromHtml(callToActions.get(rightImageIndex).getText()));
                                    break;
                            }
                        }
                        carousel_v1.setViewVisibility(R.id.carousel_body_landscape, View.GONE);

                    } else {
                        Logger.d(WebEngageConstant.TAG, "rendering auto carousel portrait mode ");

                        totalSizeOfImages = 0;
                        for (int i = 0; i < validImages.size(); i++) {
                            totalSizeOfImages += (ImageOptimizationUtil.getImageByteCount(validImages.get(i)) * 3);
                        }

                        Logger.d(WebEngageConstant.TAG, "totalSizeOfImages" + totalSizeOfImages);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && totalSizeOfImages > maxPossibleSizeOfBitmap)
                            renderableImagesList = ImageOptimizationUtil.getOptimizedRenderableImageLists(validImages, maxPossibleSizeOfBitmap, validImages.size());
                        carousel_v1 = new RemoteViews(this.applicationContext.getPackageName(), R.layout.autocarousel);

                        for (int i = 0; i < renderableImagesList.size(); i++) {
                            int leftImageIndex = (i - 1 + renderableImagesList.size()) % renderableImagesList.size();
                            int rightImageIndex = (i + 1) % renderableImagesList.size();

                            RemoteViews autocarousel_item = new RemoteViews(this.applicationContext.getPackageName(), R.layout.autocarousel_item);

                            autocarousel_item.setViewVisibility(R.id.carousel_body_landscape, View.GONE);

                            autocarousel_item.setImageViewBitmap(R.id.carousel_portrait_1_image, renderableImagesList.get(i));
                            autocarousel_item.setTextViewText(R.id.carousel_portrait_1_desc, new WEHtmlParserInterface().fromHtml(callToActions.get(i).getText()));

                            autocarousel_item.setImageViewBitmap(R.id.carousel_portrait_0_image, renderableImagesList.get(leftImageIndex));
                            autocarousel_item.setTextViewText(R.id.carousel_portrait_0_desc, new WEHtmlParserInterface().fromHtml(callToActions.get(leftImageIndex).getText()));

                            autocarousel_item.setImageViewBitmap(R.id.carousel_portrait_2_image, renderableImagesList.get(rightImageIndex));
                            autocarousel_item.setTextViewText(R.id.carousel_portrait_2_desc, new WEHtmlParserInterface().fromHtml(callToActions.get(rightImageIndex).getText()));

                            PendingIntent leftPendingIntent = PendingIntentFactory.constructCarouselBrowsePendingIntent(this.applicationContext, pushNotificationData, i, WebEngageConstant.LEFT, "autocarousel_v1_left", browseExtraData);
                            PendingIntent rightPendingIntent = PendingIntentFactory.constructCarouselBrowsePendingIntent(this.applicationContext, pushNotificationData, i, WebEngageConstant.RIGHT, "autocarousel_v1_right", browseExtraData);
                            PendingIntent clickPendingIntent = PendingIntentFactory.constructPushClickPendingIntent(this.applicationContext, pushNotificationData, callToActions.get(i), shouldDismissOnClick);
                            autocarousel_item.setOnClickPendingIntent(R.id.prev, leftPendingIntent);
                            autocarousel_item.setOnClickPendingIntent(R.id.next, rightPendingIntent);
                            autocarousel_item.setOnClickPendingIntent(R.id.carousel_portrait_1_container, clickPendingIntent);
                            customBigView.setOnClickPendingIntent(R.id.custom_head_container, clickPendingIntent);
                            carousel_v1.addView(R.id.carousel_v1_viewflipper, autocarousel_item);
                            carousel_v1.setInt(R.id.carousel_v1_viewflipper, "setFlipInterval", pushNotificationData.getCarouselV1Data().getAutoScrollTime());
                        }

                    }

                } else if (WebEngageConstant.LANDSCAPE.equals(carouselV1.getMODE())) {
                    if (pushNotificationData.getCarouselV1Data().getAutoScrollTime() == -1 || shouldAutoScroll) {
                        Logger.d(WebEngageConstant.TAG, "rendering manual carousel landscape mode ");
                        carousel_v1.setViewVisibility(R.id.carousel_body_portrait, View.GONE);
                        carousel_v1.setImageViewBitmap(R.id.carousel_landscape_image, ImageOptimizationUtil.getRenderableImage(validImages.get(0), maxPossibleSizeOfBitmap));
                        carousel_v1.setTextViewText(R.id.carousel_landscape_desc, new WEHtmlParserInterface().fromHtml(callToActions.get(currentCenterIndex).getText()));
                        PendingIntent pendingIntent = PendingIntentFactory.constructPushClickPendingIntent(this.applicationContext, this.pushNotificationData, callToActions.get(currentCenterIndex), shouldDismissOnClick);
                        carousel_v1.setOnClickPendingIntent(R.id.carousel_landscape_container, pendingIntent);
                        customBigView.setOnClickPendingIntent(R.id.custom_head_container, pendingIntent);
                    } else {
                        Logger.d(WebEngageConstant.TAG, "rendering auto carousel landscape mode ");
                        totalSizeOfImages = 0;
                        for (int i = 0; i < validImages.size(); i++) {
                            totalSizeOfImages += ImageOptimizationUtil.getImageByteCount(validImages.get(i));
                        }
                        Logger.d(WebEngageConstant.TAG, "totalSizeOfImages" + totalSizeOfImages);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && totalSizeOfImages > maxPossibleSizeOfBitmap)
                            renderableImagesList = ImageOptimizationUtil.getOptimizedRenderableImageLists(validImages, maxPossibleSizeOfBitmap, validImages.size());
                        carousel_v1 = new RemoteViews(this.applicationContext.getPackageName(), R.layout.autocarousel);
                        Logger.d(WebEngageConstant.TAG, "validImageSize " + validImages.size());
                        Logger.d(WebEngageConstant.TAG, "imageListSize " + renderableImagesList.size());
                        for (int i = 0; i < renderableImagesList.size(); i++) {
                            Logger.d(WebEngageConstant.TAG, "adding text - " + callToActions.get(i));
                            RemoteViews autocarousel_item = new RemoteViews(this.applicationContext.getPackageName(), R.layout.autocarousel_item);
                            autocarousel_item.setViewVisibility(R.id.carousel_body_portrait, View.GONE);
                            autocarousel_item.setImageViewBitmap(R.id.carousel_landscape_image, renderableImagesList.get(i));
                            autocarousel_item.setTextViewText(R.id.carousel_landscape_desc, new WEHtmlParserInterface().fromHtml(callToActions.get(i).getText()));
                            PendingIntent leftPendingIntent = PendingIntentFactory.constructCarouselBrowsePendingIntent(this.applicationContext, pushNotificationData, i, WebEngageConstant.LEFT, "autocarousel_v1_left", browseExtraData);
                            PendingIntent rightPendingIntent = PendingIntentFactory.constructCarouselBrowsePendingIntent(this.applicationContext, pushNotificationData, i, WebEngageConstant.RIGHT, "autocarousel_v1_right", browseExtraData);
                            PendingIntent clickPendingIntent = PendingIntentFactory.constructPushClickPendingIntent(this.applicationContext, pushNotificationData, callToActions.get(i), shouldDismissOnClick);
                            autocarousel_item.setOnClickPendingIntent(R.id.prev, leftPendingIntent);
                            autocarousel_item.setOnClickPendingIntent(R.id.next, rightPendingIntent);
                            autocarousel_item.setOnClickPendingIntent(R.id.carousel_landscape_container, clickPendingIntent);
                            customBigView.setOnClickPendingIntent(R.id.custom_head_container, clickPendingIntent);
                            carousel_v1.addView(R.id.carousel_v1_viewflipper, autocarousel_item);
                            carousel_v1.setInt(R.id.carousel_v1_viewflipper, "setFlipInterval", pushNotificationData.getCarouselV1Data().getAutoScrollTime());
                        }
                    }
                }

                customBigView.removeAllViews(R.id.custom_base_container);
                if (!hasBackgroundColor) {
                    showTextViews();
                } else {
                    showNativeTextViews();
                    carousel_v1.setInt(R.id.carousel_v1_body, "setBackgroundColor", pushNotificationData.getBackgroundColor());
                    customBigView.setInt(R.id.custom_small_head_container, "setBackgroundColor", pushNotificationData.getBackgroundColor());
                    customBigView.setInt(R.id.custom_head_container, "setBackgroundColor", pushNotificationData.getBackgroundColor());
                    customBigView.setInt(R.id.custom_container, "setBackgroundColor", pushNotificationData.getBackgroundColor());
                }

                customBigView.addView(R.id.custom_base_container, carousel_v1);
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
