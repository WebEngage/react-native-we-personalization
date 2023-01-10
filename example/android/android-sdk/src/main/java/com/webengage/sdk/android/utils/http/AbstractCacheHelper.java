package com.webengage.sdk.android.utils.http;


import android.content.Context;

import com.webengage.sdk.android.utils.WebEngageConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractCacheHelper {
    protected Context context;
    protected RequestObject object;
    public static final List<Interceptor> interceptors = new ArrayList<Interceptor>();

    public AbstractCacheHelper(Context context, RequestObject object) {
        this.context = context.getApplicationContext();
        this.object = object;
    }

    public static void addInterceptor(Interceptor interceptor) {
        synchronized (interceptors) {
            interceptors.add(interceptor);
        }
    }

    /**
     * checks if file is present in cache and has its corresponding SQL entry
     *
     * @return true or false
     */
    abstract boolean isFilePresent();

    /**
     * Checks if file age is within the time limit
     *
     * @return true or false
     */
    abstract boolean isFileNotExpired();

    /**
     * Establishes an URL connection with server with appropriate headers(ETag,LastModified etc)
     * to check whether file has been modified or not
     *
     * @return HttpResponse with notmodified field set to true if response code is 304
     */

    abstract Response validateFile();

    /**
     * returns input stream from the cached file
     *
     * @return
     */
    abstract Response readFromFile(Response response);

    /**
     * Establishes URL connection for the given network object
     *
     * @return
     */
    abstract Response downloadFile();


    abstract Response getCachedResponse();


    abstract byte[] saveFile(Response response);


    protected void closeCachedResponse() {
        Response response = getCachedResponse();
        if(response != null) {
            response.closeErrorStream();
            response.closeInputStream();
        }
    }

    protected String getPathToDirectory() {
        File file = new File(context.getApplicationInfo().dataDir,
                WebEngageConstant.DIR_PATH);
        if (!file.exists()) {
            file.mkdirs();

        }
        return file.getPath();
    }

    protected Response applyCachePolicy() {
        Response response = null;
        try {
            switch (object.getCachePolicy()) {
                case CachePolicy.GET_DATA_FROM_NETWORK_FIRST_ELSE_FROM_CACHE:
                    response = downloadFile();
                    if (response.isReadable())
                        return response;
                    else
                        return readFromFile(response);
                case CachePolicy.GET_VALIDATED_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE:
                    if (isFilePresent()) {
                        if (isFileNotExpired())
                            return readFromFile(response);
                        else {
                            response = validateFile();
                            if (!response.isReadable()) {
                                return readFromFile(response);
                            } else {
                                closeCachedResponse();
                                return response;
                            }

                        }
                    } else {
                        return downloadFile();
                    }
                case CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING://
                    return downloadFile();
                case CachePolicy.GET_DATA_FROM_CACHE_ONLY:
                    return readFromFile(response);
                case CachePolicy.GET_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE:
                    if (isFilePresent())
                        return readFromFile(response);
                    else {
                        return downloadFile();
                    }

                case CachePolicy.GET_VALIDATED_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE_RETURNS_NULL_IF_OFFLINE_AND_DATA_EXPIRED:
                    if (isFilePresent()) {
                        if (isFileNotExpired())
                            return readFromFile(response);
                        else {
                            response = validateFile();
                            if (!response.isReadable()) {
                                if (response.getErrorStream() != null || response.getException() != null) {
                                    closeCachedResponse();
                                    return response;
                                } else {
                                    if (!response.modified()) {
                                        return readFromFile(response);
                                    } else {
                                        closeCachedResponse();
                                        return response;
                                    }
                                }
                            } else {
                                closeCachedResponse();
                                return response;
                            }

                        }
                    } else {
                        return downloadFile();
                    }

            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


}
