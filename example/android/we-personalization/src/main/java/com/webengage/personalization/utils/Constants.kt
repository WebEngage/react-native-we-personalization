package com.webengage.personalization.utils


object InlineLayouts {
    const val BANNER = "2341ifb0"
    const val TEXT = "1af576a4"
    const val DATA_OPTION = "~20cc49da"
}

object JsonKeys {
    const val STATIC = "static"
    const val DYNAMIC = "dynamic"
    const val PARSER_TYPE = "pt"
    const val TARGET_VIEW = "target_view"
    const val TEMPLATE_DATA = "templateData"
    const val CONFIG = "config"
    const val CONTENT = "ctnt"
    const val CUSTOM = "cstm"
    const val LAYOUT_TYPE = "lt"
    const val SUB_LAYOUT_TYPE = "slt"
    const val CHILDREN = "child"
    const val FRAME_LAYOUT = "FrameLayout"
    const val LINEAR_LAYOUT = "LinearLayout"
    const val IMAGE_VIEW = "Image"
    const val VIEW = "View"
    const val BUTTON = "Button"
    const val TEXT_VIEW = "Text"
    const val HTTP = "http"
    const val HTTPS = "https"
    const val KEY = "key"
    const val VALUE = "value"
    const val TEXT_DESCRIPTION_TYPE = "description"
    const val TEXT_TITLE_TYPE = "title"
    const val REASON = "reason"
}


//region image scale type
object ScaleType {
    const val CENTER = "center"
    const val CENTER_INSIDE = "centerInside"
    const val CENTER_CROP = "centerCrop"
    const val FIT_XY = "fitXY"
}

object TextAlignment {
    const val CENTER = "center"
    const val LEFT = "left"
    const val RIGHT = "right"
}

object LineBreakMode {
    const val START = "truncateHead"
    const val END = "truncateTrail"
    const val MIDDLE = "truncateMiddle"
}

//dimen type
object Dimens {
    const val DP = "dp"
    const val SP = "sp"
    const val PX = "px"
    const val DEFAULT_TEXT_SIZE = 12
    const val MATCH_PARENT = "match_parent"
    const val WRAP_CONTENT = "wrap_content"
}

//Cache file
const val TAG = "WebEngage-Inline"
const val WE_VIEW_TAG = "INLINE_PERSONALIZATION_TAG"
const val WE_APP_PERSONALIZATION = "APP_PERSONALIZATION-"
val THREAD_STATS_TAG = TAG.hashCode()


//Timeouts
object Request {
    const val DEFAULT_READOUT_TIME = 30 * 1000
    const val DEFAULT_CONNECT_TIMEOUT = 5 * 1000
}

object EventName {
    //Event Name
    const val INLINE_PERSONALIZATION_CLICK = "app_personalization_click"
    const val INLINE_PERSONALIZATION_VIEW = "app_personalization_view"
    const val INLINE_PERSONALIZATION_FAILED = "app_personalization_failed"
}

object Animation {
    const val ANIMATION_TIME = 500L
}

object Urls {

}
