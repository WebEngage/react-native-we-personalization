package com.webengage.personalization

import android.app.Activity
import androidx.annotation.Keep
import com.webengage.sdk.android.InLinePersonalizationListener
import com.webengage.sdk.android.Logger
import com.webengage.sdk.android.WebEngage
import com.webengage.personalization.callbacks.*
import com.webengage.personalization.processor.PropertyProcessor
import com.webengage.personalization.utils.TAG
import java.lang.ref.WeakReference
import java.util.HashMap

@Keep
class WEPersonalization private constructor() : InLinePersonalizationListener,
    AbstractInLinePersonalization {

    @Keep companion object {
        @Volatile
        private var INSTANCE: AbstractInLinePersonalization? = null

        fun get(): AbstractInLinePersonalization {
            INSTANCE ?: synchronized(this) {
                INSTANCE = WEPersonalization()
            }
            return INSTANCE!!
        }
    }

    override fun init() {
        Logger.d(TAG, "Inline Personalization initialised")
        WebEngage.setInlinePersonalizationListener(this)
    }

    override fun registerWECampaignCallback(callback: WECampaignCallback) {
        WECallbackDispatcher.registerWEInlineCampaignCallback(callback)
    }

    override fun unregisterWECampaignCallback(callback: WECampaignCallback) {
        WECallbackDispatcher.unregisterWEInlineCampaignCallback(callback)
    }

    override fun registerWEPlaceholderCallback(parentID: String, listener: WEPlaceholderCallback) {
        WEPlaceholderSubscriber.subscribe(parentID, listener)
    }

    override fun unregisterWEPlaceholderCallback(parentID: String) {
        WEPlaceholderSubscriber.unsubscribe(parentID)
    }

    override fun propertiesReceived(
        activityWeakReference: WeakReference<Activity>,
        personalizationProperties: HashMap<String, Any>
    ) {
        val allProperties = personalizationProperties["properties"] as List<HashMap<String, Any>>
        val systemData = personalizationProperties["systemData"] as HashMap<String, Any>
        for (property in allProperties) {
                PropertyProcessor(activityWeakReference, property,systemData)
        }
    }

}