package com.webengage.personalization.callbacks

import com.webengage.personalization.data.WECampaignData

interface WECampaignCallback {
    fun onCampaignPrepared(data: WECampaignData): WECampaignData?
    fun onCampaignClicked(actionId: String, deepLink: String, data: WECampaignData): Boolean
    fun onCampaignShown(data: WECampaignData)
    fun onCampaignException(campaignId: String?, targetViewId: String, error: Exception)
}