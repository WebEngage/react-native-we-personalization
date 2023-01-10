package com.webengage.personalization.data.network

import android.content.Context

interface Interceptor {
    fun onRequest(requestObject: RequestObject?, context: Context?): Boolean
    fun onResponse(response: Response?, context: Context?): Response?
}