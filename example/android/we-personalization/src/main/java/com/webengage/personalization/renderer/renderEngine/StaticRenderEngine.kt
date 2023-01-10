package com.webengage.personalization.renderer.renderEngine

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.webengage.sdk.android.Logger
import com.webengage.personalization.data.WECampaignContent
import com.webengage.personalization.renderer.viewParser.staticLayouts.BannerViewParser
import com.webengage.personalization.renderer.viewParser.staticLayouts.StaticViewParser
import com.webengage.personalization.utils.InlineLayouts
import com.webengage.personalization.utils.TAG
import kotlinx.coroutines.CoroutineScope
import java.lang.ref.WeakReference

class StaticRenderEngine(
    private val campaignId: String?,
    private val weakReferenceActivity: WeakReference<Activity>,
    mParent: ViewGroup,
    private val campaignContent: WECampaignContent,
    private val campaignScope: CoroutineScope
) : RenderEngine(campaignId, weakReferenceActivity, campaignScope, mParent) {

    private var viewParser: StaticViewParser? = null

    override suspend fun render() {
        if (!campaignContent.layoutType.isNullOrEmpty()) {
            when (campaignContent.layoutType) {
                InlineLayouts.BANNER -> handleBannerView()
                InlineLayouts.TEXT -> handleBannerView()
            }
        }
        super.render()
    }

   /* private suspend fun handleCarouselView() {
        weakReferenceActivity.get()?.let {
            if (!it.isFinishing) {
                *//*viewParser = BannerViewParser(
                    campaignId,
                    campaignScope,
                    weakReferenceActivity.get()!!.applicationContext,
                    parent, campaignContent, this
                )
                viewParser!!.processLayout()*//*
                val imageSlider: ImageSlider = ImageSlider(parent.context)
                val imageList = ArrayList<SlideModel>()
                imageList.add(SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years."))
                imageList.add(SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct."))
                imageList.add(SlideModel("https://bit.ly/3fLJf72", "And people do that."))
                imageSlider.setImageList(imageList)
                parent.addView(imageSlider)
            }
        }
    }*/


    private suspend fun handleBannerView() {
        weakReferenceActivity.get()?.let {
            if (!it.isFinishing) {
                viewParser = BannerViewParser(
                    campaignId,
                    campaignScope,
                    weakReferenceActivity.get()!!.applicationContext,
                    parent, campaignContent, this
                )
                viewParser!!.processLayout()
            }
        }
    }

    override fun animationType(): String {
        if(campaignContent.children[0].properties["animationType"] !=null){
            return campaignContent.children[0].properties["animationType"].toString()
        }
        return "default"
    }

    override fun clearViewParser() {
        viewParser = null
    }

    override fun getWERenderedLayout(): View? {
        return viewParser?.getView()
    }
}