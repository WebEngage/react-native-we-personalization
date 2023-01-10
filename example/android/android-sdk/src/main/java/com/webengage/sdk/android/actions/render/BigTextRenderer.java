package com.webengage.sdk.android.actions.render;


import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.webengage.sdk.android.utils.htmlspanner.WEHtmlParserInterface;
import com.webengage.sdk.android.R;
import com.webengage.sdk.android.callbacks.CustomPushRender;

public class BigTextRenderer extends PushRenderer implements CustomPushRender {

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
    void loadImages() {
    }

    private RemoteViews constructExpandedPushBase() {
        RemoteViews pushBase = getCommonExpandedPushBase();
        if (isAndroid12) {
            pushBase.setBoolean(R.id.custom_title, "setSingleLine", false);
            pushBase.setBoolean(R.id.custom_title_native, "setSingleLine", false);
            pushBase.setInt(R.id.custom_title, "setMaxLines", 2);
            pushBase.setInt(R.id.custom_title_native, "setMaxLines", 2);
        }
        pushBase.setInt(R.id.custom_message, "setMaxLines", 7);
        pushBase.setInt(R.id.custom_message_native, "setMaxLines", 7);

        pushBase.setViewVisibility(R.id.custom_base_container, View.VISIBLE);
        PushNotificationData.BigTextStyle bigTextData = pushNotificationData.getBigTextStyleData();
        if (bigTextData != null) {
            pushBase.setTextViewText(R.id.custom_title, new WEHtmlParserInterface().fromHtml(pushNotificationData.getTitle()));
            pushBase.setTextViewText(R.id.custom_message, new WEHtmlParserInterface().fromHtml(bigTextData.getBigText()));
            pushBase.setTextViewText(R.id.custom_title_native, new WEHtmlParserInterface().fromHtml(pushNotificationData.getTitle()));
            pushBase.setTextViewText(R.id.custom_message_native, new WEHtmlParserInterface().fromHtml(bigTextData.getBigText()));
            if (!TextUtils.isEmpty(pushNotificationData.getContentSummary())) {
                pushBase.setTextViewText(R.id.custom_summary, new WEHtmlParserInterface().fromHtml(bigTextData.getSummary()));
                pushBase.setTextViewText(R.id.custom_summary_native, new WEHtmlParserInterface().fromHtml(bigTextData.getSummary()));
            } else {
                pushBase.setViewVisibility(R.id.custom_summary, View.GONE);
                pushBase.setViewVisibility(R.id.custom_summary_native, View.GONE);
            }
        }
        if (!areButtonsPresent()) {
            pushBase.setViewVisibility(R.id.push_base_margin_view, View.VISIBLE);
        }
        return pushBase;
    }

    @Override
    void buildExpandedPush() {
        if (pushNotificationData.getBigTextStyleData() != null && Build.VERSION.SDK_INT >= 16) {
            if (!buildCustomPush && (pushNotificationData.getBackgroundColor() == Color.parseColor("#00000000"))) {
                Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
                bigTextStyle.setBigContentTitle(new WEHtmlParserInterface().fromHtml(pushNotificationData.getBigTextStyleData().getBigContentTitle()));
                bigTextStyle.bigText(new WEHtmlParserInterface().fromHtml(pushNotificationData.getBigTextStyleData().getBigText()));
                if (!TextUtils.isEmpty(pushNotificationData.getBigTextStyleData().getSummary()))
                    bigTextStyle.setSummaryText(new WEHtmlParserInterface().fromHtml(pushNotificationData.getBigTextStyleData().getSummary()));
                mBuilder.setStyle(bigTextStyle);
            } else {
                customBigView = constructExpandedPushBase();
                RemoteViews bigTextView = new RemoteViews(this.applicationContext.getPackageName(), R.layout.big_text);
                customBigView.removeAllViews(R.id.custom_base_container);
                customBigView.addView(R.id.custom_base_container, bigTextView);
                if (Color.parseColor("#00000000") == pushNotificationData.getBackgroundColor()) {
                    showTextViews();
                } else {
                    showNativeTextViews();
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
