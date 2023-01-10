package com.webengage.sdk.android.actions.database;

import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SyncAction extends Action {
    private Context applicationContext = null;
    private String URL = null;
    private Object actionData = null;

    protected SyncAction(Context context) {
        super(context);
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public Object preExecute(Map<String, Object> actionAttributes) {
        actionData = actionAttributes.get(SyncActionController.ACTION_DATA);
        URL = (String) actionAttributes.get(SyncActionController.SERVER_URL);
        return generateDataPayload((ArrayList<EventPayload>) actionData);
    }

    @Override
    public Object execute(Object data) {
                Logger.d(WebEngageConstant.TAG, "Inside Event sync of RN");

        if (data != null) {
            if (ReportingStatistics.getShouldReport()) {
                if (BuildConfig.DEBUG) {
                    Logger.d(WebEngageConstant.TAG, "Events url: " + URL);
                }
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/transit+json");
                RequestObject requestObject = new RequestObject.Builder(URL, RequestMethod.POST, this.applicationContext)
                        .setParams(data)  // change data to params if end point is kafka
                        .setHeaders(headers)
                        .build();
                Response response = requestObject.execute();
                return response;
            } else {
                return new Response.Builder().build();
            }
        }
        return null;
    }

    @Override
    public void postExecute(Object data) {
        if (data != null) {
            Response response = (Response) data;
            ArrayList<EventPayload> unsyncedEventdata = (ArrayList<EventPayload>) actionData;
            if (response.getException() == null && response.getResponseCode() >= HttpURLConnection.HTTP_OK && response.getResponseCode() < HttpURLConnection.HTTP_INTERNAL_ERROR) {
                ReportingController.strategyFactory.getReportingStatistics().setLastReportStatus(true);
                ReportingController.strategyFactory.getReportingStatistics().resetNetworkReportFailureCount();
                Logger.d(WebEngageConstant.TAG, "Events successfully Logged to server, scheduling next sync");
                ArrayList<String> idsToRemove = new ArrayList<String>();
                if (unsyncedEventdata != null) {
                    for (EventPayload eventPayload : unsyncedEventdata) {
                        idsToRemove.add(Integer.toString(eventPayload.getId()));
                    }
                }
                EventDataManager.getInstance(this.applicationContext).removeEvents(idsToRemove);
            } else {
                if (ReportingStatistics.getShouldReport()) {
                    ReportingStatistics.setShouldReport(false);
                    ReportingController.strategyFactory.getReportingStatistics().setLastReportStatus(false);
                    ReportingController.strategyFactory.getReportingStatistics().incrementNetworkReportFailureCount();
                }
                List<String> failedIds = new ArrayList<String>();
                if (unsyncedEventdata != null) {
                    for (EventPayload eventPayload : unsyncedEventdata) {
                        failedIds.add(Integer.toString(eventPayload.getId()));
                    }
                }
                EventDataManager.getInstance(this.applicationContext).updateFailedEvents(failedIds);
                Logger.d(WebEngageConstant.TAG, "Event Logging failed, scheduling next sync");
                if (response.getException() != null) {
                    Logger.e(WebEngageConstant.TAG, "Event sync failed due to Exception: " + String.valueOf(response.getException()), response.getException());
                }
            }
            if (response.isReadable()) {
                response.closeInputStream();
            } else {
                response.closeErrorStream();
            }
        }
    }

    private String generateDataPayload(ArrayList<EventPayload> eventPayloadList) {
        if (eventPayloadList == null) {
            return null;
        }
        if (eventPayloadList.size() == 0) {
            return null;
        }

        try {
            return DataType.convert(eventPayloadList, DataType.STRING, true).toString();
        } catch (Exception e) {

        }
        return null;
    }
}
