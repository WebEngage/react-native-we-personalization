package com.webengage.personalization.renderer.viewParser.staticLayouts

import android.view.View

abstract class StaticViewParser()
{
    abstract suspend fun processLayout()
    abstract fun getView(): View?
}