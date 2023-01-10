package com.webengage.sdk.android.actions.database;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportingStatistics {
    private AtomicBoolean lastNetworkReportStatus = new AtomicBoolean(true);
    private AtomicInteger networkReportFailureCount = new AtomicInteger(0);
    private static AtomicBoolean shouldReport = new AtomicBoolean(true);
    private static AtomicBoolean mWantsReschedule = new AtomicBoolean(false);

    boolean getLastNetworkReportStatus() {
        return lastNetworkReportStatus.get();
    }

    void setLastReportStatus(boolean status) {
        lastNetworkReportStatus.set(status);
    }

    void incrementNetworkReportFailureCount() {
        this.networkReportFailureCount.incrementAndGet();
    }

    int getNetworkReportFailureCount() {
        return this.networkReportFailureCount.get();
    }

    void resetNetworkReportFailureCount() {
        this.networkReportFailureCount.set(0);
    }

    static boolean getShouldReport() {
        return shouldReport.get();
    }

    public static void setShouldReport(boolean b) {
        shouldReport.set(b);
    }

    public static void setRescheduleAmplify(boolean b) {
        mWantsReschedule.set(b);
    }

    public static boolean getRescheduleAmplify() {
        return mWantsReschedule.get();
    }
}

