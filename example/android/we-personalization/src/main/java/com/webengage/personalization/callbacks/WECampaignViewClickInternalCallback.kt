package com.webengage.personalization.callbacks


internal interface WECampaignViewClickInternalCallback {
    fun handleCampaignViewClicked(campaignId: String, deepLink: String, actionId: String)
}