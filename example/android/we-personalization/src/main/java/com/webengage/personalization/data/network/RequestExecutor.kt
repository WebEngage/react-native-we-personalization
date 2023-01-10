package com.webengage.personalization.data.network

import android.content.Context
import android.net.TrafficStats
import com.webengage.sdk.android.Logger
import com.webengage.personalization.data.database.ImageCacheManager.Companion.getInstance
import com.webengage.personalization.utils.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.net.ssl.HttpsURLConnection

class RequestExecutor(context: Context, requestObject: RequestObject):
    AbstractCacheHelper(context, requestObject) {
    var encodedURL: String
    var _cachedResponse: Response? = null

    private enum class HEADER_KEYS {
        MAX_AGE, LAST_MODIFIED, ETAG, EXPIRES, CACHE_CONTROL
    }

    init {
        encodedURL = hashUrl(requestObject.uRL)
    }

    private fun hashUrl(url: String): String {
        return url
    }

    override fun isFilePresent(): Boolean {
        try {
            val response = getInstance(context).getCache(encodedURL)
            _cachedResponse = response
            return response!!.isReadable()
        } catch (e: java.lang.Exception) {
            return false
        }
    }

    override fun isFileNotExpired(): Boolean {
        return false
    }

    override fun validateFile(): Response? {
        if (_cachedResponse == null) {
            _cachedResponse = getInstance(context).getCache(encodedURL)
        }
        val eTag: String? =
            getHeaderValue(_cachedResponse!!.responseHeaders!!, HEADER_KEYS.ETAG)
        val lastModified: String? =
            getHeaderValue(_cachedResponse!!.responseHeaders!!, HEADER_KEYS.LAST_MODIFIED)

        if (eTag == null && lastModified == null) {
            return establishConnection()
        }
        var headers: MutableMap<String, String>? = requestObject.headers
        if (headers == null) {
            headers = HashMap()
        }
        if (eTag != null) {
            headers["If-None-Match"] = eTag
        } else {
            headers["If-Modified-Since"] = lastModified!!
        }
        requestObject = requestObject.currentState.setHeaders(headers).build()
        return establishConnection()
    }

    private fun getHeaderValue(
        HEADERS: Map<String, List<String>?>,
        keys: HEADER_KEYS
    ): String? {
        when (keys) {
            HEADER_KEYS.MAX_AGE -> if (HEADERS["cache-control"] != null) {
                val value = HEADERS["cache-control"]!![0]
                if (value.contains("max-age")) {
                    val i = value.indexOf(",")
                    return value.substring(
                        value.indexOf("max-age") + 8,
                        if (i == -1) value.length else i
                    )
                }
            }
            HEADER_KEYS.LAST_MODIFIED -> if (HEADERS["last-modified"] != null) return HEADERS["last-modified"]!![0]
            HEADER_KEYS.ETAG -> if (HEADERS["etag"] != null) return HEADERS["etag"]!![0]
            HEADER_KEYS.EXPIRES -> if (HEADERS["expires"] != null) return HEADERS["expires"]!![0]
            HEADER_KEYS.CACHE_CONTROL -> if (HEADERS["cache-control"] != null) return HEADERS["cache-control"]!![0]
        }
        return null
    }

    override fun readFromFile(response: Response?): Response? {
        if (_cachedResponse == null) {
            _cachedResponse = getInstance(context).getCache(encodedURL)
        }
        if (response != null) {
            _cachedResponse = _cachedResponse!!.currentState.setResponseCode(response.responseCode).build()
        }
        return _cachedResponse!!.currentState.setTag(super.requestObject.tag)
            .setFlags(super.requestObject.flags).build()
    }

    override fun downloadFile(): Response? {
        return establishConnection()
    }

    override fun getCachedResponse(): Response? {
        return _cachedResponse
    }


    override fun saveFile(response: Response): ByteArray? {
        return getInstance(context).saveImageData(response)
    }

    private fun establishConnection(): Response? {
        Logger.d(TAG, "Establishing connection....")
        var con: HttpURLConnection?
        var outputStream: OutputStream?
        val builder = Response.Builder()
        var response: Response?
        super.requestObject.tag?.let { builder.setTag(it) }
        builder.setFlags(super.requestObject.flags)

        // To avoid crash in strict mode
        TrafficStats.setThreadStatsTag(THREAD_STATS_TAG)
        builder.setCacheKey(encodedURL)
        return try {
            con = URL(super.requestObject.uRL).openConnection() as HttpURLConnection
            con.requestMethod = super.requestObject.requestMethod.toString()
            con.connectTimeout = Request.DEFAULT_CONNECT_TIMEOUT
            con.readTimeout = Request.DEFAULT_READOUT_TIME
            super.requestObject.headers?.let {
                for ((key, value) in it) {
                    con.setRequestProperty(key, value)
                }
                con.setRequestProperty("Accept-Encoding", "gzip")
            }

            if (!RequestMethod.GET.toString()
                    .equals(super.requestObject.requestMethod.toString(), ignoreCase = true)
            ) {
                con.doOutput = true
            }
            con.doInput = true
            val params: Any? = super.requestObject.params
            if (params != null) {
                if (super.requestObject.headers != null
                    && super.requestObject.headers!!.containsKey("Content-Encoding") && "gzip".equals(
                        super.requestObject.headers!!.get("Content-Encoding"), ignoreCase = true
                    )
                ) {
                    con.setRequestProperty("Content-Encoding", "gzip")
                    outputStream = con.outputStream
                    val gzipOutputStream = GZIPOutputStream(outputStream)
                    val writer: Writer = OutputStreamWriter(gzipOutputStream)
                    writeToOutputStream(writer, params)
                    gzipOutputStream.close()
                    outputStream.close()
                } else {
                    outputStream = con.outputStream
                    val writer: Writer = OutputStreamWriter(outputStream)
                    writeToOutputStream(writer, params)
                    outputStream.close()
                }
            }
            builder.setResponseCode(con.responseCode)
            if (con.responseCode == HttpsURLConnection.HTTP_NOT_MODIFIED && RequestMethod.GET == super.requestObject.requestMethod) {
                builder.setModifiedState(false)
            }
            val serializableHeaders: MutableMap<String, List<String>> = HashMap()
            val headers = con.headerFields
            if (headers != null) {
                for ((key, value) in headers) {
                    val serializableList: MutableList<String> = ArrayList()
                    serializableList.addAll(value)
                    if (key != null) {
                        serializableHeaders[key.toLowerCase()] = serializableList
                    }
                }
            }
            builder.setResponseHeaders(serializableHeaders)
            response = builder.build()
            if (response.responseCode == HttpURLConnection.HTTP_OK) {
                var inputStream: InputStream? = null
                inputStream = if (WEUtils.checkGZIP(con)) {
                    GZIPInputStream(con.inputStream)
                } else {
                    con.inputStream
                }
                response = response.currentState.setInputStream(inputStream).build()
                synchronized(interceptors) {
                    for (interceptor in interceptors) {
                        response = interceptor.onResponse(response, context)
                    }
                }
                response = response!!.currentState.setTimeStamp(System.currentTimeMillis()).build()
                if (super.requestObject.cachePolicy != CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING && response!!.isReadable()) {
                    val data = saveFile(response!!)
                    return response!!.currentState.setInputStream(ByteArrayInputStream(data))
                        .build()
                }
            } else {
                if (response!!.responseCode >= 400) {
                    try {
                        response =
                            response!!.currentState.setErrorStream(con.errorStream).build()
                    } catch (e: Exception) {
                    }
                }
            }
            response
        } catch (e: Exception) {
            builder.setException(e)
            builder.build()
        }
    }

    @Throws(java.lang.Exception::class)
    private fun writeToOutputStream(writer: Writer, params: Any) {
        if (params is Map<*, *>) {
            writer.write(WEUtils.getParams(params as Map<String?, String?>))
        } else {
            writer.write(params.toString())
        }
        writer.close()
    }

}