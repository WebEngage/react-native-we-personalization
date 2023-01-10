package com.webengage.personalization.data.network

import android.content.Context

import java.util.*

abstract class AbstractCacheHelper(
    var context: Context,
    var requestObject: RequestObject
) {

    /**
     * checks if file is present in cache and has its corresponding SQL entry
     *
     * @return true or false
     */
    abstract fun isFilePresent(): Boolean

    /**
     * Checks if file age is within the time limit
     *
     * @return true or false
     */
    abstract fun isFileNotExpired(): Boolean

    /**
     * Establishes an URL connection with server with appropriate headers(ETag,LastModified etc)
     * to check whether file has been modified or not
     *
     * @return HttpResponse with notmodified field set to true if response code is 304
     */
    abstract fun validateFile(): Response?

    /**
     * returns input stream from the cached file
     *
     * @return
     */
    abstract fun readFromFile(response: Response?): Response?

    /**
     * Establishes URL connection for the given network object
     *
     * @return
     */
    abstract fun downloadFile(): Response?
    abstract fun getCachedResponse(): Response?

    abstract fun saveFile(response: Response): ByteArray?

    private fun closeCachedResponse() {
        val response = getCachedResponse()
        if (response != null) {
            response.closeErrorStream()
            response.closeInputStream()
        }
    }

    fun applyCachePolicy(): Response? {
        var response: Response? = null
        try {
            when (requestObject.cachePolicy) {
                CachePolicy.GET_DATA_FROM_NETWORK_FIRST_ELSE_FROM_CACHE -> {
                    response = downloadFile()
                    return if (response!!.isReadable()) response else readFromFile(response)
                }
                CachePolicy.GET_VALIDATED_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE -> {
                    return if (isFilePresent()) {
                        if (isFileNotExpired()) readFromFile(response) else {
                            response = validateFile()
                            if (!response!!.isReadable()) {
                                readFromFile(response)
                            } else {
                                closeCachedResponse()
                                response
                            }
                        }
                    } else {
                        downloadFile()
                    }
                }
                CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING -> {
                    return downloadFile()
                }
                CachePolicy.GET_DATA_FROM_CACHE_ONLY -> {
                    return readFromFile(response)
                }
                CachePolicy.GET_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE -> {
                    return if (isFilePresent()) readFromFile(
                        response
                    ) else {
                        downloadFile()
                    }
                }
                CachePolicy.GET_VALIDATED_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE_RETURNS_NULL_IF_OFFLINE_AND_DATA_EXPIRED -> {
                    return if (isFilePresent()) {
                        if (isFileNotExpired()) readFromFile(response) else {
                            response = validateFile()
                            if (!response!!.isReadable()) {
                                if (response.errorStream != null || response.exception != null) {
                                    closeCachedResponse()
                                    response
                                } else {
                                    if (!response.modified()) {
                                        readFromFile(response)
                                    } else {
                                        closeCachedResponse()
                                        response
                                    }
                                }
                            } else {
                                closeCachedResponse()
                                response
                            }
                        }
                    } else {
                        downloadFile()
                    }
                }
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    companion object {
        @JvmField
        val interceptors: MutableList<Interceptor> = ArrayList()
        fun addInterceptor(interceptor: Interceptor) {
            synchronized(interceptors) { interceptors.add(interceptor) }
        }
    }

}