package com.webengage.personalization.processor

import android.annotation.SuppressLint
import android.view.Gravity
import com.webengage.personalization.utils.Properties

@SuppressLint("RtlHardcoded")
object GravityProcessor {
    private val mGravityMap = mutableMapOf<String, Int>()

    init {
        mGravityMap[Properties.CENTER] = Gravity.CENTER
        mGravityMap[Properties.LEFT] = Gravity.LEFT
        mGravityMap[Properties.RIGHT] = Gravity.RIGHT
        mGravityMap[Properties.TOP] = Gravity.TOP
        mGravityMap[Properties.BOTTOM] = Gravity.BOTTOM
        mGravityMap[Properties.START] = Gravity.START
        mGravityMap[Properties.END] = Gravity.END
    }

    fun processValue(value: String?): Int {
        if (value == null) {
            return Gravity.NO_GRAVITY
        }
        val gravities = value.split("|")
        var returnGravity = Gravity.NO_GRAVITY
        for (gravity in gravities) {
            val gravityValue = mGravityMap[gravity]
            if (null != gravityValue) {
                returnGravity = returnGravity.or(gravityValue)
            }
        }
        return returnGravity
    }
}