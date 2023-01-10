package com.webengage.personalization.renderer.viewParser

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.webengage.personalization.data.WECampaignContent
import com.webengage.personalization.processor.DimensionProcessor
import com.webengage.personalization.utils.Dimens.DEFAULT_TEXT_SIZE
import com.webengage.personalization.utils.Properties
import com.webengage.personalization.utils.TAG
import com.webengage.personalization.utils.WEUtils.Companion.toColor
import com.webengage.sdk.android.Logger
import com.webengage.sdk.android.utils.htmlspanner.WEHtmlParserInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ButtonViewParser(
    campaignId: String?,
    private val context: Context,
    private val data: WECampaignContent,
    private val button: AppCompatButton? = null
) : ViewParser(campaignId, context, data) {

    init {
        initButtonView()
    }

    private fun initButtonView() {
        mView = this.button ?: AppCompatButton(context)
        CoroutineScope(Dispatchers.Main).launch {
            if(button?.text.isNullOrBlank()) {
                mView.visibility = View.GONE
            } else {
                mView.visibility =View.VISIBLE
            }
        }
    }

    override fun processProperties() {
        super.processProperties()
        handleText()
        handleTextAllCaps()
        handleTextStyling()
        val vto = button!!.viewTreeObserver
        vto.addOnGlobalLayoutListener {
            val width = button!!.width
            val height = button!!.height
            handleStyling(width, height)
        }
    }

    private fun handleTextAllCaps() {
        val textAllCaps = data.properties[Properties.TextView.TextAllCaps]
        textAllCaps?.let {
            (mView as AppCompatButton).isAllCaps = it as Boolean
        }
    }


    /**
     * Handle corners, backgroundColor, borderWidth, borderColor,
     */
    private fun handleStyling(width: Int, height: Int) {

        val gradientDrawable = GradientDrawable()
        //applying background color
        val backgroundColor =
            data.properties[Properties.Background]?.toString()?.toColor()
        if (backgroundColor != null) {
            gradientDrawable.setColor(backgroundColor)
        }

        //applying corner radius
        val cornerRadius = data.getLayoutCornerRadius()
        if (!cornerRadius.isNullOrBlank()) {
            gradientDrawable.cornerRadius =
                DimensionProcessor.processValue(context, cornerRadius, 0).toFloat()
        }

        //applying border color & width
        val borderColor =
            data.properties[Properties.BorderColor]?.toString()?.toColor()
        val borderWidth = data.properties[Properties.BorderWidth]?.toString()
        if (borderColor != null) {
            var width = 2
            if (!borderWidth.isNullOrBlank()) {
                width = DimensionProcessor.processValue(
                    context = context,
                    dimen = borderWidth,
                    defaultValue = 0
                )
            }
            gradientDrawable.setStroke(width, borderColor)
            gradientDrawable.shape = GradientDrawable.RECTANGLE
        }

        (mView as AppCompatButton).background = gradientDrawable
        (mView as AppCompatButton).gravity = Gravity.CENTER
        //applying ripple effect
        val stateList = ColorStateList.valueOf(Color.parseColor("#1f000000"))
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mView.background = RippleDrawable(stateList, mView.background, null)
        }
    }

    /**
     * Handle text size and color
     */
    private fun handleText() {
        //apply text size
        val textSize = data.properties[Properties.TextView.TextSize]?.toString()
        val buttonView = (mView as AppCompatButton)
        buttonView.textSize = DimensionProcessor.processValue(context, textSize, DEFAULT_TEXT_SIZE).toFloat()

        //set text
        val text = data.properties[Properties.TextView.Text]?.toString().orEmpty().trim()
        if (text.isNotEmpty()) {
            if (!text.isNullOrBlank()) {
                val htmlParserInterface = WEHtmlParserInterface()
                buttonView.text = htmlParserInterface.fromHtml(
                    text, buttonView.currentTextColor,
                    buttonView.solidColor, buttonView.textSize
                ).trim()
            }

            //apply text color
            val textColor = data.properties[Properties.TextView.TextColor]?.toString()?.toColor()
            textColor?.let { (mView as AppCompatButton).setTextColor(it) }
        } else {
            buttonView.visibility = View.GONE
        }
    }

    fun asView(): View {
        return mView
    }
}