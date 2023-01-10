package com.webengage.sdk.android.actions.render;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.utils.WebEngageConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


class BaseNotificationDataHolder {
    private String title = null;
    private String message = null;
    private String summary = null;
    private String variationId = null;
    private boolean render = true;
    private Uri sound = null;
    private String type = null;
    private Bundle customData = null;
    private boolean vibrate = false;
    private int ledColor = 0;
    private JSONObject jsonObject = null;
    private String experimentId = null;
    private String license_code = null;
    private int priority = 0;
    private String channelId = null;
    private boolean amplified = false;

    public BaseNotificationDataHolder(JSONObject notificationJson, String type, Context context) throws JSONException {
        this.jsonObject = notificationJson;
        this.title = notificationJson.isNull("rt") ? null : notificationJson.getString("rt");
        if (TextUtils.isEmpty(this.title)) {
            this.title = notificationJson.isNull("title") ? null : notificationJson.getString("title");
        }
        if (this.title == null) {
            this.title = context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
        }
        if (notificationJson.isNull("license_code")) {
            throw new JSONException("license_code is null");
        }
        this.license_code = notificationJson.getString("license_code");
        if (!WebEngage.get().getWebEngageConfig().getWebEngageKey().equalsIgnoreCase(this.license_code)) {
            throw new JSONException("license_code mismatch , received license_code : " + this.license_code + ", integrated license code : " + WebEngage.get().getWebEngageConfig().getWebEngageKey());
        }

        this.message = notificationJson.isNull("rm") ? null : notificationJson.getString("rm");
        if (TextUtils.isEmpty(this.message)) {
            this.message = notificationJson.getString("message");
        }

        if (TextUtils.isEmpty(this.message)) {
            throw new JSONException("message is Null");
        }


        if (notificationJson.isNull("experimentId")) {
            throw new JSONException("experimentId is null");
        }

        this.summary = notificationJson.isNull("rst") ? null : notificationJson.getString("rst");
        this.experimentId = notificationJson.getString("experimentId");
        this.render = true;
        this.sound = null;
        this.vibrate = notificationJson.optBoolean("vib", false);
        this.ledColor = 0;
        if (!notificationJson.isNull("snd")) {
            int id = context.getResources().getIdentifier(notificationJson.optString("snd"), "raw", context.getPackageName());
            if (id != 0) {
                this.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + id);
            } else {
                this.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        this.priority = notificationJson.optInt("prt", 0);
        if (!notificationJson.isNull("chId")) {
            this.channelId = notificationJson.optString("chId");
        }

        if (notificationJson.isNull("identifier")) {
            throw new JSONException("Notification ID is Null");
        }
        this.variationId = notificationJson.getString("identifier");
        this.type = type;
        customData = new Bundle();
        JSONArray customDataArray = notificationJson.isNull("custom") ? null : notificationJson.optJSONArray("custom");
        if (customDataArray != null) {
            for (int i = 0; i < customDataArray.length(); i++) {
                try {
                    JSONObject jsonObject = customDataArray.getJSONObject(i);
                    customData.putString(jsonObject.getString("key"), jsonObject.getString("value"));
                } catch (JSONException e) {

                }
            }
        }

        this.amplified = notificationJson.optBoolean(WebEngageConstant.AMPLIFIED);
    }

    public JSONObject getPushPayloadJSON(){
        return this.jsonObject;
    }

    String getNotificationType() {
        return this.type;
    }

    public String getVariationId() {
        return this.variationId;
    }

    public void setShouldRender(boolean shouldRender) {
        this.render = shouldRender;
    }

    @Deprecated
    public void setVibrateFlag(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public boolean getVibrateFlag() {
        return this.vibrate;
    }

    public void setLedLight(int argb) {
        this.ledColor = argb;
    }

    public int getLedColor() {
        return this.ledColor;
    }

    @Deprecated
    public void setSound(Uri sound) {
        this.sound = sound;
    }


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentText() {
        return this.message;
    }

    public void setContentText(String text) {
        this.message = text;
    }

    public boolean shouldRender() {
        return this.render;
    }

    public Uri getSound() {
        return this.sound;
    }

    @Deprecated
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public Bundle getCustomData() {
        return this.customData;
    }

    @Deprecated
    public void setCustomData(Bundle customData) {
        this.customData = customData;
    }

    public String getExperimentId() {
        return this.experimentId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return this.channelId;
    }

    public boolean isAmplified() {
        return this.amplified;
    }

    public String getContentSummary() {
        return summary;
    }

    public void setContentSummary(String summary) {
        this.summary = summary;
    }

}
