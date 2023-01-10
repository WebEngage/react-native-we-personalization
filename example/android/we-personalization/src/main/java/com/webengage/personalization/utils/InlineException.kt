package com.webengage.personalization.utils

enum class InlineExceptionType {
    RESOURCE_FETCHING_FAILED,
    CAMPAIGN_FETCHING_FAILED,
    CAMPAIGN_RENDER_TIMEOUT,
    TARGET_PROPERTY_MISSING,
    NON_QUALIFIED_CAMPAIGN_CLEARED,
}
/**
 * - Resource fetching failed. Default/Callback triggered
 * - Campaign failed to render in set time. Default/Callback triggered
 * - Target property missing. Callback triggered
 * - Campaign fetching failed. Default/Callback triggered
 */
class InlineException(message: String?) : Exception(message)


