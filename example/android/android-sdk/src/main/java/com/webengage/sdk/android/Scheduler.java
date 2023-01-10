package com.webengage.sdk.android;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.webengage.sdk.android.actions.rules.ConfigurationManager;
import com.webengage.sdk.android.utils.ManifestUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.List;

class Scheduler {
    Context applicationContext = null;

    Scheduler(Context context) {
        this.applicationContext = context.getApplicationContext();
    }


    protected void scheduleSessionDestroy(long nextTime) {
        AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTime, PendingIntentFactory.constructSessionDestroyPendingIntent(applicationContext));
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, PendingIntentFactory.constructSessionDestroyPendingIntent(applicationContext));
        }
    }


    protected void cancelSessionDestroy() {
        AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntentFactory.constructSessionDestroyPendingIntent(applicationContext));
    }


    protected void scheduleNextSync(long nextTime) {
        AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, nextTime, PendingIntentFactory.constructNextSyncPendingIntent(applicationContext));
        } else {
            alarmManager.set(AlarmManager.RTC, nextTime, PendingIntentFactory.constructNextSyncPendingIntent(applicationContext));
        }
    }

    protected void scheduleConfigRefresh(long nextTime) {
        AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, nextTime, PendingIntentFactory.constructConfigRefreshPendingIntent(applicationContext));
    }

    protected void scheduleUserProfileCall(long nextTime) {
        AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, nextTime, PendingIntentFactory.constructUserProfileFetchPendingIntent(applicationContext));
    }


    protected void cancelUserProfileCall() {
        AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntentFactory.constructUserProfileFetchPendingIntent(applicationContext));
    }


    protected void cancelNextSync() {
        AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntentFactory.constructNextSyncPendingIntent(applicationContext));
    }


    protected void scheduleLeaveIntentEvent(ConfigurationManager configurationManager) {
        if (configurationManager == null) {
            return;
        }
        if (configurationManager.shouldDoLeaveIntent()) {
            AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + WebEngageConstant.LEAVE_INTENT_TRIGGER_DELAY, PendingIntentFactory.constructLeaveIntentPendingIntent(this.applicationContext));
        }
    }

    protected void cancelLeaveIntentEvent(ConfigurationManager configurationManager) {
        if (configurationManager == null) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntentFactory.constructLeaveIntentPendingIntent(this.applicationContext));
    }

    protected void schedulePageDelayEvents(List<Object> pageDelayValues) {
        scheduleDelayEvents(EventName.WE_WK_PAGE_DELAY, pageDelayValues);
    }


    protected void cancelAllPageDelayEvents(List<Object> pageDelayValues) {
        cancelAllDelayEvents(EventName.WE_WK_PAGE_DELAY, pageDelayValues);
    }


    protected void scheduleSessionDelayEvents(List<Object> sessionDelayValues) {
        scheduleDelayEvents(EventName.WE_WK_SESSION_DELAY, sessionDelayValues);
    }


    protected void cancelAllSessionDelayEvents(List<Object> sessionDelayValues) {
        cancelAllDelayEvents(EventName.WE_WK_SESSION_DELAY, sessionDelayValues);
    }

    // Implemented here instead on analytics impl because config is not available during first activity resume, so session delay cannot be scheduled
    private void scheduleDelayEvents(String eventName, List<Object> list) {
        if (list != null) {
            AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
            for (int i = 0; i < list.size(); i++) {
                Long delay = (Long) list.get(i);
                if (delay != null) {
                    PendingIntent pendingIntent = PendingIntentFactory.constructDelayPendingIntent(eventName, delay, applicationContext);
                    if (pendingIntent != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
                        } else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
                        }
                    }
                }

            }
        }

    }

    private void cancelAllDelayEvents(String eventName, List<Object> list) {
        if (list != null) {
            AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
            for (int i = 0; i < list.size(); i++) {
                Long delay = (Long) list.get(i);
                if (delay != null) {
                    PendingIntent pendingIntent = PendingIntentFactory.constructDelayPendingIntent(eventName, delay, applicationContext);
                    if (pendingIntent != null) {
                        alarmManager.cancel(pendingIntent);
                    }
                }
            }
        }
    }

    private int getJobId() {
        return (this.applicationContext.getPackageName() + WebEngage.get().getWebEngageConfig().getWebEngageKey()).hashCode();
    }

    @SuppressLint("MissingPermission")
    void scheduleAmplify(long delay) {
        final int jobId = getJobId();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                ComponentName componentName = new ComponentName(applicationContext, WebEngageJobService.class);
                JobInfo.Builder builder = new JobInfo.Builder(jobId, componentName);
                builder.setMinimumLatency(delay);
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
                builder.setRequiresCharging(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setRequiresBatteryNotLow(true);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    builder.setPrefetch(true);
                }

                if (ManifestUtils.checkPermission(ManifestUtils.RECEIVE_BOOT_COMPLETED, applicationContext)) {
                    builder.setPersisted(true);
                } else {
                    Logger.d(WebEngageConstant.TAG, "For WebEngage push amplification to work even after boot-up, add RECEIVE_BOOT_COMPLETED permission in the AndroidManifest.");
                }

                int result = jobScheduler.schedule(builder.build());
                if (BuildConfig.DEBUG) {
                    if (result == JobScheduler.RESULT_SUCCESS) {
                        Logger.d(WebEngageConstant.TAG, "Amplification job scheduled after " + (delay / WebEngageConstant.ONE_MINUTE) + " minute(s)");
                    } else {
                        Logger.d(WebEngageConstant.TAG, "Amplification job schedule failed");
                    }
                }
            }
        } else {
            AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                PendingIntent pendingIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    pendingIntent = PendingIntentFactory.constructPushAmplifyPendingIntent(applicationContext, jobId, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                }
                else {
                    pendingIntent = PendingIntentFactory.constructPushAmplifyPendingIntent(applicationContext, jobId, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, pendingIntent);
                if (BuildConfig.DEBUG) {
                    Logger.d(WebEngageConstant.TAG, "Amplification alarm set after " + (delay / WebEngageConstant.ONE_MINUTE) + " minute(s)");
                }
            }
        }
    }

    boolean isAmplifyScheduled() {
        final int jobId = getJobId();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) this.applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
                    if (jobInfo.getId() == jobId) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            PendingIntent pendingIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                pendingIntent = PendingIntentFactory.constructPushAmplifyPendingIntent(applicationContext, jobId, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
            }
            else{
                pendingIntent = PendingIntentFactory.constructPushAmplifyPendingIntent(applicationContext, jobId, PendingIntent.FLAG_NO_CREATE);
            }
            return pendingIntent != null;
        }
    }
}
