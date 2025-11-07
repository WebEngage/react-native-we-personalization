package com.webengage.we_personalization_rn

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.webengage.we_personalization_rn.bridge.WEPersonalizationBridge

class WEPersonalizationModule(reactContext: ReactApplicationContext) :
    com.webengage.we_personalization_rn.NativeWEPersonalizationBridgeSpec(reactContext) {

    private val bridge: WEPersonalizationBridge = WEPersonalizationBridge(reactContext)

    override fun getName(): String {
        return "WEPersonalizationBridge"
    }

    override fun initWePersonalization() {
        bridge.initWePersonalization()
    }

    override fun registerProperty(propertyId: String?, screenName: String?) {
        if (propertyId != null && screenName != null) {
            bridge.registerProperty(propertyId, screenName)
        }
    }

    override fun deregisterProperty(propertyId: String?) {
        propertyId?.let { bridge.deregisterProperty(it) }
    }

    override fun registerWECampaignCallback() {
        bridge.registerWECampaignCallback()
    }

    override fun deregisterWECampaignCallback() {
        bridge.deregisterWECampaignCallback()
    }

    override fun trackClick(propertyId: String?, attributes: ReadableMap?) {
        if (propertyId != null) {
            bridge.trackClick(propertyId, attributes)
        }
    }

    override fun trackImpression(propertyId: String?, attributes: ReadableMap?) {
        if (propertyId != null) {
            bridge.trackImpression(propertyId, attributes)
        }
    }

    override fun addListener(eventType: String?) {
        // Handled by React Native
    }

    override fun removeListeners(count: Double) {
        // Handled by React Native
    }
}