package com.webengage.personalization.processor

import android.content.Context
import android.view.ViewGroup
import androidx.collection.LruCache
import com.webengage.personalization.utils.*

object DimensionProcessor {
    private val sDimenCache = LruCache<String, Int>(20)

    init {
        sDimenCache.put(Dimens.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        sDimenCache.put(Dimens.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun processValue(context: Context, dimen: String?, defaultValue: Int): Int {
        if (dimen.isNullOrBlank()) {
            return defaultValue
        }
        if (dimen.contains("dp") || dimen.contains("sp") || dimen.contains("px")) {
            return try {
                val value = sDimenCache[dimen]
                return if (value == null) {
                    val integerValue =
                        dimen.substring(0, dimen.length - 2).toIntOrNull() ?: defaultValue

                    return when (dimen.substring(dimen.length - 2, dimen.length)) {
                        Dimens.DP -> getDPValue(integerValue, dimen, context)
                        Dimens.SP -> getSPValue(integerValue, dimen, context)
                        Dimens.PX -> getPXValue(integerValue, dimen, context)
                        else -> defaultValue
                    }
                } else {
                    value
                }
            } catch (e: Exception) {
                defaultValue
            }
        } else {
            return try {
                val value = sDimenCache[dimen]
                return if (value == null) {
                    val integerValue = dimen.toIntOrNull() ?: defaultValue
                    getDPValue(integerValue, dimen, context)
                } else {
                    value
                }
            } catch (e: java.lang.Exception) {
                defaultValue
            }
        }
    }

    private fun getDPValue(integerValue: Int, dimen: String, context: Context): Int {
        val value = integerValue.toFloat()
        val dpValue = WEUtils.getDP(context, value)
        sDimenCache.put(dimen, dpValue)
        return sDimenCache[dimen] ?: 0
    }

    private fun getSPValue(integerValue: Int, dimen: String, context: Context): Int {
        sDimenCache.put(dimen, integerValue)
        return sDimenCache[dimen] ?: 0
    }

    private fun getPXValue(integerValue: Int, dimen: String, context: Context): Int {
        val value = integerValue.toFloat()
        val pxValue = WEUtils.getPX(context, value)
        sDimenCache.put(dimen, pxValue)
        return sDimenCache[dimen] ?: 0
    }
}