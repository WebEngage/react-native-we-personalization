package com.webengage.personalization.renderer.viewParser

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import androidx.cardview.widget.CardView
import com.webengage.personalization.data.WECampaignContent
import com.webengage.personalization.processor.DimensionProcessor
import com.webengage.personalization.utils.Properties
import com.webengage.personalization.utils.TAG
import com.webengage.personalization.utils.WEUtils.Companion.toColor
import com.webengage.sdk.android.Logger
import java.lang.Exception

class CardViewLayoutParser(
    campaignId: String?,
    val context: Context,
    val data: WECampaignContent,
    var cardView: CardView? = null
) : ViewParser(campaignId, context, data) {

    init {
        mView = cardView ?: CardView(context)
        cardView = mView as CardView
        cardView!!.visibility = View.VISIBLE
    }

    override fun processProperties() {
        super.processProperties()
        applyCornerRadius()
        applyShadow()
        applyBackground()
    }

    private fun applyBackground() {
        val background = data.properties[Properties.Background] as? String
        try {
            if (!background.isNullOrBlank()) {
                cardView!!.setCardBackgroundColor(background.toColor()!!)
            }
        } catch (e: Exception) {
        }

    }

    //Shadow will come as 0dp if unchecked
    private fun applyShadow() {
        val shadow = data.properties[Properties.Shadow] as? String
        if (!shadow.isNullOrBlank()) {
            cardView!!.cardElevation =
                DimensionProcessor.processValue(context, shadow, 10).toFloat()
        } else {
            cardView!!.cardElevation = 0f
        }
    }

    private fun applyCornerRadius() {
        var radius = data.properties[Properties.CornerRadius] as? String
        if (!radius.isNullOrBlank()) {
            if (radius == "h/2") {
                cardView!!.viewTreeObserver.addOnGlobalLayoutListener(
                    object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            cardView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            radius = (cardView!!.height / 2).toString() + "dp"
                            val radiusFloat =
                                DimensionProcessor.processValue(context, radius, 0).toFloat()
                            cardView!!.radius = radiusFloat
                        }
                    })
            } else {
                val radiusFloat =
                    DimensionProcessor.processValue(context, radius, 0).toFloat()
                cardView!!.radius = radiusFloat
            }
        } else {
            cardView!!.radius = 0f
        }
    }

    fun asViewGroup(): CardView {
        return cardView!!
    }

}