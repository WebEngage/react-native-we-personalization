package com.webengage.sdk.android;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import com.webengage.sdk.android.actions.database.ReportingStatistics;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class WebEngageJobService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {
        ReportingStatistics.setRescheduleAmplify(true);
        WebEngage.get().dispatchAmplify(null);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return ReportingStatistics.getRescheduleAmplify();
    }
}
