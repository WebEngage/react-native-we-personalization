package com.webengage.sdk.android;

import android.content.Context;

import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

/**
 * Created by shahrukhimam on 15/09/17.
 */

public class PushChannelConfiguration {


    private String notificationChannelName;
    private String notificationChannelDescription;
    private int notificationChannelImportance;
    private String notificationChannelGroup;
    private int notificationChannelLightColor;
    private int notificationChannelLockScreenVisibility;
    private boolean notificationChannelShowBadge;
    private String notificationChannelSound;
    private boolean notificationChannelVibration;
    private String notificationChannelId;


    private boolean isNotificationChannelIdSet;
    private boolean isNotificationChannelNameSet;
    private boolean isNotificationChannelDescriptionSet;
    private boolean isNotificationChannelImportanceSet;
    private boolean isNotificationChannelGroupSet;
    private boolean isNotificationLightColorSet;
    private boolean isNotificationLockScreenVisibilitySet;
    private boolean isNotificationChannelShowBadgeSet;
    private boolean isNotificationChannelSoundSet;
    private boolean isNotificationChannelVibrationSet;

    private PushChannelConfiguration(Builder builder) {
        this.notificationChannelName = builder.notificationChannelName;
        this.notificationChannelDescription = builder.notificationChannelDescription;
        this.notificationChannelImportance = builder.notificationChannelImportance;
        this.notificationChannelGroup = builder.notificationChannelGroup;
        this.notificationChannelLightColor = builder.notificationChannelLightColor;
        this.notificationChannelLockScreenVisibility = builder.notificationChannelLockScreenVisibility;
        this.notificationChannelShowBadge = builder.notificationChannelShowBadge;
        this.notificationChannelSound = builder.notificationChannelSound;
        this.notificationChannelVibration = builder.notificationChannelVibration;
        this.notificationChannelId = builder.notificationChannelId;

        this.isNotificationChannelIdSet = builder.isNotificationChannelIdSet;
        this.isNotificationChannelNameSet = builder.isNotificationChannelNameSet;
        this.isNotificationChannelDescriptionSet = builder.isNotificationChannelDescriptionSet;
        this.isNotificationChannelImportanceSet = builder.isNotificationChannelImportanceSet;
        this.isNotificationChannelGroupSet = builder.isNotificationChannelGroupSet;
        this.isNotificationLightColorSet = builder.isNotificationLightColorSet;
        this.isNotificationLockScreenVisibilitySet = builder.isNotificationLockScreenVisibilitySet;
        this.isNotificationChannelShowBadgeSet = builder.isNotificationChannelShowBadgeSet;
        this.isNotificationChannelSoundSet = builder.isNotificationChannelSoundSet;
        this.isNotificationChannelVibrationSet = builder.isNotificationChannelVibrationSet;
    }

    public String getNotificationChannelName() {
        return this.notificationChannelName;
    }

    public String getNotificationChannelDescription() {
        return this.notificationChannelDescription;
    }

    public int getNotificationChannelImportance() {
        return this.notificationChannelImportance;
    }

    public String getNotificationChannelGroup() {
        return this.notificationChannelGroup;
    }

    public int getNotificationChannelLightColor() {
        return this.notificationChannelLightColor;
    }

    public int getNotificationChannelLockScreenVisibility() {
        return this.notificationChannelLockScreenVisibility;
    }

    public boolean isNotificationChannelShowBadge() {
        return this.notificationChannelShowBadge;
    }

    public String getNotificationChannelSound() {
        return this.notificationChannelSound;
    }

    public boolean isNotificationChannelVibration() {
        return this.notificationChannelVibration;
    }

    public String getNotificationChannelId() {
        return this.notificationChannelId;
    }

    public boolean isNotificationChannelIdSet() {
        return isNotificationChannelIdSet;
    }

    public boolean isNotificationChannelNameSet() {
        return isNotificationChannelNameSet;
    }

    public boolean isNotificationChannelDescriptionSet() {
        return isNotificationChannelDescriptionSet;
    }

    public boolean isNotificationChannelImportanceSet() {
        return isNotificationChannelImportanceSet;
    }

    public boolean isNotificationChannelGroupSet() {
        return isNotificationChannelGroupSet;
    }

    public boolean isNotificationLightColorSet() {
        return isNotificationLightColorSet;
    }

    public boolean isNotificationLockScreenVisibilitySet() {
        return isNotificationLockScreenVisibilitySet;
    }

    public boolean isNotificationChannelShowBadgeSet() {
        return isNotificationChannelShowBadgeSet;
    }

    public boolean isNotificationChannelSoundSet() {
        return isNotificationChannelSoundSet;
    }

    public boolean isNotificationChannelVibrationSet() {
        return isNotificationChannelVibrationSet;
    }


