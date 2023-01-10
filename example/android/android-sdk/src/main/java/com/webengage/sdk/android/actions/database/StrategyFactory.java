package com.webengage.sdk.android.actions.database;


import android.content.Context;

import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.Map;

class StrategyFactory {
    ReportingStatistics reportingStatistics;
    private int networkFailureCountThreshold = 5;
    private Context applicationContext = null;
    private BufferStrategy bufferStrategy = null;
    private NetworkStrategy networkStrategy = null;
    private FlushStrategy flushStrategy = null;

    StrategyFactory(ReportingStatistics reportingStatistics, Context applicationContext) {
        this.reportingStatistics = reportingStatistics;
        this.applicationContext = applicationContext;
    }

    private BufferStrategy getBufferStrategy() {
        if (bufferStrategy == null) {
            bufferStrategy = new BufferStrategy(applicationContext);
        }
        return bufferStrategy;
    }

    private NetworkStrategy getNetworkStrategy() {
        if (networkStrategy == null) {
            networkStrategy = new NetworkStrategy(applicationContext);
        }
        return networkStrategy;
    }

    public FlushStrategy getFlushStrategy() {
        if (flushStrategy == null) {
            flushStrategy = new FlushStrategy(applicationContext, getNetworkStrategy());
        }
        return flushStrategy;
    }

    private int getPriority(EventPayload eventPayload) {
        if (eventPayload == null) {
            return WebEngageConstant.PRIORITY_LOW;
        }
        String category = eventPayload.getCategory();
        if (WebEngageConstant.APPLICATION.equals(category)) {
            if (eventPayload.getExtraData() != null) {
                Map<String, Object> map = (Map<String, Object>) eventPayload.getExtraData();
                if ((Boolean) map.get(WebEngageConstant.HIGH_REPORTING_PRIORITY)) {
                    return WebEngageConstant.PRIORITY_HIGH;
                } else {
                    return WebEngageConstant.PRIORITY_LOW;
                }
            } else {
                return WebEngageConstant.PRIORITY_LOW;
            }

        } else {
            Integer eventPriority = WebEngageConstant.priorityMap.get(eventPayload.getEventName());
            return (eventPriority == null ? WebEngageConstant.PRIORITY_LOW : eventPriority);
        }
    }

    private Strategy getStrategy(int priority) {
        if (!this.reportingStatistics.getLastNetworkReportStatus() && this.reportingStatistics.getNetworkReportFailureCount() > this.networkFailureCountThreshold) {
            return getBufferStrategy();
        }
        if (priority >= WebEngageConstant.PRIORITY_HIGH || getCurrentReportingStrategy() == ReportingStrategy.FORCE_SYNC) {
            return new BufferFlushStrategy(getBufferStrategy(), getFlushStrategy());
        } else {
            return new BufferNetworkStrategy(getBufferStrategy(), getNetworkStrategy(), applicationContext);
        }
    }

    public Strategy getStrategy(EventPayload eventPayload) {
        return getStrategy(getPriority(eventPayload));
    }

    public Strategy getStrategy() {
        return getStrategy(WebEngageConstant.PRIORITY_LOW);
    }

    public ReportingStatistics getReportingStatistics() {
        return this.reportingStatistics;
    }

    private ReportingStrategy getCurrentReportingStrategy() {
        return WebEngage.get().getWebEngageConfig().getEventReportingStrategy();
    }

}
