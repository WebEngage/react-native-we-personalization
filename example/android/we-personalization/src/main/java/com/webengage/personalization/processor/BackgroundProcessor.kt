package com.webengage.personalization.processor

import com.webengage.personalization.utils.WEUtils.Companion.toColor

object BackgroundProcessor {
    fun processValue(background: String): Any {
        return if (background.contains("#")) {
            background.toColor()!!
        } else {
            //For drawable support in phase 2
            ""
        }
    }
}