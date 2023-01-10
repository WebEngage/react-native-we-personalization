package com.webengage.sdk.android.actions.exception;


import android.content.Context;
import android.util.Log;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ExceptionAction extends Action {
    private Context applicationContext = null;

    ExceptionAction(Context context) {
        super(context);
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        return actionAttributes;
    }

    @Override
    protected Object execute(Object data) {
        try {
            Map<String, Object> actionAttributes = (Map<String, Object>) data;
            Exception exception = (Exception) actionAttributes.get(ExceptionController.ACTION_DATA);
            Map<String, String> params = new HashMap<String, String>();
            params.put("sdk_id", Integer.toString(BuildConfig.SDK_ID));
            params.put("luid", getLUID());
            if (!getCUID().isEmpty()) {
                params.put("cuid", getCUID());
            }
            params.put("source", "webengage");
            params.put("event", URLEncoder.encode(exception.getClass().getSimpleName(), "UTF-8"));
            params.put("type", "exception");
            params.put("category", WebEngage.get().getWebEngageConfig().getWebEngageKey());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("version", WebEngage.get().getWebEngageConfig().getWebEngageVersion());
            jsonObject.put("text", Log.getStackTraceString(exception));
            params.put("data", URLEncoder.encode(jsonObject.toString(), "UTF-8"));
            Map<String, String> headers = new HashMap<String, String>();
            RequestObject requestObject = new RequestObject.Builder(WebEngageConstant.Urls.EXCEPTION_END_POINT.toString() + "/?" + WebEngageUtils.getParams(params), RequestMethod.GET, this.applicationContext)
                    .setHeaders(headers)
                    .build();
            Response response = requestObject.execute();
            Logger.e(WebEngageConstant.TAG, "Exception Logged: " + Log.getStackTraceString(exception));
            if (response != null) {
                if (response.isReadable()) {
                    response.closeInputStream();
                } else {
                    response.closeErrorStream();
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void postExecute(Object data) {

    }


}
