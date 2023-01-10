package com.webengage.sdk.android;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.List;

/**
 * Created by shahrukhimam on 15/09/17.
 */

public class PushChannelManager {


    public static synchronized void registerPushChannel(PushChannelConfiguration pushChannelConfiguration, Context context) {
        if (context == null || pushChannelConfiguration == null) {
            throw new IllegalArgumentException("Invalid Arguments");
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(pushChannelConfiguration.getNotificationChannelId(), pushChannelConfiguration.getNotificationChannelName(), pushChannelConfiguration.getNotificationChannelImportance());

            if (pushChannelConfiguration.getNotificationChannelDescription() != null) {
                notificationChannel.setDescription(pushChannelConfiguration.getNotificationChannelDescription());
            }

            if (pushChannelConfiguration.getNotificationChannelGroup() != null) {
                notificationChannel.setGroup(pushChannelConfiguration.getNotificationChannelGroup());
            }

            if (pushChannelConfiguration.getNotificationChannelLightColor() != -1) {
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(pushChannelConfiguration.getNotificationChannelLightColor());
            }

            notificationChannel.setLockscreenVisibility(pushChannelConfiguration.getNotificationChannelLockScreenVisibility());
            notificationChannel.setShowBadge(pushChannelConfiguration.isNotificationChannelShowBadge());
            if (pushChannelConfiguration.getNotificationChannelSound() != null) {
                Uri sound = null;
                int id = context.getResources().getIdentifier(pushChannelConfiguration.getNotificationChannelSound(), "raw", context.getPackageName());
                if (id != 0) {
                    sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + id);
                } else {
                    sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Logger.e(WebEngageConstant.TAG, "No sound resources found in raw folder for name: " + pushChannelConfiguration.getNotificationChannelSound()+", using default tone.");
                }
                notificationChannel.setSound(sound, null);
            }
            notificationChannel.enableVibration(pushChannelConfiguration.isNotificationChannelVibration());

            notificationManager.createNotificationChannel(notificationChannel);

        }
    }


    public static synchronized boolean isChannelPresent(String channelId, Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            if (channelId == null || context == null) {
                return false;
            }
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager.getNotificationChannel(channelId) != null;
        } else {
            return true;
        }


    }

    public static synchronized boolean isChannelGroupPresent(String groupId, Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            if (groupId == null || context == null) {
                return false;
            }
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            List<NotificationChannelGroup> notificationChannelGroupList = notificationManager.getNotificationChannelGroups();
            if (notificationChannelGroupList != null) {
                for (NotificationChannelGroup notificationChannelGroup : notificationChannelGroupList) {
                    if (groupId.equals(notificationChannelGroup.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
