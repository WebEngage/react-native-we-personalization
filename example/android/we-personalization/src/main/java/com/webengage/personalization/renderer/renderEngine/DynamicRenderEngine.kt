package com.webengage.personalization.renderer.renderEngine

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.webengage.personalization.data.WECampaignContent
import com.webengage.personalization.renderer.viewParser.CardViewLayoutParser
import com.webengage.personalization.utils.*
import kotlinx.coroutines.CoroutineScope
import java.lang.ref.WeakReference

class DynamicRenderEngine(
    private val campaignId: String?,
    private val weakReferenceActivity: WeakReference<Activity>,
    mParent: ViewGroup,
    private val campaignContent: WECampaignContent,
    campaignScope: CoroutineScope
) : RenderEngine(campaignId,weakReferenceActivity, campaignScope, mParent) {

    override suspend fun render() {
        readParent(weakReferenceActivity.get()!!.applicationContext, parent, campaignContent)
        super.render()
    }

    override fun animationType(): String {
        //TODO("Not yet implemented")
        return "None"
    }


    override fun clearViewParser() {

    }

    override fun getWERenderedLayout(): View? {
        return null
    }

    private fun readParent(context: Context, parent: ViewGroup, WECampaign: WECampaignContent) {
        when (WECampaign.layoutType) {
            JsonKeys.FRAME_LAYOUT -> {
                val frameLayout = CardViewLayoutParser(campaignId, context, WECampaign)
                parent.addView(frameLayout.asViewGroup())
                frameLayout.processProperties()
                val _parent = frameLayout.asViewGroup()
                for (child in WECampaign.children) {
                    if (WEUtils.isViewGroup(child.layoutType!!)) {
                        readParent(context, _parent, child)
                    } else {
                        inflateChildren(context, _parent, child)
                    }
                }
            }
        }
    }

    private fun inflateChildren(context: Context, _parent: ViewGroup, child: WECampaignContent) {
        when (child.layoutType) {
            JsonKeys.IMAGE_VIEW -> {
//                val imageViewParser = ImageViewParser(context, child, this)
//                _parent.addView(imageViewParser.asView())
//                imageViewParser.processProperties()
            }

            JsonKeys.BUTTON -> {
//                val btnViewParser = ButtonViewParser(context, child)
//                _parent.addView(btnViewParser.asView())
//                btnViewParser.processProperties()
            }
        }

    }
}