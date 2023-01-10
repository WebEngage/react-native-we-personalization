package com.webengage.sdk.android;

import android.content.Context;
import android.graphics.Bitmap;

import com.webengage.sdk.android.actions.exception.ImageLoadException;
import com.webengage.sdk.android.utils.NetworkUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;
import com.webengage.sdk.android.utils.http.Interceptor;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import java.io.ByteArrayInputStream;

class NetworkInterceptor implements Interceptor {
    @Override
    public boolean onRequest(RequestObject requestObject, Context context) {
        return true;
    }

    @Override
    public Response onResponse(Response response, Context context) {
        if (response.getTag() != null) {
            Bitmap bitmap = null;
            try {
                if (WebEngageConstant.LANDSCAPE.equalsIgnoreCase(response.getTag())) {
                    bitmap = NetworkUtils.loadBitmapByHeight(response, 192f, context.getApplicationContext());
                    return response.getCurrentState().setInputStream(new ByteArrayInputStream(WebEngageUtils.serialize(bitmap))).build();
                } else if (WebEngageConstant.PORTRAIT.equalsIgnoreCase(response.getTag())) {
                    bitmap = NetworkUtils.loadBitmap(response, 192f, 192f, context.getApplicationContext());
                    return response.getCurrentState().setInputStream(new ByteArrayInputStream(WebEngageUtils.serialize(bitmap))).build();
                }
            } catch (Exception e) {
                return response.getCurrentState().setException(new ImageLoadException(e.getMessage())).setInputStream(null).build();
            } catch (OutOfMemoryError error) {
                return response.getCurrentState().setException(new ImageLoadException("OutOfMemoryError")).setInputStream(null).build();
            }
        }

        return response;
    }
}
