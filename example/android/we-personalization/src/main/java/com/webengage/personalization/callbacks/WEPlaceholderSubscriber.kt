package com.webengage.personalization.callbacks

import java.lang.ref.WeakReference

internal object WEPlaceholderSubscriber {
    private val SUBSCRIBED_VIEWS: MutableMap<String, WeakReference<WEPlaceholderCallback>> = mutableMapOf()

    fun subscribe(parentID: String, listener: WEPlaceholderCallback) {
        SUBSCRIBED_VIEWS[parentID] = WeakReference(listener)

    }

    fun unsubscribe(parentID: String) {
        SUBSCRIBED_VIEWS.remove(parentID)
    }

    fun getViewCallback(id: String): WeakReference<WEPlaceholderCallback>? {
        return SUBSCRIBED_VIEWS[id]
    }

    internal fun hasViewCallbacks(): Boolean {
        return SUBSCRIBED_VIEWS.isNotEmpty()
    }
}