package com.webengage.personalization.callbacks

import android.util.Log
import com.webengage.personalization.data.WECampaignData
import com.webengage.personalization.utils.EventName
import com.webengage.personalization.utils.TAG
import com.webengage.sdk.android.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.ref.WeakReference

internal object WECallbackDispatcher : WEPlaceholderCallback {
    private val registeredWEPersonalizationCallback = mutableListOf<WECampaignCallback>()
    private val campaignViewClickCallbackInternalMap: MutableMap<String, WeakReference<WECampaignViewClickInternalCallback>> =
        mutableMapOf()

    fun registerWEInlineCampaignCallback(WECampaignCallback: WECampaignCallback) {
        registeredWEPersonalizationCallback.add(WECampaignCallback)
    }

    fun unregisterWEInlineCampaignCallback(WECampaignCallback: WECampaignCallback) {
        registeredWEPersonalizationCallback.remove(WECampaignCallback)
    }

    internal fun registerCampaignViewClickInternalCallback(
        id: String,
        listener: WECampaignViewClickInternalCallback
    ) {
        campaignViewClickCallbackInternalMap[id] = WeakReference(listener)
    }

    internal fun unregisterCampaignViewClickInternalCallback(id: String) {
        campaignViewClickCallbackInternalMap.remove(id)
    }

    internal fun callCampaignViewClick(campaignId: String, deepLink: String, actionId: String) {
        if (campaignViewClickCallbackInternalMap.containsKey(campaignId)) {
            campaignViewClickCallbackInternalMap[campaignId]?.get()
                ?.handleCampaignViewClicked(campaignId, deepLink, actionId)
        } else
            Log.d("WECallbackDispatcher", "Error ")
    }

    internal fun onCampaignPrepared(campaignId: String, data: WECampaignData): WECampaignData? {
        if (registeredWEPersonalizationCallback.size > 0) {
            registeredWEPersonalizationCallback.forEach { wePersonalizationCallback ->
                wePersonalizationCallback.onCampaignPrepared(data)
            }
        } else {
            Logger.d(TAG, "onCampaignPrepared no callback attached $campaignId")
        }
        return data
    }

    internal fun onCampaignClicked(
        pId: String,
        variationId: String, actionId: String?, deepLink: String, data: WECampaignData
    ): Boolean {
        EventDispatcher.trackEvent(EventName.INLINE_PERSONALIZATION_CLICK, pId, variationId,
            data.campaignId, data.content?.customData, actionId!!)
        var flag = false
        if (registeredWEPersonalizationCallback.size > 0) {
            registeredWEPersonalizationCallback.forEach { wePersonalizationCallback ->
                flag = flag or wePersonalizationCallback.onCampaignClicked(
                    actionId,
                    deepLink,
                    data
                )
            }
        }

        return flag
    }

    internal fun onCampaignShown(
        pId: String,
        variationId: String,
        campaignId: String,
        data: WECampaignData
    ) {
        EventDispatcher.trackEvent(
            EventName.INLINE_PERSONALIZATION_VIEW,
            pId,
            variationId,
            campaignId,
            attributes = data.content?.customData
        )
        CoroutineScope(Dispatchers.Main).launch {
            if (registeredWEPersonalizationCallback.size > 0) {
                registeredWEPersonalizationCallback.forEach { wePersonalizationCallback ->
                    wePersonalizationCallback.onCampaignShown(data)
                }
            }
        }
    }

    internal fun onCampaignException(campaignId: String?, variationId: String?, pId: String, targetViewId: String, error: Exception) {
        CoroutineScope(Dispatchers.Main).launch {
            if (registeredWEPersonalizationCallback.size > 0) {
                registeredWEPersonalizationCallback.forEach { wePersonalizationCallback ->
                    wePersonalizationCallback.onCampaignException(
                        campaignId,
                        targetViewId,
                        error
                    )
                }
            }
        }
    }

    override fun onDataReceived(data: WECampaignData) {
        val callback = WEPlaceholderSubscriber.getViewCallback(data.targetViewId)
        CoroutineScope(Dispatchers.Main).launch {
            callback?.get()?.onDataReceived(data)
        }
    }

    override fun onPlaceholderException(
        campaignId: String?,
        targetViewId: String,
        error: Exception
    ) {
        val callback = WEPlaceholderSubscriber.getViewCallback(targetViewId)
        CoroutineScope(Dispatchers.Main).launch {
            callback?.get()?.onPlaceholderException(campaignId, targetViewId, error)
        }
    }

    override fun onRendered(data: WECampaignData) {
        val callback = WEPlaceholderSubscriber.getViewCallback(data.targetViewId)
        CoroutineScope(Dispatchers.Main).launch {
            callback?.get()?.onRendered(data)
        }
    }

    internal fun hasCampaignCallbacksAttached(): Boolean {
        return registeredWEPersonalizationCallback.size > 0 || WEPlaceholderSubscriber.hasViewCallbacks()
    }
}