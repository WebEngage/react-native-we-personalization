package com.webengage.personalization.renderer.renderEngine

import android.app.ActionBar
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.webengage.personalization.utils.*
import com.webengage.sdk.android.Logger
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

private typealias URL = String
private typealias DOWNLOADED = Boolean

abstract class RenderEngine(
    private val campaignId: String?,
    private val weakReferenceActivity: WeakReference<Activity>,
    private val campaignScope: CoroutineScope,
    val parent: ViewGroup
) : ResourceFetchCallback {
    private val downloadableResEntry = hashMapOf<URL, DOWNLOADED>()
    abstract fun animationType(): String
    abstract fun clearViewParser()
    abstract fun getWERenderedLayout(): View?

    /**
     * By default this renders the layout when there are no resources to download.
     *
     **/
    open suspend fun render() {
        if (!hasResources()) {
            renderLayout()
        }
    }

    /**
     * This function is called when there is a timeout or when activity is destroyed
     */
    private fun stopRenderAndSendCallback() {
        clearViewParser()
        stopAllDownloadListeners()
    }

    private fun stopAllDownloadListeners() {
        downloadableResEntry.forEach {
            //Cancel the downloads if needed.
            downloadableResEntry.remove(it.key)
        }
//        Logger.e(TAG, "stopAllDownloadListeners  RESOURCE_FETCHING_FAILED ")
        campaignScope.cancel(CancellationException(InlineExceptionType.RESOURCE_FETCHING_FAILED.name))
    }

    /**
     * This function will post res. success and check if all the res. are downloaded
     */
    override fun resourcedFetchedSuccess(url: String) {
        // Logger.d(TAG, "Add resourcedFetchedSuccess: $url")
        downloadableResEntry[url] = true
        checkAllResourceDownloadStatus()
    }

    /**
     * This function will post campaign failure if any of the res. returns failure.
     */
    override fun resourceFetchedFailure(url: String) {
        downloadableResEntry[url] = false
        //As the resource fetch return failure, stopRendering is called
        stopRenderAndSendCallback()
        // Logger.d("TAG", "resourceFetchedFailure for : $url for ${campaignId}")
    }

    /**
     * This function will keep a record of how many resources
     * needs to be downloaded with their status
     */
    override fun addResource(url: String) {
        // Logger.d(TAG, "addResource: $url")
        downloadableResEntry[url] = false
    }

    private fun checkAllResourceDownloadStatus() {
        val allResourceReady = downloadableResEntry.values.all { it }
        if (allResourceReady) {
            downloadableResEntry.forEach {
                downloadableResEntry.remove(it.key)
            }
        }
    }

    //Add custom view to the parent
    private fun renderLayout() {
        // Logger.d(TAG, "Logger for execution in renderLayout $campaignId")
        try {
            if (campaignScope.isActive) {
                campaignScope.launch(Dispatchers.Main) {
                    weakReferenceActivity.get()?.let {
                        val layoutView = getWERenderedLayout()
                        if (!it.isFinishing && layoutView != null) {
                            //parent.visibility = View.VISIBLE
                            drawLayout(layoutView)
                            parent.bringChildToFront(layoutView)
                        } else {
//                            Logger.e(TAG, "renderLayout In cancellation for $campaignId layoutView != null ${layoutView != null} it.isFinishing: ${it.isFinishing}")
                            campaignScope.cancel(CancellationException("Activity is finishing or layout is null $campaignId"))
                        }
                    }
                }
            }
        } catch (throwable: Throwable) {
            throwable.stackTrace
//            Logger.e(TAG, "startProcessingCampaign  CAMPAIGN_FETCHING_FAILED ? ${(throwable.message )}")
            campaignScope.cancel(CancellationException(throwable.message))
        }
    }

    private fun drawLayout(layoutView: View) {
        if (parent.layoutParams.height == -2) {
            parent.measure(
                parent.layoutParams.width,
                parent.layoutParams.height
            )
        }
        var initialHeight = parent.measuredHeight
        val persistedView = parent.findViewWithTag<View>(WE_VIEW_TAG)
//        if (persistedView != null) {
//            initialHeight -= (persistedView.marginTop + persistedView.marginBottom)
//        }
        //Adding View
        layoutView.tag = WE_VIEW_TAG
        parent.removeView(layoutView)
        layoutView.alpha = 0f
        parent.addView(layoutView)

        val finalHeight = if (parent.layoutParams.height == ActionBar.LayoutParams.WRAP_CONTENT) {
            layoutView.measure(
                parent.layoutParams.width,
                parent.layoutParams.height
            )
            layoutView.measuredHeight
        } else {
            parent.layoutParams.height
        }

        val anim = layoutView.animate().alpha(1f)
        anim.duration = (Animation.ANIMATION_TIME)
        if (initialHeight != finalHeight && animationType().equals("default",true)) {
            WEUtils.slideView(parent, layoutView, initialHeight, finalHeight)
        } else {
            layoutView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            parent.visibility = View.VISIBLE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            anim.setUpdateListener { value ->
                if (!campaignScope.isActive) {
                    anim.cancel()
                    clearViewParser()
                }
                if (value.animatedValue as Float >= 1f) {
                    persistedView?.let { _view ->
                        parent.removeView(_view)
                    }
                }
            }
        }
    }

    private fun hasResources(): Boolean {
        return (downloadableResEntry.size > 0)
    }

    //end region
}
