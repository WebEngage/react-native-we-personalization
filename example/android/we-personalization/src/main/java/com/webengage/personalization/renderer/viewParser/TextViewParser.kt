package com.webengage.personalization.renderer.viewParser

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.webengage.personalization.data.WECampaignContent
import com.webengage.personalization.processor.DimensionProcessor
import com.webengage.personalization.utils.LineBreakMode
import com.webengage.personalization.utils.Properties
import com.webengage.personalization.utils.TextAlignment
import com.webengage.personalization.utils.WEUtils.Companion.toColor
import com.webengage.sdk.android.utils.htmlspanner.WEHtmlParserInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextViewParser(
    campaignId: String?,
    private val context: Context,
    private val data: WECampaignContent,
    private val textView: TextView?
) : ViewParser(campaignId, context, data) {

    init {
        initTextView()
    }

    private fun initTextView() {
        mView = this.textView ?: AppCompatTextView(context)
        CoroutineScope(Dispatchers.Main).launch {
            if(textView?.text.isNullOrBlank()) {
                mView.visibility = View.GONE
            } else {
                mView.visibility =View.VISIBLE
            }
        }
    }

    override fun processProperties() {
        super.processProperties()
        handleTextSize()
        handleTextColor()
        handleTextStyling()
        handleTextAlignment()
        handleText()
        handleTextTruncateEllipsize()
    }

    private fun handleTextAlignment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            (mView as AppCompatTextView).textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            data.properties[Properties.TextView.TextAlignment]?.let {
                when (it as String) {
                    TextAlignment.CENTER -> {
                        (mView as AppCompatTextView).textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    }
                    TextAlignment.LEFT -> {
                        (mView as AppCompatTextView).textAlignment =
                            TextView.TEXT_ALIGNMENT_VIEW_START
                    }
                    TextAlignment.RIGHT -> {
                        (mView as AppCompatTextView).textAlignment =
                            TextView.TEXT_ALIGNMENT_VIEW_END
                    }
                    else -> {
                        (mView as AppCompatTextView).textAlignment =
                            TextView.TEXT_ALIGNMENT_VIEW_START
                    }
                }
            }
        }
    }

    private fun handleTextTruncateEllipsize() {
        //ellipsize
        data.properties[Properties.TextView.LineBreakMode]?.let {
            when (it as String) {
                LineBreakMode.START -> {
                    (mView as AppCompatTextView).ellipsize = TextUtils.TruncateAt.START
                }
                LineBreakMode.END -> {
                    (mView as AppCompatTextView).ellipsize = TextUtils.TruncateAt.END
                }
                LineBreakMode.MIDDLE -> {
                    (mView as AppCompatTextView).ellipsize = TextUtils.TruncateAt.MIDDLE
                }
                else -> {
                    (mView as AppCompatTextView).ellipsize = TextUtils.TruncateAt.END
                }
            }
        }

    }

    private fun handleText() {
        //Extra blank space is added at the end because while italicising the text android
        // textview clips the last character. a known bug from android so need to do a fix around it
        val textualData = data.properties[Properties.TextView.Text]?.toString().orEmpty()+" "
        if (textualData.isNotEmpty()) {
            val textView = (mView as AppCompatTextView)
            val htmlParserInterface = WEHtmlParserInterface()
            val spannableString = htmlParserInterface.fromHtml(
                textualData,
                textView.currentTextColor,
                textView.solidColor,
                textView.textSize
            )
            //don't trim space at end of text needed to support italic text
            textView.text = spannableString
            val maxLines =
                data.properties[Properties.TextView.MaxLines]?.toString()?.toIntOrNull() ?: 0
            if (maxLines > 0)
                textView.maxLines = maxLines
        } else {
            textView?.visibility = View.GONE
        }
    }

    private fun handleTextSize() {
        val textDimen = data.properties[Properties.TextView.TextSize]?.toString()
        val textSize = DimensionProcessor.processValue(context, textDimen, 14)
        (mView as AppCompatTextView).textSize = textSize.toFloat()
    }

    private fun handleTextColor() {
        val color = data.properties[Properties.TextView.TextColor]?.toString()?.toColor()
        color?.let { (mView as AppCompatTextView).setTextColor(it) }
    }

    fun asView(): View {
        return mView
    }
}