    public boolean isValid(Context context) {
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (WebEngageUtils.isEmpty(this.getNotificationChannelId())) {
                Logger.e(WebEngageConstant.TAG, " Notification channel cannot be null or empty");
                return false;
            }

            if (WebEngageUtils.isEmpty(this.getNotificationChannelName())) {
                Logger.e(WebEngageConstant.TAG, " Notification channel name cannot be null or empty");
                return false;
            }

            if (this.getNotificationChannelImportance() < 0 || this.getNotificationChannelImportance() > 5) {
                Logger.e(WebEngageConstant.TAG, " Notification channel importance should be >=0 && <= 5");
                return false;
            }
            if (this.getNotificationChannelGroup() != null) {
                if (!PushChannelManager.isChannelGroupPresent(this.getNotificationChannelGroup(), applicationContext)) {
                    Logger.e(WebEngageConstant.TAG, " Notification channel group with id: " + this.getNotificationChannelGroup() + " is not yet registered");
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static class Builder {
        private String notificationChannelId = WebEngageConstant.DEFAULT_PUSH_CHANNEL_ID;
        private String notificationChannelName = WebEngageConstant.DEFAULT_PUSH_CHANNEL_NAME;
        private String notificationChannelDescription = null;
        private int notificationChannelImportance = WebEngageConstant.DEFAULT_PUSH_CHANNEL_IMPORTANCE;
        private String notificationChannelGroup = null;
        private int notificationChannelLightColor = -1;
        private int notificationChannelLockScreenVisibility = 1;
        private boolean notificationChannelShowBadge = true;
        private String notificationChannelSound = null;
        private boolean notificationChannelVibration = true;


        private boolean isNotificationChannelIdSet = false;
        private boolean isNotificationChannelNameSet = false;
        private boolean isNotificationChannelDescriptionSet = false;
        private boolean isNotificationChannelImportanceSet = false;
        private boolean isNotificationChannelGroupSet = false;
        private boolean isNotificationLightColorSet = false;
        private boolean isNotificationLockScreenVisibilitySet = false;
        private boolean isNotificationChannelShowBadgeSet = false;
        private boolean isNotificationChannelSoundSet = false;
        private boolean isNotificationChannelVibrationSet = false;

        public Builder() {

        }

        public Builder setNotificationChannelName(String notificationChannelName) {
            this.notificationChannelName = notificationChannelName;
            this.isNotificationChannelNameSet = true;
            return this;
        }

        public Builder setNotificationChannelImportance(int notificationChannelImportance) {
            this.notificationChannelImportance = notificationChannelImportance;
            this.isNotificationChannelImportanceSet = true;
            return this;
        }


        public Builder setNotificationChannelDescription(String notificationChannelDescription) {
            this.notificationChannelDescription = notificationChannelDescription;
            this.isNotificationChannelDescriptionSet = true;
            return this;
        }

        public Builder setNotificationChannelGroup(String group) {
            this.notificationChannelGroup = group;
            this.isNotificationChannelGroupSet = true;
            return this;
        }

        public Builder setNotificationChannelLightColor(int argb) {
            this.notificationChannelLightColor = argb;
            this.isNotificationLightColorSet = true;
            return this;
        }

        public Builder setNotificationChannelLockScreenVisibility(int lockScreenVisibility) {
            this.notificationChannelLockScreenVisibility = lockScreenVisibility;
            this.isNotificationLockScreenVisibilitySet = true;
            return this;
        }

        public Builder setNotificationChannelShowBadge(boolean showBadge) {
            this.notificationChannelShowBadge = showBadge;
            this.isNotificationChannelShowBadgeSet = true;
            return this;
        }

        public Builder setNotificationChannelSound(String sound) {
            this.notificationChannelSound = sound;
            this.isNotificationChannelSoundSet = true;
            return this;
        }

        public Builder setNotificationChannelVibration(boolean vibration) {
            this.notificationChannelVibration = vibration;
            this.isNotificationChannelVibrationSet = true;
            return this;
        }

        public PushChannelConfiguration build() {
            PushChannelConfiguration pushChannelConfiguration = new PushChannelConfiguration(this);
            return pushChannelConfiguration;
        }

    }


    @Override
    public int hashCode() {
        return this.notificationChannelId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PushChannelConfiguration) {
            PushChannelConfiguration pushChannelConfiguration = (PushChannelConfiguration) obj;
            return pushChannelConfiguration.getNotificationChannelId().equals(this.getNotificationChannelId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "ChannelId: " + this.getNotificationChannelId()
                + "\nChannelName: " + this.getNotificationChannelName()
                + "\nChannelImportance: " + this.getNotificationChannelImportance()
                + "\nChannelDescription: " + this.getNotificationChannelDescription()
                + "\nChannelGroup: " + this.getNotificationChannelGroup()
                + "\nChannelColor: " + this.getNotificationChannelLightColor()
                + "\nLockScreenVisibility: " + this.getNotificationChannelLockScreenVisibility()
                + "\nShowBadge: " + this.isNotificationChannelShowBadge()
                + "\nSound: " + this.getNotificationChannelSound()
                + "\nVibration: " + this.isNotificationChannelVibration();

    }
}
