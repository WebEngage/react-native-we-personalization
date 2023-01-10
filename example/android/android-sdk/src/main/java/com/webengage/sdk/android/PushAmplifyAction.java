package com.webengage.sdk.android;

import android.content.Context;
import android.os.Bundle;

import com.webengage.sdk.android.actions.database.ReportingStatistics;
import com.webengage.sdk.android.utils.NetworkUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;
import com.webengage.sdk.android.utils.http.CachePolicy;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushAmplifyAction extends Action {
    private Context applicationContext = null;

    protected PushAmplifyAction(Context context) {
        super(context);
        this.applicationContext = context;
    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        String licenseCode = WebEngage.get().getWebEngageConfig().getWebEngageKey();
        String luid = getLUID();
        String cuid = getCUID();
        String pushAmplifyUrl = WebEngageConstant.Urls.getAmplifyPushUrl(licenseCode, luid, cuid);

        if (BuildConfig.DEBUG) {
            Logger.d(WebEngageConstant.TAG, "Push amplification url: " + pushAmplifyUrl);
        }
        int cachePolicy = CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING;
        RequestObject requestObject = new RequestObject.Builder(pushAmplifyUrl, RequestMethod.GET, this.applicationContext)
                .setCachePolicy(cachePolicy)
                .build();

        return requestObject.execute();
    }

    @Override
    protected Object execute(Object data) {
        Response response = (Response) data;
        if (response.isReadable()) {
            try {
                if (BuildConfig.DEBUG) {
                    Logger.d(WebEngageConstant.TAG, "Push amplification response code: " + response.getResponseCode());
                }
                if (response.getResponseCode() == 200) {
                    JSONObject jsonResponse = new JSONObject(NetworkUtils.readEntireStream(response.getInputStream()));
                    if (BuildConfig.DEBUG) {
                        Logger.d(WebEngageConstant.TAG, "Push amplification response: " + jsonResponse);
                    }

                    String status = jsonResponse.optString("status", "");
                    if ("success".equals(status)) {
                        JSONObject dataJson = jsonResponse.optJSONObject("data");
                        if (dataJson != null && dataJson != JSONObject.NULL) {
                            long next = dataJson.optLong("next", WebEngageConstant.AMPLIFY_DEFAULT_INTERVAL_MINUTES);
                            saveAmplifyInterval(next * WebEngageConstant.ONE_MINUTE);

                            JSONArray pushPayloads = dataJson.optJSONArray("pushPayloads");
                            if (pushPayloads != null && pushPayloads.length() > 0) {
                                for (int i = 0; i < pushPayloads.length(); i++) {
                                    JSONObject jsonObject = pushPayloads.getJSONObject(i);
                                    JSONObject messageData = jsonObject.getJSONObject("message_data");

                                    if (!messageData.has(WebEngageConstant.AMPLIFIED)) {
                                        messageData.put(WebEngageConstant.AMPLIFIED, true);
                                    }

                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("source", jsonObject.getString("source"));
                                    map.put("message_action", jsonObject.getString("message_action"));
                                    map.put("message_data", String.valueOf(messageData));

                                    Bundle bundle = WebEngageUtils.mapToBundle(map);
                                    SubscriberManager.get(applicationContext).callSubscribers(Topic.GCM_MESSAGE, bundle);
                                }
                            }
                        }
                    }
                } else {
                    response.closeInputStream();
                }
            } catch (Exception e) {
                Logger.e(WebEngageConstant.TAG, "Exception while parsing push amplification data", e);
                dispatchExceptionTopic(e);
            } finally {
                try {
                    response.closeInputStream();
                } catch (Throwable t) {
                    Logger.e(WebEngageConstant.TAG, "Error while closing push-amp input stream", t);
                }
            }
        } else {
            response.closeErrorStream();
        }
        return null;
    }

    @Override
    protected void postExecute(Object data) {
    }
}
