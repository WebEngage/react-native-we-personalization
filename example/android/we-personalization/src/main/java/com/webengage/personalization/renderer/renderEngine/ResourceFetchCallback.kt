package com.webengage.personalization.renderer.renderEngine

interface ResourceFetchCallback {
    fun resourcedFetchedSuccess(url: String)
    fun resourceFetchedFailure(url: String)
    fun addResource(url: String)
}