package com.webengage.sdk.android.actions.database;

import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.rules.ConfigurationManager;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.NetworkUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.http.CachePolicy;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import java.util.Map;

public class JourneyContextAction extends Action {
    Context applicationContext = null;
    String userIdentifier = null;
    ConfigurationManager configurationManager = null;

    JourneyContextAction(Context context) {
        super(context);
        this.applicationContext = context;
    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        return DataHolder.get().getUpfc() != null && DataHolder.get().isAppForeground();
    }

    @Override
    protected Object execute(Object data) {
        if ((Boolean) data) {
            String upfc = null;

                try {
                    upfc = (String) DataType.convert(DataHolder.get().getUpfc(), DataType.STRING, false);
                } catch (Exception e) {
                    dispatchExceptionTopic(e);
                }

                String url = WebEngageConstant.Urls.getJounreyContextEndPoint(getLUID(), getCUID(), WebEngage.get().getWebEngageConfig().getWebEngageKey(), upfc);
                if (BuildConfig.DEBUG) {
                    Logger.d(WebEngageConstant.TAG, "jcx url: " + url);
                }
                long start = System.currentTimeMillis();
                RequestObject requestObject = new RequestObject.Builder(url, RequestMethod.GET, this.applicationContext)
                        .setCachePolicy(CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING)
                        .build();
                Response response = requestObject.execute();

            if (response != null && response.isReadable()) {
                    userIdentifier = getCUID().isEmpty() ? getLUID() : getCUID();
                    Map<String, Object> journeyData = null;
                    try {
                        journeyData = NetworkUtils.getAsMap(response.getInputStream(), true);
                    } catch (Exception e) {
                        dispatchExceptionTopic(e);
                    }

                    if (journeyData != null) {
                        DataHolder.get().silentSetData(DataContainer.JOURNEY.toString(), journeyData.get("journey"));
                    }
                } else {
                    if (response != null) {
                        response.closeErrorStream();
                    }
                }

        }
        return null;
    }

    @Override
    protected void postExecute(Object data) {

    }
}
