package com.webengage.personalization.callbacks

import com.webengage.personalization.data.WECampaignData
import java.lang.Exception


interface WEPlaceholderCallback {

    fun onDataReceived(data: WECampaignData)

    fun onPlaceholderException(
        campaignId: String? = null,
        targetViewId: String,
        error: Exception
    )

    fun onRendered(data: WECampaignData)
}