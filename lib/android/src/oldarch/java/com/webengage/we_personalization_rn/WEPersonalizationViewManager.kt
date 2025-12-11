package com.webengage.we_personalization_rn

import android.view.ViewGroup
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.annotations.ReactPropGroup
import com.webengage.we_personalization_rn.utils.WEConstants

class WEPersonalizationViewManager(private val applicationContext: ReactApplicationContext) : SimpleViewManager<ViewGroup>() {

    private var width = 0
    private var height = 0
    private var screenName: String? = null
    private var propertyId: String? = null

    override fun getName(): String = WEPersonalizationViewManagerImpl.NAME

    override fun createViewInstance(context: ThemedReactContext): ViewGroup {
        return WEPersonalizationViewManagerImpl.createViewInstance(context)
    }

    @ReactPropGroup(names = [WEConstants.WIDTH, WEConstants.HEIGHT], customType = WEConstants.STYLE)
    fun setStyle(view: ViewGroup, index: Int, value: Int) {
        when (index) {
            0 -> width = value
            1 -> height = value
        }
            WEPersonalizationViewManagerImpl.setStyle(view, width, height)
    }

    @ReactProp(name = WEConstants.PROPERTY_ID)
    fun setPropertyId(view: ViewGroup, propertyId: String?) {
        this.propertyId = propertyId
        WEPersonalizationViewManagerImpl.updateProperties(view, screenName, propertyId)
    }

    @ReactProp(name = WEConstants.SCREEN_NAME)
    fun setScreenName(view: ViewGroup, screenName: String?) {
        this.screenName = screenName
            WEPersonalizationViewManagerImpl.updateProperties(view, screenName, propertyId)
    }
}