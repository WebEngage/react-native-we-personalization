package com.webengage.personalization.renderer.viewParser

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.webengage.sdk.android.Logger
import com.webengage.personalization.data.WECampaignContent
import com.webengage.personalization.data.network.CachePolicy
import com.webengage.personalization.data.network.RequestMethod
import com.webengage.personalization.data.network.RequestObject
import com.webengage.personalization.processor.DimensionProcessor
import com.webengage.personalization.renderer.renderEngine.ResourceFetchCallback
import com.webengage.personalization.utils.*
import com.webengage.personalization.utils.Properties.View.SCALE_TYPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class ImageViewParser(
    private val campaignId: String?,
    private val context: Context,
    private val data: WECampaignContent,
    private val imageView: AppCompatImageView? = null
) : ViewParser(campaignId, context, data) {

    init {
        initImageView()
    }

    private fun initImageView() {
        mView = this.imageView ?: AppCompatImageView(context)
        CoroutineScope(Dispatchers.Main).launch {
            mView.visibility = View.VISIBLE
        }
    }

    override fun processProperties() {
        super.processProperties()
        applyImageScaleType()
        applyCornerRadius()
    }

    private fun applyCornerRadius() {
        var radius = data.properties[Properties.CornerRadius] as? String
        if (!radius.isNullOrBlank()) {
            mView.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        if (radius == "h/2") {
                            radius = (mView.height / 2).toString() + "dp"
                        }
                        val radiusFloat =
                            DimensionProcessor.processValue(context, radius, 0).toFloat()
                        val roundedBitmapDrawable: RoundedBitmapDrawable? =
                            (mView as ImageView).drawable as? RoundedBitmapDrawable
                        roundedBitmapDrawable?.cornerRadius = radiusFloat
                    }
                })
        }
    }

    fun loadResource(resourceFetchCallback: WeakReference<ResourceFetchCallback>): RoundedBitmapDrawable? {
        val url: String = data.properties[Properties.SRC]?.toString().orEmpty()
        if (url.isNotBlank()) {
            val headers = mutableMapOf<String, String>()
            headers["accept"] = "image/webp";
            val requestObjBuilder =
                RequestObject.Builder(url, RequestMethod.GET, WeakReference(context))
                    .setHeaders(headers)
            requestObjBuilder.cachePolicy =
                CachePolicy.GET_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE
            val imgRequestObject = requestObjBuilder.build()
            try {
                val imgRequest = imgRequestObject.execute()
                if (null != imgRequest && imgRequest.isReadable()) {
                    val imgBitmap = BitmapFactory.decodeStream(imgRequest.inputStream)
                    val roundedBitmapDrawable: RoundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(
                            mView.resources, imgBitmap
                        )
                    resourceFetchCallback.get()?.resourcedFetchedSuccess(url = url)
                    return roundedBitmapDrawable
                } else {
//                    Logger.d(
//                        "TAG",
//                        "imgRequest in onFailure.invoke() ${imgRequest?.exception} for $campaignId"
//                    )
                    resourceFetchCallback.get()?.resourceFetchedFailure(url = url)
                }
            } catch (e: Exception) {
//                Logger.d(
//                    "TAG",
//                    "imgRequest in onFailure.invoke() with exception: " + e.printStackTrace()
//                )
                resourceFetchCallback.get()?.resourceFetchedFailure(url = url)
            }
        }
        return null
    }

    private fun applyImageScaleType() {
        data.properties[SCALE_TYPE]?.let {
            when (it as String) {
                ScaleType.CENTER -> {
                    (mView as AppCompatImageView).scaleType = ImageView.ScaleType.CENTER
                }
                ScaleType.CENTER_INSIDE -> {
                    (mView as AppCompatImageView).scaleType = ImageView.ScaleType.CENTER_INSIDE
                }
                ScaleType.CENTER_CROP -> {
                    (mView as AppCompatImageView).scaleType = ImageView.ScaleType.CENTER_CROP
                }
                ScaleType.FIT_XY -> {
                    (mView as AppCompatImageView).scaleType = ImageView.ScaleType.FIT_XY
                }
            }
        }
    }

    fun asView(): View {
        return mView
    }
}