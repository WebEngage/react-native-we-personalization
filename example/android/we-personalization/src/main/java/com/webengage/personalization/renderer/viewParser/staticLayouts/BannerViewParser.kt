package com.webengage.personalization.renderer.viewParser.staticLayouts

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.webengage.personalization.R
import com.webengage.personalization.data.WECampaignContent
import com.webengage.personalization.renderer.renderEngine.ResourceFetchCallback
import com.webengage.personalization.renderer.viewParser.ButtonViewParser
import com.webengage.personalization.renderer.viewParser.CardViewLayoutParser
import com.webengage.personalization.renderer.viewParser.ImageViewParser
import com.webengage.personalization.renderer.viewParser.TextViewParser
import com.webengage.personalization.utils.*
import com.webengage.sdk.android.Logger
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.ref.WeakReference
import kotlin.coroutines.coroutineContext

/** CampaignScope -> network Call -> Render
 *  ResourceScope -> ??
 *
 */
class BannerViewParser(
    private val campaignId: String?,
    private val campaignScope: CoroutineScope,
    private val context: Context,
    private val parent: ViewGroup,
    private val data: WECampaignContent,
    private val resourceFetchCallback: ResourceFetchCallback
) : StaticViewParser() {

    //region global variables
    private val bannerLayout by lazy {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutId = if (data.layoutType.equals(
                InlineLayouts.BANNER,
                true
            )
        ) R.layout.we_banner_layout else R.layout.we_banner_layout
        layoutInflater?.let {
            layoutInflater.inflate(
                layoutId,
                parent,
                false
            )
        }
    }

    override suspend fun processLayout() {
        data.children.forEach {
            handleParentView(it)
        }
    }

    private suspend fun handleParentView(campaignContent: WECampaignContent) {
        when (campaignContent.layoutType) {
            JsonKeys.VIEW -> {
                for (child in campaignContent.children) {
                    if (WEUtils.isViewGroup(child.layoutType!!)) {
                        handleParentView(child)
                    } else {
                        handleChildView(child)
                    }
                }
                handleCardView(campaignContent)
            }
        }
    }

    /**
     * Handle all the child view mentioned in layout content
     */

    private suspend fun handleChildView(childCampaignContent: WECampaignContent) {
        //Logger.d(TAG, "bannerLayout handleChildView layoutType ${childCampaignContent.layoutType}")
        when (childCampaignContent.layoutType) {
            JsonKeys.IMAGE_VIEW -> handleImageView(childCampaignContent)
            JsonKeys.BUTTON -> handleButton(childCampaignContent)
            JsonKeys.TEXT_VIEW -> handleTextView(childCampaignContent)
            null -> {
            }
        }
    }

    private fun handleCardView(campaignContent: WECampaignContent) {
        val cardView: CardView? = bannerLayout?.findViewById(R.id.we_parent_card_view)
        val cardViewParser = CardViewLayoutParser(campaignId, context, campaignContent, cardView)
        cardViewParser.processProperties()
    }

    private fun handleTextView(campaignContent: WECampaignContent) {
        val tv: AppCompatTextView? = when (campaignContent.subLayoutType) {
            JsonKeys.TEXT_TITLE_TYPE -> {
                bannerLayout?.findViewById(R.id.we_banner_title)
            }
            JsonKeys.TEXT_DESCRIPTION_TYPE -> {
                bannerLayout?.findViewById(R.id.we_banner_description)
            }
            else -> {
                bannerLayout?.findViewById(R.id.we_banner_title)
            }
        }
        val textViewViewParser = TextViewParser(campaignId, context, campaignContent, tv)
        textViewViewParser.processProperties()
        if (tv?.text.isNullOrEmpty()) {
            tv?.visibility = View.GONE
        }
    }

    private fun handleButton(campaignContent: WECampaignContent) {
        // Logger.d(TAG, "Logger for execution in handleButton $campaignId")
        val button: AppCompatButton? =
            bannerLayout?.findViewById(R.id.we_banner_btn1)
        val buttonViewParser = ButtonViewParser(campaignId, context, campaignContent, button)
        buttonViewParser.processProperties()
        if (button?.text.isNullOrEmpty()) {
            button?.visibility = View.GONE
        }
    }

    private suspend fun handleImageView(campaignContent: WECampaignContent) {
        val imageView: AppCompatImageView? =
            bannerLayout?.findViewById(R.id.we_banner_iv_bg)
        val imageViewParser = if (imageView != null) {
            ImageViewParser(campaignId, context, campaignContent, imageView)
        } else {
            ImageViewParser(campaignId, context, campaignContent)
        }

        val url = campaignContent.properties[Properties.SRC]?.toString().orEmpty()
        if (url.isEmpty()) {
            imageView?.visibility = View.GONE
            if (data.layoutType.equals(InlineLayouts.BANNER, true)) {
                campaignScope.cancel()
            }
        } else {
            imageViewParser.processProperties()
            if ((url.contains(JsonKeys.HTTP) || url.contains(JsonKeys.HTTPS))) {
                resourceFetchCallback.addResource(url)
                withContext(campaignScope.coroutineContext + Dispatchers.IO) {
                    imageViewParser.loadResource(WeakReference(resourceFetchCallback))
                }?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        imageView?.setImageDrawable(it)
                    }
                }
            } else {
                val drawableImage = getResourceByName(url)
                if (drawableImage != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        imageView?.setImageDrawable(drawableImage)
                    }
                } else {
                    imageView?.visibility = View.GONE
                    if (data.layoutType.equals(InlineLayouts.BANNER, true)) {
                        Logger.d(
                            TAG,
                            "Image is not found in res drawable folder for campaign id $campaignId of resource name $url"
                        )
                        campaignScope.cancel()
                    }
                }
            }
        }

    }

    private fun getResourceByName(name: String): Drawable? {
        val resID = context.resources.getIdentifier(
            name,
            "drawable",
            context.applicationContext.packageName
        )
        return if (resID > 0)
            try {
                ActivityCompat.getDrawable(context, resID)
            } catch (e: Exception) {
                null
            }
        else
            null
    }

    override fun getView(): View? {
        return bannerLayout
    }
}