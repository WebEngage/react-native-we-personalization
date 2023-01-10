package com.webengage.personalization.data

import android.content.Context
import com.webengage.personalization.data.property.ViewMargin
import com.webengage.personalization.data.property.ViewPadding
import com.webengage.personalization.processor.DimensionProcessor
import com.webengage.personalization.utils.Dimens.MATCH_PARENT
import com.webengage.personalization.utils.JsonKeys.IMAGE_VIEW
import com.webengage.personalization.utils.Properties
import com.webengage.personalization.utils.TAG
import com.webengage.sdk.android.Logger

data class WECampaignContent(
    var layoutType: String? = "",
    var subLayoutType: String = "",
    var properties: MutableMap<String, Any> = mutableMapOf(),
    var children: MutableList<WECampaignContent> = mutableListOf(),
    var customData: HashMap<String, Any> = hashMapOf(),
) {
    fun getLayoutHeight(): String? {
        val height = properties[Properties.Height] as String?
        return when (layoutType) {
            IMAGE_VIEW -> {
                if (height.isNullOrBlank()) {
                    MATCH_PARENT
                } else {
                    height
                }
            }
            else -> height
        }
    }

    fun getFontName(): String? {
        return properties[Properties.FONT_NAME] as String?
    }

    fun getLayoutWidth(): String? {
        val width = properties[Properties.Width] as String?
        return when (layoutType) {
            IMAGE_VIEW -> {
                if (width.isNullOrBlank()) {
                    MATCH_PARENT
                } else {
                    width
                }
            }
            else -> width
        }
    }

    fun getLayoutCornerRadius(): String? {
        return properties[Properties.CornerRadius] as String?
    }

    fun getGravity(): String? {
        //return properties[Properties.LayoutPosition] as String? ?: "center"
        return properties[Properties.LayoutPosition] as String?
    }

    fun getCallToActionId(): String? {
        return properties[Properties.Keys.CallToAction] as String?
    }

    fun getLayoutMargin(context: Context): ViewMargin {
        val marginAll = properties[Properties.Margin] as String?
        return if (!marginAll.isNullOrBlank()) {
            val value = DimensionProcessor.processValue(
                context = context,
                dimen = marginAll,
                defaultValue = 0
            )
            ViewMargin(value, value, value, value)

        } else {
            ViewMargin(
                DimensionProcessor.processValue(context, getLayoutMarginLeft(), 0),
                DimensionProcessor.processValue(context, getLayoutMarginTop(), 0),
                DimensionProcessor.processValue(context, getLayoutMarginRight(), 0),
                DimensionProcessor.processValue(context, getLayoutMarginBottom(), 0)
            )
        }
    }

    private fun getLayoutMarginTop(): String? {
        return properties[Properties.MarginTop] as String?
    }

    private fun getLayoutMarginLeft(): String? {
        return properties[Properties.MarginLeft] as String?
    }

    private fun getLayoutMarginRight(): String? {
        return properties[Properties.MarginRight] as String?
    }

    private fun getLayoutMarginBottom(): String? {
        return properties[Properties.MarginBottom] as String?
    }

    fun getLayoutMarginStart(): String? {
        return properties[Properties.MarginStart] as String?
    }

    fun getLayoutMarginEnd(): String? {
        return properties[Properties.MarginEnd] as String?
    }

    fun getLayoutPadding(context: Context): ViewPadding {
        val padding = properties[Properties.Padding] as String?
        return if (!padding.isNullOrBlank()) {
            val value = DimensionProcessor.processValue(
                context = context,
                dimen = padding,
                defaultValue = 0
            )
            Logger.d(TAG, " Padding: " + value)
            ViewPadding(value, value, value, value)
        } else {
            ViewPadding(
                DimensionProcessor.processValue(context, getLayoutPaddingLeft(), 0),
                DimensionProcessor.processValue(context, getLayoutPaddingTop(), 0),
                DimensionProcessor.processValue(context, getLayoutPaddingRight(), 0),
                DimensionProcessor.processValue(context, getLayoutPaddingBottom(), 0),
                DimensionProcessor.processValue(context, getLayoutPaddingStart(), 0),
                DimensionProcessor.processValue(context, getLayoutPaddingEnd(), 0)
            )
        }
    }

    private fun getLayoutPaddingTop(): String? {
        return properties[Properties.PaddingTop] as String?
    }

    private fun getLayoutPaddingLeft(): String? {
        return properties[Properties.PaddingLeft] as String?
    }

    private fun getLayoutPaddingRight(): String? {
        return properties[Properties.PaddingRight] as String?
    }

    private fun getLayoutPaddingBottom(): String? {
        return properties[Properties.PaddingBottom] as String?
    }

    private fun getLayoutPaddingStart(): String? {
        return properties[Properties.PaddingStart] as String?
    }

    private fun getLayoutPaddingEnd(): String? {
        return properties[Properties.PaddingEnd] as String?
    }

}