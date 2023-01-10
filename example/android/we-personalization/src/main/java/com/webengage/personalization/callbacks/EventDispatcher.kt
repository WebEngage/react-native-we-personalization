package com.webengage.personalization.callbacks

import com.webengage.sdk.android.WebEngage
import com.webengage.personalization.utils.EventName
import com.webengage.personalization.utils.JsonKeys
import com.webengage.personalization.utils.TAG
import com.webengage.sdk.android.Logger

internal object EventDispatcher {
    internal fun trackEvent(
        eventName: String,
        pId: String,
        variationId: String,
        campaignId: String,
        attributes: Map<String, Any>? = null,
        actionId: String? = null,
        ) {
        val systemAttr = mutableMapOf<String, Any>()
        systemAttr["p_id"] = pId
        systemAttr["id"] = variationId
        systemAttr["experiment_id"] = campaignId
        actionId?.let { systemAttr["actionId"] = actionId }
        val analytics = WebEngage.get().analytics()
        analytics.trackSystem(eventName, systemAttr, attributes)
    }

    fun trackExceptionOccurred(campaignId: String?, variationId: String?, pId: String,
                               targetViewId: String,journeyId:String, error: String) {
        val systemAttr = mutableMapOf<String, Any>()
        val eventAttr = mutableMapOf<String, Any>()
        Logger.d(TAG, "trackExceptionOccurred with pId $pId campaignId $campaignId variationId $variationId targetViewId $targetViewId $error")
        systemAttr["experiment_id"] = campaignId ?: ""
        systemAttr["p_id"] = pId
        systemAttr["targetView"] = targetViewId
        systemAttr["id"] = variationId ?: ""
        systemAttr["journeyId"] = journeyId ?: ""
        eventAttr[JsonKeys.REASON] = error
        val analytics = WebEngage.get().analytics()
        analytics.trackSystem(EventName.INLINE_PERSONALIZATION_FAILED, systemAttr, eventAttr)
    }
}