package com.webengage.sdk.android.actions.render;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import com.webengage.sdk.android.utils.htmlspanner.WEHtmlParserInterface;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.PendingIntentFactory;
import com.webengage.sdk.android.PushChannelManager;
import com.webengage.sdk.android.R;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.utils.ManifestUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class PushRenderer {
    protected Context applicationContext = null;
    protected PushNotificationData pushNotificationData = null;
    protected int hashedNotificationID;
    protected boolean buildCustomPush = false;
    protected List<Bitmap> validImages = null;
    protected Notification.Builder mBuilder = null;
    protected RemoteViews customBigView = null;
    protected Long when;
    private boolean hasBackgroundColor = false;
    private boolean isAndroid12 = false;

    protected boolean onRender(Context context, PushNotificationData pushNotificationData) {
        this.initRender(context, pushNotificationData);
        this.downloadImages();
        this.loadImages();
        this.buildCollapsedPush();
        this.buildExpandedPush();
        this.addIntents();
        this.show();
        return true;
    }

    protected boolean onRerender(Context context, PushNotificationData pushNotificationData, Bundle extras) {

        if (extras != null) {
            this.when = extras.getLong(WebEngageConstant.WHEN);
        }

        this.initRender(context, pushNotificationData);
        this.loadImages();
        this.buildCollapsedPush();
        this.buildExpandedPush();
        this.addIntents();
        this.show();
        return true;
    }

    private void initRender(Context context, PushNotificationData pushNotificationData) {
        this.applicationContext = context.getApplicationContext();
        this.pushNotificationData = pushNotificationData;
        this.hashedNotificationID = pushNotificationData.getVariationId().hashCode();
        this.validImages = new ArrayList<Bitmap>();

        Bundle customData = pushNotificationData.getCustomData();
        this.buildCustomPush = customData != null && customData.containsKey(WebEngageConstant.WE_PUSH_CUSTOM) && Boolean.parseBoolean(customData.getString(WebEngageConstant.WE_PUSH_CUSTOM));

        if (pushNotificationData.getBackgroundColor() != Color.parseColor("#00000000"))
            this.hasBackgroundColor = true;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.S)
            this.isAndroid12 = true;

    }

    protected void dispatchException(Exception e) {
        Intent intent = IntentFactory.newIntent(Topic.EXCEPTION, e, applicationContext);
        WebEngage.startService(intent, applicationContext);
    }

    /**
     * Download and save images in cache
     */
    abstract void downloadImages();

    /**
     * Add images to valid images list
     */
    abstract void loadImages();

    protected Response loadImageResponse(RequestObject requestObject) {
        Response response = null;
        for (int i = 0; i < 5; i++) {  // Retrying 5 times to download complete image
            if (response != null) {
                response.closeErrorStream();
            }
            response = requestObject.execute();
            if (response.isReadable() || response.getResponseCode() >= 400) {
                break;
            }
        }
        return response;
    }

    protected Bitmap loadImage(Response response) {
        if (response != null) {
            if (response.isReadable()) {
                try {
                    return BitmapFactory.decodeStream(response.getInputStream());
                } catch (Exception e) {
                    Logger.e(WebEngageConstant.TAG, "Exception while decoding input stream to bitmap.", e);
                } catch (OutOfMemoryError error) {
                    Logger.e(WebEngageConstant.TAG, "Error while decoding input stream to bitmap.", error);
                } finally {
                    response.closeInputStream();
                }
            } else {
                Logger.e(WebEngageConstant.TAG, "Could not download image " + response.getURL() + ", response code: " + response.getResponseCode());
                response.closeErrorStream();
            }
        }
        return null;
    }

    private void buildCollapsedPush() {
        String channelId = pushNotificationData.getChannelId();
        if (Build.VERSION.SDK_INT >= 26) {
            if (channelId != null) {
                if (!PushChannelManager.isChannelPresent(channelId, applicationContext)) {
                    channelId = WebEngageConstant.DEFAULT_PUSH_CHANNEL_ID;
                }
            } else {
                channelId = WebEngageConstant.DEFAULT_PUSH_CHANNEL_ID;
            }
            mBuilder = new Notification.Builder(this.applicationContext, channelId);
        } else {
            mBuilder = new Notification.Builder(this.applicationContext);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setVisibility(Notification.VISIBILITY_PRIVATE);
        }
        mBuilder.setSmallIcon(pushNotificationData.getSmallIcon());

        if (buildCustomPush || hasBackgroundColor) {
            RemoteViews customPushBase = constructCustomPushBase();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBuilder.setCustomContentView(customPushBase);
            } else {
                mBuilder.setContent(customPushBase);
            }
        }

        mBuilder.setContentTitle(new WEHtmlParserInterface().fromHtml(pushNotificationData.getTitle()))
                .setContentText(new WEHtmlParserInterface().fromHtml(pushNotificationData.getContentText()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && pushNotificationData.getContentSummary() != null) {
            mBuilder.setSubText(new WEHtmlParserInterface().fromHtml(pushNotificationData.getContentSummary()));
        }


        if (pushNotificationData.getLargeIcon() != null) {
            mBuilder.setLargeIcon(pushNotificationData.getLargeIcon());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBuilder.setLargeIcon(Icon.createWithResource(this.applicationContext,
                    this.applicationContext.getApplicationInfo().icon));
        }

        if (pushNotificationData.isSticky())
            mBuilder.setOngoing(true);

    }

    private RemoteViews constructCustomPushBase() {
        RemoteViews pushBase = getCommonExpandedPushBase();
        pushBase.setViewVisibility(R.id.push_base_margin_view, View.VISIBLE);
        pushBase.setTextViewText(R.id.custom_title, new WEHtmlParserInterface().fromHtml(pushNotificationData.getTitle()));
        pushBase.setTextViewText(R.id.custom_message, new WEHtmlParserInterface().fromHtml(pushNotificationData.getContentText()));
        pushBase.setTextViewText(R.id.custom_title_native, new WEHtmlParserInterface().fromHtml(pushNotificationData.getTitle()));
        pushBase.setTextViewText(R.id.custom_message_native, new WEHtmlParserInterface().fromHtml(pushNotificationData.getContentText()));
        if (!TextUtils.isEmpty(pushNotificationData.getContentSummary())) {
            pushBase.setTextViewText(R.id.custom_summary, new WEHtmlParserInterface().fromHtml(pushNotificationData.getContentSummary()));
            pushBase.setTextViewText(R.id.custom_summary_native, new WEHtmlParserInterface().fromHtml(pushNotificationData.getContentSummary()));
        } else {
            pushBase.setViewVisibility(R.id.custom_summary, View.GONE);
            pushBase.setViewVisibility(R.id.custom_summary_native, View.GONE);
        }
        pushBase.setImageViewResource(R.id.small_icon, pushNotificationData.getSmallIcon());
        if (when == null) {
            when = System.currentTimeMillis();
        }
        DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(applicationContext);
        String time = dateFormat.format(new Date());
        pushBase.setTextViewText(R.id.custom_notification_time, time);
        pushBase.setTextViewText(R.id.custom_notification_time_native, time);

        if (!hasBackgroundColor) {
            showTextViews(pushBase);
        } else {
            showNativeTextViews(pushBase);
        }

        return pushBase;

    }

    protected RemoteViews getCommonExpandedPushBase() {
        RemoteViews pushBase = new RemoteViews(this.applicationContext.getPackageName(), R.layout.push_base);
        int targetSdkVersion = this.applicationContext.getApplicationInfo().targetSdkVersion;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && targetSdkVersion >= Build.VERSION_CODES.N) {
            //For Android 12 , template is provided for all notifications.
            // So hide the View used for showing the app name in custom notifications
            if (isAndroid12) {
                pushBase.setViewVisibility(R.id.custom_small_head_container, View.GONE);
            } else {
                pushBase.setViewVisibility(R.id.custom_small_head_container, View.VISIBLE);
                int smallIcon = pushNotificationData.getSmallIcon();
                if (smallIcon != -1) {
                    pushBase.setImageViewResource(R.id.small_icon, smallIcon);
                } else {
                    pushBase.setImageViewIcon(R.id.small_icon, Icon.createWithResource(this.applicationContext, this.applicationContext.getApplicationInfo().icon));
                }

                String appName = pushNotificationData.getAppName();
                if (appName != null) {
                    pushBase.setTextViewText(R.id.app_name, appName);
                    pushBase.setTextViewText(R.id.app_name_native, appName);
                }

                if (when == null) {
                    when = System.currentTimeMillis();
                }
                mBuilder.setWhen(when);

                DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(applicationContext);
                String time = dateFormat.format(new Date());
                pushBase.setTextViewText(R.id.custom_notification_time, time);
                pushBase.setTextViewText(R.id.custom_notification_time_native, time);

            }
            if (isAndroid12) {
                int leftPadding = applicationContext.getResources().getDimensionPixelSize(R.dimen.we_push_content_margin_colorbg);
                if(!hasBackgroundColor)
                    leftPadding = 0;
                pushBase.setViewPadding(R.id.custom_head_container, leftPadding, 0, 0, 0);
            }
            if (hasBackgroundColor) {
                pushBase.setInt(R.id.push_base_container, "setBackgroundColor", pushNotificationData.getBackgroundColor());
            }
        } else {
            pushBase.setViewVisibility(R.id.custom_small_head_container, View.GONE);
        }

        if (pushNotificationData.getLargeIcon() != null) {
            pushBase.setImageViewBitmap(R.id.custom_icon, pushNotificationData.getLargeIcon());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pushBase.setImageViewIcon(R.id.custom_icon, Icon.createWithResource(this.applicationContext, this.applicationContext.getApplicationInfo().icon));
        }

        pushBase.setViewVisibility(R.id.push_base_margin_view, View.GONE);

        return pushBase;
    }

    /**
     * Construct big style push notification
     */
    abstract void buildExpandedPush();

    private void addIntents() {
        // actions
        List<CallToAction> callToActions = pushNotificationData.getCallToActions();
        if (callToActions != null && callToActions.size() > 0) {
            int actionNumber = 0;
            for (CallToAction callToAction : callToActions) {
                if (callToAction.isPrimeAction()) {
                    mBuilder.setContentIntent(PendingIntentFactory.constructPushClickPendingIntent(this.applicationContext, this.pushNotificationData, callToAction, true));
                } else if (callToAction.isNative()) {
                    PendingIntent ctaPendingIntent = PendingIntentFactory.constructPushClickPendingIntent(this.applicationContext, this.pushNotificationData, callToAction, true);
                    if (!buildCustomPush && !hasBackgroundColor) {
                        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 20) {
                            mBuilder.addAction(0, new WEHtmlParserInterface().fromHtml(callToAction.getText()), ctaPendingIntent);
                        } else if (Build.VERSION.SDK_INT >= 20 && Build.VERSION.SDK_INT < 23) {
                            Notification.Action action = new Notification.Action.Builder(0, new WEHtmlParserInterface().fromHtml(callToAction.getText()), ctaPendingIntent).build();
                            mBuilder.addAction(action);
                        } else if (Build.VERSION.SDK_INT >= 23) {
                            Notification.Action action = new Notification.Action.Builder(null, new WEHtmlParserInterface().fromHtml(callToAction.getText()), ctaPendingIntent).build();
                            mBuilder.addAction(action);
                        }
                    } else if (customBigView != null) {
                        actionNumber++;
                        int actionId = -1;
                        switch (actionNumber) {
                            case 1:
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                                    customBigView.setInt(R.id.action_list,
                                            "setBackgroundColor", Color.parseColor("#e8e8e8"));
                                }
                                if (applicationContext.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.S && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    int leftPadding = applicationContext.getResources().getDimensionPixelSize(R.dimen.we_push_content_margin_colorbg);
                                    customBigView.setViewPadding(R.id.actions, leftPadding, 0, 0, 0);
                                    customBigView.setViewLayoutHeight(R.id.actions, applicationContext.getResources().getDimension(R.dimen.we_push_action_list_height_template), TypedValue.COMPLEX_UNIT_PX);
                                }
                                customBigView.setViewVisibility(R.id.action_list, View.VISIBLE);
                                actionId = R.id.action1_native;
                                break;
                            case 2:
                                actionId = R.id.action2_native;
                                break;
                            case 3:
                                actionId = R.id.action3_native;
                                break;
                        }
                        if (actionId != -1) {
//                            if (pushNotificationData.getBackgroundColor() != Color.parseColor("#00000000")) {
                            customBigView.setViewVisibility(actionId, View.VISIBLE);
                            customBigView.setTextViewText(actionId, new WEHtmlParserInterface().fromHtml(callToAction.getText()));
                            customBigView.setOnClickPendingIntent(actionId, ctaPendingIntent);
//                            } else {
//                                customBigView.setViewVisibility(actionId, View.VISIBLE);
//                                customBigView.setTextViewText(actionId, new HtmlTextProcessor(callToAction.getText(), HtmlTextProcessor.DEFAULT_FONT_SIZE, applicationContext).getTrimmedText());
//                                customBigView.setOnClickPendingIntent(actionId, ctaPendingIntent);
//
//                            }
                        }
                    }
                }
            }
        }

        // delete intent
        PendingIntent deletePendingIntent = PendingIntentFactory.constructPushDeletePendingIntent(this.applicationContext, this.pushNotificationData);
        mBuilder.setDeleteIntent(deletePendingIntent);

        // set push configurations
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 26) {
            int[] ar = {Notification.PRIORITY_MIN, Notification.PRIORITY_LOW,
                    Notification.PRIORITY_DEFAULT, Notification.PRIORITY_HIGH,
                    Notification.PRIORITY_MAX};
            if (ar.length < pushNotificationData.getPriority() + 2) {
                mBuilder.setPriority(ar[pushNotificationData.getPriority() + 2]);
            }
        }

        if (Build.VERSION.SDK_INT < 26) {
            if (pushNotificationData.getVibrateFlag()) {
                if (ManifestUtils.checkPermission(ManifestUtils.VIBRATE, applicationContext)) {
                    mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                }
            }
            if (pushNotificationData.getSound() != null) {
                mBuilder.setSound(pushNotificationData.getSound());
            }
            if (pushNotificationData.getLedColor() != 0) {
                mBuilder.setLights(pushNotificationData.getLedColor(), 500, 1000);
            }
        }
    }

    protected boolean areButtonsPresent() {
        List<CallToAction> callToActions = this.pushNotificationData.getCallToActions();
        if (callToActions != null && callToActions.size() > 0) {
            for (CallToAction callToAction : callToActions) {
                if (!callToAction.isPrimeAction() && callToAction.isNative()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void show() {
        Notification notification = null;
        if (Build.VERSION.SDK_INT < 16) {
            notification = mBuilder.getNotification();
        } else {
            notification = mBuilder.build();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mBuilder.setShowWhen(true);
        }

        if (customBigView != null) {
            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT <= 23) {
                notification.bigContentView = customBigView;
            } else if (Build.VERSION.SDK_INT >= 24) {
                notification = mBuilder.setCustomBigContentView(customBigView).build();
            }
        }
        if (pushNotificationData.getAccentColor() != -1 && Build.VERSION.SDK_INT >= 21) {
            notification.color = pushNotificationData.getAccentColor();
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

        NotificationManager notificationManager = (NotificationManager) this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            notificationManager.notify(hashedNotificationID, notification);
        } catch (SecurityException e) {
            notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;
            notificationManager.notify(hashedNotificationID, notification);
        }
    }


    private RemoteViews showTextViews(RemoteViews pushBase) {
        pushBase.setViewVisibility(R.id.app_name_native, View.GONE);
        pushBase.setViewVisibility(R.id.custom_summary_native, View.GONE);
        pushBase.setViewVisibility(R.id.custom_notification_time_native, View.GONE);
        pushBase.setViewVisibility(R.id.custom_title_native, View.GONE);
        pushBase.setViewVisibility(R.id.custom_message_native, View.GONE);
        return pushBase;

    }

    private RemoteViews showNativeTextViews(RemoteViews pushBase) {
        pushBase.setViewVisibility(R.id.app_name, View.GONE);
        pushBase.setViewVisibility(R.id.custom_summary, View.GONE);
        pushBase.setViewVisibility(R.id.custom_notification_time, View.GONE);
        pushBase.setViewVisibility(R.id.custom_title, View.GONE);
        pushBase.setViewVisibility(R.id.custom_message, View.GONE);

        return pushBase;
    }
}
