package com.webengage.sdk.android.actions.render;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class InAppNotificationData implements Parcelable {

    private String experimentId;
    private String layoutId;
    private String variationId;
    private JSONObject data;
    private boolean render = true;
    private InAppType inAppType = null;


    private InAppNotificationData(Parcel parcel) {
        try {
            this.data = new JSONObject(parcel.readString());
            this.experimentId = parcel.readString();
            this.layoutId = parcel.readString();
            this.variationId = parcel.readString();
            this.render = parcel.readByte() != 0;
            this.inAppType = (InAppType) parcel.readSerializable();
        } catch (Exception e) {
        }

    }

    public InAppNotificationData(String experimentId, String variationId, String layoutId, String notificationBaseData) throws Exception {
        if (experimentId == null || notificationBaseData == null || variationId == null || layoutId == null) {
            throw new IllegalArgumentException("InApp:experimentId: " + experimentId + " data: " + notificationBaseData + " variationId: " + variationId + " layoutId: " + layoutId);
        }
        JSONObject jsonObject = new JSONObject(notificationBaseData);
        if (jsonObject.isNull("status")) {
            throw new IllegalArgumentException("status object not found in notification response");
        }
        JSONObject status = jsonObject.optJSONObject("status");
        boolean success = status.optBoolean("success", false);
        if (!success) {
            throw new IllegalArgumentException("success value found as false in notification response");
        }
        this.data = jsonObject.optJSONObject("templateData");
        this.experimentId = experimentId;
        this.variationId = variationId;
        this.layoutId = layoutId;
        JSONObject layoutAttributes = this.data.getJSONObject("layoutAttributes");
        this.inAppType = layoutAttributes.isNull("type") ? null : InAppType.valueOf(layoutAttributes.optString("type"));
        if (this.inAppType == null) {
            throw new JSONException("Notification Type is NUll");
        }

    }

    public JSONObject getData() {
        return data;
    }

    public void setNotificationData(JSONObject data) {
        this.data = data;
    }

    public String getExperimentId() {
        return this.experimentId;
    }

    public String getLayoutId() {
        return this.layoutId;
    }


    public String getVariationId() {
        return this.variationId;
    }

    public void setShouldRender(boolean shouldRender) {
        this.render = shouldRender;
    }

    protected InAppType getInAppType() {
        return inAppType;
    }

    public boolean shouldRender() {
        return this.render;
    }


    public static final Parcelable.Creator<InAppNotificationData> CREATOR = new Parcelable.Creator<InAppNotificationData>() {
        @Override
        public InAppNotificationData createFromParcel(Parcel source) {
            return new InAppNotificationData(source);
        }

        @Override
        public InAppNotificationData[] newArray(int size) {
            return new InAppNotificationData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data.toString());
        dest.writeString(this.experimentId);
        dest.writeString(this.layoutId);
        dest.writeString(this.variationId);
        dest.writeByte((byte) (render ? 1 : 0));
        dest.writeSerializable(this.inAppType);

    }

    public enum InAppType {
        BLOCKING,
        HEADER,
        FOOTER,
        MODAL
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        InAppNotificationData inAppNotificationData = (InAppNotificationData) o;
        return this.experimentId.equals(inAppNotificationData.getExperimentId());
    }

    @Override
    public int hashCode() {
        return this.experimentId.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        sb.append(this.getClass().getName() + " Object { " + NEW_LINE);
        sb.append(" Experiment Id : " + getExperimentId() + NEW_LINE);
        sb.append(" Variation Id : " + getVariationId() + NEW_LINE);
        sb.append(" Data : " + this.getData().toString() + NEW_LINE);
        sb.append("}");
        return sb.toString();
    }
}


