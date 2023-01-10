package com.webengage.personalization.data.network

import android.content.Context
import com.webengage.personalization.utils.InlineExceptionType
import com.webengage.personalization.utils.WE_APP_PERSONALIZATION
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.utils.WebEngageUtils
import java.io.UnsupportedEncodingException
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.net.URLEncoder
import kotlin.system.measureTimeMillis


object NetworkProcessor {

    fun getCampaignData(
        context: WeakReference<Context>,
        variationId: String,
        lUID: String,
        cUID: String,
        url: String,
        params: String?
    ): String? {
        //Logger.d(TAG, "NetworkProcessor getCampaignData variationId - $variationId campaignId: $campaignId ")
        var result: String? = null
        val time = measureTimeMillis {
            val postHeaders = mutableMapOf<String, String>()
            postHeaders["Content-Type"] = "application/json"
            postHeaders["Content-Encoding"] = "gzip"
           val requestObject: RequestObject = RequestObject.Builder(
                getURL(url, lUID, variationId, cUID), RequestMethod.POST,context)
                .setCachePolicy(CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING)
                .setParams(params)
                .setHeaders(postHeaders)
                .build()
            val response: Response? = requestObject.execute()
            result = if (response != null && response.responseCode == 200 && response.isReadable()) {
                WebEngageUtils.readEntireStream(response.inputStream)
            } else {
                response?.closeErrorStream()
                InlineExceptionType.CAMPAIGN_FETCHING_FAILED.name
            }
        }
//        Logger.d(TAG, "Network call took: $time for campaignId: $campaignId")
        return result
    }

    private fun getURL(baseURL: String, luid: String, variationId: String, cuid: String): String {
        val sb = StringBuilder()
        sb.append(baseURL)
        sb.append("/users/")
        sb.append(WebEngage.get().webEngageConfig.webEngageKey)
        sb.append("/")
        sb.append(luid)
        sb.append("/templates/")
        sb.append(WE_APP_PERSONALIZATION)
        sb.append(variationId)
        if (cuid != null && cuid.isNotEmpty()) {
            try {
                val encodedCUID = URLEncoder.encode(cuid, "UTF-8")
                sb.append("?cuid=")
                sb.append(encodedCUID)
            } catch (e: UnsupportedEncodingException) {
            }
        }
        return sb.toString()
    }

}