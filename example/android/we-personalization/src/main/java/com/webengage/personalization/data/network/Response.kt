package com.webengage.personalization.data.network

import java.io.InputStream

class Response private constructor(builder: Builder) {
    val id: Int
    val exception: Exception?
    val responseHeaders: Map<String, List<String>>?
    private val modifiedState: Boolean
    val inputStream: InputStream?
    val errorStream: InputStream?
    val responseCode: Int
    val tag: String
    val flags: Int
    val uRL: String?
    val timeStamp: Long
    fun modified(): Boolean {
        return modifiedState
    }

    fun isReadable(): Boolean {
        return exception == null && inputStream != null && errorStream == null
    }


    fun closeInputStream() {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (e: Exception) {
            }
        }
    }

    fun closeErrorStream() {
        if (errorStream != null) {
            try {
                errorStream.close()
            } catch (e: Exception) {
            }
        }
    }

    val currentState: Builder
        get() = Builder()
            .setID(id)
            .setException(exception)
            .setResponseHeaders(responseHeaders)
            .setModifiedState(modifiedState)
            .setResponseCode(responseCode)
            .setInputStream(inputStream)
            .setErrorStream(errorStream)
            .setTag(tag)
            .setFlags(flags)
            .setCacheKey(uRL)
            .setTimeStamp(timeStamp)

    class Builder {
        var id = -1
        var exception: Exception? = null
        var responseHeaders: Map<String, List<String>>? = null
        var modifiedState = true
        var inputStream: InputStream? = null
        var errorStream: InputStream? = null
        var responseCode = -1
        var tag = ""
        var flags = 0
        var cacheKey: String? = null
        var timeStamp = 0L

        fun setID(id: Int): Builder {
            this.id = id
            return this
        }

        fun setException(exception: Exception?): Builder {
            this.exception = exception
            return this
        }

        fun setResponseHeaders(responseHeaders: Map<String, List<String>>?): Builder {
            this.responseHeaders = responseHeaders
            return this
        }

        fun setModifiedState(modifiedState: Boolean): Builder {
            this.modifiedState = modifiedState
            return this
        }

        fun setInputStream(inputStream: InputStream?): Builder {
            this.inputStream = inputStream
            return this
        }

        fun setErrorStream(errorStream: InputStream?): Builder {
            this.errorStream = errorStream
            return this
        }

        fun setResponseCode(responseCode: Int): Builder {
            this.responseCode = responseCode
            return this
        }

        fun setTag(tag: String?): Builder {
            this.tag = tag ?: ""
            return this
        }

        fun setFlags(flags: Int): Builder {
            this.flags = this.flags or flags
            return this
        }

        fun setCacheKey(cacheKey: String?): Builder {
            this.cacheKey = cacheKey
            return this
        }

        fun setTimeStamp(timeStamp: Long): Builder {
            this.timeStamp = timeStamp
            return this
        }

        fun build(): Response {
            return Response(this)
        }
    }

    init {
        exception = builder.exception
        responseHeaders = builder.responseHeaders
        modifiedState = builder.modifiedState
        inputStream = builder.inputStream
        errorStream = builder.errorStream
        responseCode = builder.responseCode
        tag = builder.tag
        flags = builder.flags
        uRL = builder.cacheKey
        timeStamp = builder.timeStamp
        id = builder.id
    }
}