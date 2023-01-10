package com.webengage.personalization.data.network

import android.content.Context
import java.lang.ref.WeakReference

class RequestObject private constructor(builder: Builder) {
    var uRL: String
    var requestMethod: RequestMethod
    var headers: MutableMap<String, String>?
    var params: Any?
    var tag: String?
    var flags: Int
    var cachePolicy: Int
    private var context: Context?

    class Builder(val url: String, val method: RequestMethod, context: WeakReference<Context>) {
        var headers: MutableMap<String, String>? = null
        var params: Any? = null
        var tag: String? = null
        var context: Context? = null
        var flags = 0
        var cachePolicy = CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING

        fun setHeaders(headers: MutableMap<String, String>?): Builder {
            this.headers = headers
            return this
        }

        fun setParams(params: Any?): Builder {
            this.params = params
            return this
        }

        fun setTag(tag: String?): Builder {
            this.tag = tag
            return this
        }

        fun setFlags(flags: Int): Builder {
            this.flags = this.flags or flags
            return this
        }

        fun setCachePolicy(policy: Int): Builder {
            cachePolicy = policy
            return this
        }

        fun build(): RequestObject {
            return RequestObject(this)
        }

        init {
            this.context = context.get()!!
        }
    }

    val currentState: Builder
        get() = Builder(uRL, requestMethod, WeakReference(context))
            .setTag(tag)
            .setFlags(flags)
            .setCachePolicy(cachePolicy)
            .setHeaders(headers)
            .setParams(params)

    fun execute(): Response? {
        var flag = true
        synchronized(AbstractCacheHelper.interceptors) {
            for (interceptor in AbstractCacheHelper.interceptors) {
                flag = flag and interceptor.onRequest(this, context)
            }
        }
        var response: Response? = null
        if (flag) {
            val requestExecutor = RequestExecutor(context!!, this)
            response = requestExecutor.applyCachePolicy()
        }
        if (response == null) {
            response = Response.Builder().build()
        }
        return response
    }

    companion object {
        const val FLAG_PERSIST_AFTER_CONFIG_REFRESH = 1
    }

    init {
        uRL = builder.url
        requestMethod = builder.method
        headers = builder.headers
        params = builder.params
        tag = builder.tag
        flags = builder.flags
        cachePolicy = builder.cachePolicy
        context = builder.context
    }
}