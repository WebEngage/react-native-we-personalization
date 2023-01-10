package com.webengage.personalization.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import java.net.HttpURLConnection

class WEUtils {
    companion object {
        fun getDP(context: Context, valueInDP: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                valueInDP,
                context.resources.displayMetrics
            ).toInt()
        }

        fun getSP(context: Context, valueInDP: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                valueInDP,
                context.resources.displayMetrics
            ).toInt()
        }

        fun getPX(context: Context, valueInDP: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                valueInDP,
                context.resources.displayMetrics
            ).toInt()
        }

        fun isViewGroup(type: String): Boolean {
            return (JsonKeys.FRAME_LAYOUT.equals(type, ignoreCase = true)
                    || JsonKeys.LINEAR_LAYOUT.equals(type, ignoreCase = true)
                    || JsonKeys.VIEW.equals(type, ignoreCase = true))
        }

        fun String.toColor(): Int? {
            return try {
                Color.parseColor(this)
            } catch (e: Throwable) {
                null
            }
        }

        fun checkGZIP(con: HttpURLConnection): Boolean {
            val encoding = con.contentEncoding
            return (encoding != null
                    && (encoding == "gzip" || encoding == "zip" || (encoding
                    == "application/x-gzip-compressed")))
        }

        fun getParams(params: Map<String?, String?>): String {
            val sb = StringBuffer()
            params.map {
                sb.append(if (sb.length == 0) "" else "&")
                sb.append(it.key).append("=").append(it.value)
            }
            return sb.toString()
        }

        fun View.isInViewport(): Boolean {
            val bounds = Rect()
            return this.getGlobalVisibleRect(bounds)
                    && this.height == bounds.height() && this.width == bounds.width()
//            val actualPosition = Rect()
//            this.getGlobalVisibleRect(actualPosition)
//            val screen = Rect(0, 0, this.resources.displayMetrics.widthPixels, this.resources.displayMetrics.heightPixels)
//            return actualPosition.intersect(screen)
        }

        fun slideView(parent: ViewGroup, view: View, currentHeight: Int, newHeight: Int) {
            val slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(500)

            /* We use an update listener which listens to each tick
             * and manually updates the height of the view  */
            view.measure(view.layoutParams.width, view.layoutParams.height)
            val measuredH = view.measuredHeight
            /*  We use an animationSet to play the animation  */

            val animationSet = AnimatorSet();
            animationSet.interpolator = AccelerateDecelerateInterpolator();
            animationSet.play(slideAnimator);
            animationSet.start()
            parent.layoutParams.height = 0
            parent.visibility = View.VISIBLE
            slideAnimator.addUpdateListener { animation1 ->
                val value = animation1.animatedValue as Int
                if (value == measuredH) {
                    view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    animationSet.cancel()
                } else {
                    view.layoutParams.height = value
                    parent.layoutParams.height = value
                    if (value == newHeight) {
                        view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                view.requestLayout()
            }
        }
    }
}


