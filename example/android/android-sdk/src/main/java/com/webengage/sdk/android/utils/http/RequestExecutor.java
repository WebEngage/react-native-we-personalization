package com.webengage.sdk.android.utils.http;


import android.content.Context;
import android.net.TrafficStats;
import android.os.Build;

import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

public class RequestExecutor extends AbstractCacheHelper {
    String encodedURL = null;
    Response cachedResponse = null;
    private Context applicationContext = null;

    RequestExecutor(Context context, RequestObject object) {
        super(context, object);
        this.encodedURL = hashUrl(object.getURL());
        this.applicationContext = context;
    }


    private String hashUrl(String url) {
        return url;
    }

    @Override
    protected boolean isFilePresent() {
        cachedResponse = HttpDataManager.getInstance(applicationContext).getCache(encodedURL);
        return cachedResponse.isReadable();
    }

    @Override
    Response getCachedResponse() {
        return this.cachedResponse;
    }

    @Override
    protected boolean isFileNotExpired() {
//        if (cachedResponse == null) {
//            cachedResponse = HttpDataManager.getInstance(applicationContext).getCache(encodedURL);
//        }
//        if (cachedResponse.getTimeStamp() == 0l || cachedResponse.getResponseHeaders() == null) {
//            return true;
//        }
//        Map<String, List<String>> headers = cachedResponse.getResponseHeaders();
//        String maxAge = getHeaderValue(headers, HEADER_KEYS.MAX_AGE);
//        if (maxAge == null) {
//            return true;
//        }
//        if (System.currentTimeMillis() < (cachedResponse.getTimeStamp() + Long.valueOf(maxAge))) {
//            return true;
//        }
        return false;

    }

    @Override
    protected Response validateFile() {
        if (cachedResponse == null) {
            cachedResponse = HttpDataManager.getInstance(this.applicationContext).getCache(encodedURL);
        }
        String eTag = getHeaderValue(cachedResponse.getResponseHeaders(), HEADER_KEYS.ETAG);
        String lastModified = getHeaderValue(cachedResponse.getResponseHeaders(), HEADER_KEYS.LAST_MODIFIED);
        if (eTag == null && lastModified == null) {
            return establishConnection();
        }
        Map<String, String> headers = object.getHeaders();
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        if (eTag != null) {
            headers.put("If-None-Match", eTag);
        } else {
            headers.put("If-Modified-Since", lastModified);
        }
        object = object.getCurrentState().setHeaders(headers).build();
        return establishConnection();
    }


    @Override
    protected Response readFromFile(Response response) {
        if (cachedResponse == null) {
            cachedResponse = HttpDataManager.getInstance(this.applicationContext).getCache(encodedURL);
        }
        if (response != null) {
            cachedResponse = cachedResponse.getCurrentState().setResponseCode(response.getResponseCode()).build();
        }
        return cachedResponse.getCurrentState().setTag(super.object.getTag()).setFlags(super.object.getFlags()).build();


    }

    @Override
    protected Response downloadFile() {
        return establishConnection();
    }


    @Override
    protected byte[] saveFile(Response response) {
        return HttpDataManager.getInstance(this.applicationContext).saveHttpData(response);
    }

    private Response establishConnection() {
        HttpURLConnection con = null;
        OutputStream outputStream = null;
        Response.Builder builder = new Response.Builder();
        Response response = null;
        builder.setTag(super.object.getTag());
        builder.setFlags(super.object.getFlags());

        // To avoid crash in strict mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TrafficStats.setThreadStatsTag(WebEngageConstant.THREAD_STATS_TAG);
        }

