package com.webengage.personalization.data

import com.webengage.personalization.callbacks.EventDispatcher
import com.webengage.personalization.utils.EventName
import com.webengage.personalization.utils.InlineLayouts

data class WECampaignData(
    internal val parserType: String?,
    val targetViewId: String,
    var content: WECampaignContent?,
    var campaignId: String = "",
    internal var propertyId: String? = null,
    internal var variationId: String? = null,
    internal var shouldRender: Boolean = true,
    internal var isDataOption: Boolean = false,
) {

    //1. Enable default animation
    //2. Disable default animation with no custom animation
    //3. Enable custom animation.

    init {
        content?.let {
            it.layoutType?.let { layoutType ->
                if (layoutType == InlineLayouts.DATA_OPTION) {
                    isDataOption = true
                }
            }
        }
    }

    fun trackImpression(attributes: Map<String, Any>? = null) {
        if (isDataOption) {
            attributes?.let { content?.customData?.putAll(attributes)}
            EventDispatcher.trackEvent(
                EventName.INLINE_PERSONALIZATION_VIEW,
                propertyId!!,
                variationId!!,
                campaignId,
                content?.customData,
            )
        }
    }

    fun trackClick(attributes: Map<String, Any>? = null) {
        if (isDataOption) {
            attributes?.let { content?.customData?.putAll(attributes)}
            EventDispatcher.trackEvent(
                EventName.INLINE_PERSONALIZATION_CLICK,
                propertyId!!,
                variationId!!,
                campaignId,
                content?.customData)
        }
    }

    fun stopRendering() {
        shouldRender = false
    }
}