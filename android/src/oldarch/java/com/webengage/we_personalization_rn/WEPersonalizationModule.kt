package com.webengage.we_personalization_rn

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.webengage.we_personalization_rn.bridge.WEPersonalizationBridge
import com.webengage.we_personalization_rn.utils.WEConstants

class WEPersonalizationModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    private val bridge = WEPersonalizationBridge(reactContext)

    override fun getName(): String = WEConstants.PERSONALIZATION_BRIDGE

    @ReactMethod
    fun initWePersonalization() {
        bridge.initWePersonalization()
    }

    @ReactMethod
    fun registerProperty(propertyId: String?, screenName: String?) {
        if (propertyId != null && screenName != null) {
            bridge.registerProperty(propertyId, screenName)
        }
    }

    @ReactMethod
    fun deregisterProperty(propertyId: String?) {
        propertyId?.let { bridge.deregisterProperty(it) }
    }

    @ReactMethod
    fun registerWECampaignCallback() {
        bridge.registerWECampaignCallback()
    }

    @ReactMethod
    fun deregisterWECampaignCallback() {
        bridge.deregisterWECampaignCallback()
    }

    @ReactMethod
    fun trackClick(propertyId: String?, attributes: ReadableMap?) {
        if (propertyId != null) {
            bridge.trackClick(propertyId, attributes)
        }
    }

    @ReactMethod
    fun trackImpression(propertyId: String?, attributes: ReadableMap?) {
        if (propertyId != null) {
            bridge.trackImpression(propertyId, attributes)
        }
    }
}