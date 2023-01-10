package com.webengage.personalization

import androidx.annotation.Keep
import com.webengage.personalization.callbacks.WEPlaceholderCallback
import com.webengage.personalization.callbacks.WECampaignCallback

@Keep interface AbstractInLinePersonalization {

    fun init()

    fun registerWECampaignCallback(callback: WECampaignCallback)

    fun unregisterWECampaignCallback(callback: WECampaignCallback)

    fun registerWEPlaceholderCallback(parentID: String, listener: WEPlaceholderCallback)

    fun unregisterWEPlaceholderCallback(parentID: String)

}