        builder.setCacheKey(this.encodedURL);
        try {
            con = (HttpURLConnection) new URL(super.object.getURL()).openConnection();
            con.setRequestMethod(super.object.getRequestMethod().toString());
            con.setConnectTimeout(WebEngageConstant.DEFAULT_CONNECT_TIMEOUT);
            con.setReadTimeout(WebEngageConstant.DEFAULT_READOUT_TIME);
            if (super.object.getHeaders() != null) {
                Map<String, String> headers = super.object.getHeaders();
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    con.setRequestProperty(entry.getKey(), entry.getValue());
                }
                con.setRequestProperty("Accept-Encoding", "gzip");
            }
            if (!RequestMethod.GET.toString().equalsIgnoreCase(super.object.getRequestMethod().toString())) {
                con.setDoOutput(true);
            }
            con.setDoInput(true);
            Object params = super.object.getParams();
            if (params != null) {
                if (super.object.getHeaders() != null && super.object.getHeaders().containsKey("Content-Encoding") && "gzip".equalsIgnoreCase(super.object.getHeaders().get("Content-Encoding"))) {
                    con.setRequestProperty("Content-Encoding", "gzip");
                    outputStream = con.getOutputStream();
                    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
                    Writer writer = new OutputStreamWriter(gzipOutputStream);
                    writeToOutputStream(writer, params);
                    gzipOutputStream.close();
                    outputStream.close();
                } else {
                    outputStream = con.getOutputStream();
                    Writer writer = new OutputStreamWriter(outputStream);
                    writeToOutputStream(writer, params);
                    outputStream.close();
                }

            }
            builder.setResponseCode(con.getResponseCode());
            if (con.getResponseCode() == HttpsURLConnection.HTTP_NOT_MODIFIED && RequestMethod.GET.equals(super.object.getRequestMethod())) {
                builder.setModifiedState(false);
            }
            Map<String, List<String>> serializableHeaders = new HashMap<String, List<String>>();
            Map<String, List<String>> headers = con.getHeaderFields();
            if (headers != null) {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    List<String> serializableList = new ArrayList<String>();
                    serializableList.addAll(entry.getValue());
                    if (entry.getKey() != null) {
                        serializableHeaders.put(entry.getKey().toLowerCase(), serializableList);
                    }
                }
            }
            builder.setResponseHeaders(serializableHeaders);
            response = builder.build();
            if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = null;
                if (WebEngageUtils.CheckGZIP(con)) {
                    is = new GZIPInputStream(con.getInputStream());

                } else {
                    is = con.getInputStream();
                }

                response = response.getCurrentState().setInputStream(is).build();
                synchronized (interceptors) {
                    for (Interceptor interceptor : interceptors) {
                        response = interceptor.onResponse(response, applicationContext);
                    }
                }
                response = response.getCurrentState().setTimeStamp(System.currentTimeMillis()).build();
                if (super.object.getCachePolicy() != (CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING) && response.isReadable()) {
                    byte[] data = saveFile(response);
                    return response.getCurrentState().setInputStream(new ByteArrayInputStream(data)).build();
                }

            } else {
                if (response.getResponseCode() >= 400) {
                    try {
                        response = response.getCurrentState().setErrorStream(con.getErrorStream()).build();
                    } catch (Exception e) {

                    }
                }
            }

            return response;

        } catch (Exception e) {
            builder.setException(e);
            return builder.build();
        }
    }


    private void writeToOutputStream(Writer writer, Object params) throws Exception {
        if (params instanceof Map<?, ?>) {
            writer.write(WebEngageUtils.getParams((Map<String, String>) params));
        } else {
            writer.write(params.toString());
        }
        writer.close();

    }


    private enum HEADER_KEYS {
        MAX_AGE, LAST_MODIFIED, ETAG, EXPIRES, CACHE_CONTROL
    }

    private String getHeaderValue(Map<String, List<String>> HEADERS,
                                  HEADER_KEYS keys) {
        switch (keys) {
            case MAX_AGE:
                if (HEADERS.get("cache-control") != null) {
                    String value = HEADERS.get("cache-control").get(0);
                    if (value.contains("max-age")) {
                        int i = value.indexOf(",");
                        return value.substring(value.indexOf("max-age") + 8, i == -1 ? value.length() : i);
                    }
                }
                break;
            case LAST_MODIFIED:
                if (HEADERS.get("last-modified") != null)
                    return HEADERS.get("last-modified").get(0);
                break;
            case ETAG:
                if (HEADERS.get("etag") != null)
                    return HEADERS.get("etag").get(0);
                break;
            case EXPIRES:
                if (HEADERS.get("expires") != null)
                    return HEADERS.get("expires").get(0);
                break;
            case CACHE_CONTROL:
                if (HEADERS.get("cache-control") != null)
                    return HEADERS.get("cache-control").get(0);
                break;

        }
        return null;

    }


}
