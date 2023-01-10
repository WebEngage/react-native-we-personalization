package com.webengage.personalization.renderer.viewParser

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.webengage.personalization.callbacks.WECallbackDispatcher
import com.webengage.personalization.data.WECampaignContent
import com.webengage.personalization.data.property.ViewMargin
import com.webengage.personalization.processor.BackgroundProcessor
import com.webengage.personalization.processor.DimensionProcessor
import com.webengage.personalization.processor.GravityProcessor
import com.webengage.personalization.utils.Properties
import com.webengage.personalization.utils.TAG
import com.webengage.sdk.android.Logger


abstract class ViewParser(
    private val campaignId: String?,
    private val context: Context,
    private val data: WECampaignContent
) {

    lateinit var mView: View
    private val packageName = context.packageName

    open fun processProperties() {
        applyViewMargin()
        applyHeightWidth()
        applyFont()
        applyPosition()
        applyPadding()
        applyBackground()
        if (null != campaignId)
            handleActionURL()
    }


    private fun handleActionURL() {
        var actionURL: String? = data.properties[Properties.ACTION_URL] as String?
        if (actionURL.isNullOrEmpty() || actionURL.equals("null", true)) {
            actionURL = data.layoutType
        }
        if (!actionURL.isNullOrEmpty()) {
            mView.setOnClickListener {
                val callToActionId = if (data.getCallToActionId() != null) data.getCallToActionId() else ""
                WECallbackDispatcher.callCampaignViewClick(
                    campaignId!!,
                    actionURL,
                    callToActionId!!
                )
            }
        }
    }

    private fun applyBackground() {
        try {
            if (!(data.properties[Properties.Background] as? String).isNullOrBlank() && mView !is CardView) {
//                Logger.d(
//                    TAG,
//                    "applyBackground value ${(data.properties[Properties.Background] as? String)}"
//                )
                mView.setBackgroundColor(BackgroundProcessor.processValue(data.properties[Properties.Background] as String) as Int)
            }
        } catch (e: Exception) {
            //Logger.e(TAG, "applyBackground exception ${e.message}")
        }
    }

    private fun applyFont() {
        val fontName = data.getFontName()

        if (fontName.isNullOrBlank()) {
            return
        }

        val resources = context.resources
        val id = when {
            (resources.getIdentifier(
                fontName,
                "raw",
                packageName
            )) != 0 -> {
                resources.getIdentifier(
                    fontName,
                    "raw",
                    packageName
                )
            }
            (resources.getIdentifier(
                fontName,
                "font",
                packageName
            )) != 0 -> {
                resources.getIdentifier(
                    fontName,
                    "font",
                    packageName
                )
            }
            else -> {
                0
            }
        }
        if (id != 0) {
            try {
                val font = ResourcesCompat.getFont(context, id)
                if (this::mView.isInitialized) {
                    when (mView) {
                        is AppCompatButton -> {
                            (mView as AppCompatButton).typeface = font
                        }
                        is AppCompatTextView -> {
                            (mView as AppCompatTextView).typeface = font
                        }
                    }
                }
            } catch (e: Exception) {
            }
        } else {
            try {
                val defTypeface = Typeface.create(fontName, Typeface.NORMAL)
                if (this::mView.isInitialized) {
                    when (mView) {
                        is AppCompatButton -> {
                            (mView as AppCompatButton).typeface = defTypeface

                        }
                        is AppCompatTextView -> {
                            (mView as AppCompatTextView).typeface = defTypeface
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun applyHeightWidth() {
        val height = DimensionProcessor.processValue(
            context = context,
            dimen = data.getLayoutHeight(),
            defaultValue = ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val width = DimensionProcessor.processValue(
            context = context,
            dimen = data.getLayoutWidth(),
            defaultValue = ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val layoutParams = mView.layoutParams
        layoutParams.height = height
        layoutParams.width = width
        mView.layoutParams = layoutParams
    }

    private fun applyPosition() {
        val layoutParams = if (this::mView.isInitialized) {
            mView.layoutParams
        } else {
            null
        }
        data.getGravity()?.let {
            val gravityValue = GravityProcessor.processValue(it)
            if (gravityValue == Gravity.NO_GRAVITY)
                return
            if (mView.layoutParams is LinearLayout.LayoutParams) {
                (layoutParams as LinearLayout.LayoutParams).gravity = gravityValue
            } else if (mView.layoutParams is FrameLayout.LayoutParams) {
                (layoutParams as FrameLayout.LayoutParams).gravity = gravityValue
            }
        }
        mView.layoutParams = layoutParams
    }

    private fun applyPadding() {
        val padding = data.getLayoutPadding(context)
        if ((padding.start > 0 || padding.end > 0)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mView.setPaddingRelative(padding.start, padding.top, padding.end, padding.bottom)
            } else {
                mView.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            }
        } else {
           // mView.setPadding(padding.left, padding.top, padding.right, padding.bottom)
        }
    }

    private fun applyViewMargin() {
        val margin: ViewMargin = data.getLayoutMargin(context)
        val layoutParams: ViewGroup.MarginLayoutParams =
            mView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(margin.left, margin.top, margin.right, margin.bottom)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.marginStart =
                margin.left //DimensionProcessor.processValue(context, margin.left.toString()+"dp", 0)
            layoutParams.marginEnd =
                margin.right //DimensionProcessor.processValue(context, margin.right.toString()+"dp", 0)
            layoutParams.setMargins(0, margin.top, 0, margin.bottom)
        } else {
            layoutParams.setMargins(margin.left, margin.top, margin.right, margin.bottom)
        }
        mView.layoutParams = layoutParams
    }

    /**
     * Make text italic, bold or underline
     */
    fun handleTextStyling() {
        (mView as TextView).apply {
            val isItalic = data.properties[Properties.TextView.IS_ITALICS]?.toString().toBoolean()
            val isBold = data.properties[Properties.TextView.IS_BOLD]?.toString().toBoolean()
            val isUnderLine =
                data.properties[Properties.TextView.IS_UNDER_LINE]?.toString().toBoolean()

            //adding extra space for italic so that the last letter is fully visible
            val spanString = if (isItalic) {
                SpannableString("${this.text} ")
            } else {
                SpannableString(this.text)
            }

            if (isItalic) {
                spanString.setSpan(StyleSpan(Typeface.ITALIC), 0, spanString.length, 0)
            }
            if (isUnderLine) {
                spanString.setSpan(UnderlineSpan(), 0, spanString.length, 0)
            }
            if (isBold) {
                spanString.setSpan(StyleSpan(Typeface.BOLD), 0, spanString.length, 0)
            }

            text = spanString
        }
    }
}