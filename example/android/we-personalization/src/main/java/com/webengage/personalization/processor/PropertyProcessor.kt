package com.webengage.personalization.processor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.JsonReader
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.webengage.personalization.BuildConfig
import com.webengage.personalization.callbacks.EventDispatcher
import com.webengage.personalization.callbacks.WECallbackDispatcher
import com.webengage.personalization.callbacks.WECampaignViewClickInternalCallback
import com.webengage.personalization.data.WECampaignData
import com.webengage.personalization.data.WELayoutDataTypeAdapter
import com.webengage.personalization.data.network.NetworkProcessor
import com.webengage.personalization.renderer.renderEngine.DynamicRenderEngine
import com.webengage.personalization.renderer.renderEngine.RenderEngine
import com.webengage.personalization.renderer.renderEngine.StaticRenderEngine
import com.webengage.personalization.utils.*
import com.webengage.personalization.utils.Animation.ANIMATION_TIME
import com.webengage.personalization.utils.WEUtils.Companion.isInViewport
import com.webengage.sdk.android.Logger
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.StringReader
import java.lang.ref.WeakReference

class PropertyProcessor(
    private val activityWeakReference: WeakReference<Activity>,
    private val propertyDetail: HashMap<String, Any>,
    private val systemData: HashMap<String, Any>
) : WECampaignViewClickInternalCallback {
    private val campaignScope = CoroutineScope(Job())
    private var targetParent: ViewGroup? = null
    private var targetViewInPort = false
    private var campaignData: WECampaignData? = null
    private var renderEngine: RenderEngine? = null
    lateinit var defaultContentScope: CoroutineScope
    private var propertyId: String
    private var targetViewId: String
    private lateinit var variationId: String
    private var debugModeLapseTime: Int
    private var BASE_URL: String

    init {
        val campaignObject = propertyDetail["campaign"] as HashMap<String, Any>?
        propertyId = propertyDetail["p_id"] as String
        targetViewId = propertyDetail["targetView"] as String
        BASE_URL = systemData["base_url"] as String
        debugModeLapseTime = if (BuildConfig.BUILD_TYPE.equals("debug", true)) {
            5000
        } else {
            0
        }
        startProcessingProperty(campaignObject);
    }

    private fun startProcessingProperty(campaignObject: HashMap<String, Any>?) {
        val id: Int = activityWeakReference.get()!!.resources.getIdentifier(
            targetViewId, "id", activityWeakReference.get()!!.packageName
        )
        if (id != 0) {
            targetParent =
                activityWeakReference.get()!!.window.decorView.rootView.findViewById<ViewGroup>(
                    id
                )
            Logger.d(
                TAG,
                "PropertyProcessor -> startProcessingProperty campaignObject is campaignObject == null ?  " +
                        "${campaignObject == null}  propertyDetail = ${propertyDetail["experiment_id"]}"
            )
            if (null != targetParent) {
                when {
                    null != campaignObject -> {
                        startProcessingCampaign(campaignObject)
                    }
                    propertyDetail["content"] != null -> {
                        //Try for default data rendering.
                        renderDefaultContent(propertyDetail["experiment_id"] as String)
                    }
                    else -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            clearWEViewIfPresent()
                        }
                    }
                }
            } else {
                handleException(InlineExceptionType.TARGET_PROPERTY_MISSING.name)
            }
        } else {
            Logger.d(
                TAG,
                "Inline error message startProcessingProperty throwing error $targetParent"
            )
            handleException(InlineExceptionType.TARGET_PROPERTY_MISSING.name)
        }
    }

    private fun clearWEViewIfPresent() {
        val layoutView = targetParent?.findViewWithTag<ViewGroup>(WE_VIEW_TAG)
        layoutView?.let {
//            targetParent?.removeView(it)
//            targetParent?.visibility = View.GONE
//            val anim = it.animate().alpha(1f)
//            anim.duration = (2000)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                anim.setUpdateListener { value ->
//                    val alphaV = 1f.minus(value.animatedValue as Float)
//                    it.alpha = alphaV
//                    if (value.animatedValue as Float == 0f) {
//                        targetParent?.removeView(it)
//                        targetParent?.visibility = View.GONE
//                    }
//                }
//            }
//            anim.start()
            WEUtils.slideView(targetParent!!, it, it.measuredHeight, 0)
            handleException(InlineExceptionType.NON_QUALIFIED_CAMPAIGN_CLEARED.name)
        }
    }

    /**
     * This take care of the campaign running on the target view id.
     * It will fetch the data from P13 and then render it.
     * **/
    private fun startProcessingCampaign(campaignObject: HashMap<String, Any>) {
        val campaignId = campaignObject["notificationEncId"].toString()
        // Fetch campaign data w.r.t variation id
        val failureTimeout =
            if (campaignObject["failureTimeout"] == null) 2000L else campaignObject["failureTimeout"].toString()
                .toLong()
        val campaignJob = campaignScope.launch(Dispatchers.IO) {
            withTimeout(
                failureTimeout + ANIMATION_TIME
            ) {
                attachDetachedListener(campaignId)
                val campaignData =
                    campaignScope.async {
                        variationId = propertyDetail["variationId"].toString()
                        val cuid = systemData["cuid"] as String
                        val luid = systemData["luid"] as String
                        val params: String? =
                            if (propertyDetail["params"] != null) propertyDetail["params"] as String else null
                        NetworkProcessor.getCampaignData(
                            WeakReference(activityWeakReference.get()!!.applicationContext),
                            variationId, luid, cuid, BASE_URL, params
                        )
                    }.await()
                if (campaignData.equals(InlineExceptionType.CAMPAIGN_FETCHING_FAILED.name)) {
                    Logger.e(TAG, "startProcessingCampaign  CAMPAIGN_FETCHING_FAILED ? ${(campaignData )}")
                    campaignScope.cancel(CancellationException(campaignData))
                } else {
                    renderEngine = withContext(campaignScope.coroutineContext + Dispatchers.Main) {
                        prepareForRenderingUI(campaignId, campaignData, campaignScope)
                    }
                }
            }
        }
        campaignJob.invokeOnCompletion { cause: Throwable? ->
            Logger.d(
                TAG,
                "Property Processing complete for property: $targetViewId $campaignId "
            )

            if (cause != null) {
                Logger.d(
                    TAG,
                    "Process Campaign invokeOnCompletion Throwable with cause: $cause $campaignId"
                )
                CoroutineScope(Dispatchers.Main).launch {
                    if (cause is TimeoutCancellationException) {
                        handleException(InlineExceptionType.CAMPAIGN_RENDER_TIMEOUT.name)
                    } else {
                        handleException(cause.localizedMessage)
                    }
                }
                if (campaignJob.isCancelled
                    && (renderEngine?.getWERenderedLayout() == null)
                ) {
                    renderDefaultContent(campaignId)
                }
            } else {
                if (!campaignJob.isCancelled) {
                    checkAndAttachListeners(campaignId)
                } else {
                    renderDefaultContent(campaignId)
                }
            }
        }
    }

    private fun renderDefaultContent(campaignId: String) {
        Logger.d(TAG, "renderDefaultContent for $campaignId")
        if (propertyDetail["content"] != null) {
            defaultContentScope = CoroutineScope(Job())
            val defaultContentJob = defaultContentScope.launch(Dispatchers.Main) {
                prepareForRenderingUI(campaignId, getDefaultData(), defaultContentScope)
            }

            defaultContentJob.invokeOnCompletion { cause: Throwable? ->
                if (cause != null) {
                    Logger.d(
                        TAG,
                        "render Default Content of Campaign  Throwable with cause: $cause $campaignId"
                    )
                } else {
                    if (!defaultContentJob.isCancelled) {
                        Logger.d(TAG, "Default Content DISPLAYED $campaignId")
                        checkAndAttachListeners(campaignId)
                    } else {
                        Logger.d(
                            TAG,
                            "Campaign renderDefaultContent $campaignId has been CANCELLED"
                        )

                    }
                }
            }
        } else {
            //handleException(InlineExceptionType.)
        }
    }

    private suspend fun prepareForRenderingUI(
        campaignId: String,
        data: String? = null,
        scope: CoroutineScope
    ): RenderEngine? {
        Logger.d(TAG, "prepareForRenderingUI campaignId $campaignId")
        Logger.d(TAG, "prepareForRenderingUI targetViewId $targetViewId")
        if (data == null) {
            scope.cancel(CancellationException(InlineExceptionType.CAMPAIGN_FETCHING_FAILED.name))
            return null
        } else {
            val jsonReader = JsonReader(StringReader(data))
            if (targetParent != null)
                campaignData = WELayoutDataTypeAdapter().read(jsonReader, targetViewId)
            else
                scope.cancel(CancellationException(InlineExceptionType.TARGET_PROPERTY_MISSING.name))
        }
        campaignData?.let {
            campaignData?.campaignId = campaignId
            campaignData?.propertyId = propertyId
            campaignData?.variationId = variationId

            WECallbackDispatcher.onDataReceived(campaignData!!)
            val modifiedData =
                WECallbackDispatcher.onCampaignPrepared(
                    campaignId,
                    campaignData!!
                )
            if (it.isDataOption) {
                return null
            }
            if (modifiedData != null) {
                campaignData = modifiedData
            }

            if (campaignData!!.shouldRender) {
                return render(campaignId, campaignData!!, scope)
            } else {
                Logger.e(TAG, "prepareForRenderingUI  ShouldRender flag has been set to false ")
                scope.cancel(CancellationException("ShouldRender flag has been set to false for $campaignId"))
            }
        }
        return null
    }

    private suspend fun render(
        campaignId: String?,
        _campaignData: WECampaignData,
        coroutineScope: CoroutineScope
    ): RenderEngine? {
        Logger.d(TAG, "PropertyProcessor render campaignId $campaignId")
        /*Logger.d(TAG, "PropertyProcessor render isDataOption ${_campaignData.isDataOption}")
        Logger.d(TAG, "PropertyProcessor render content ${_campaignData.content}")
        Logger.d(TAG, "PropertyProcessor render parserType ${_campaignData.parserType}")
        Logger.d(TAG, "PropertyProcessor render shouldRender ${_campaignData.shouldRender}")
        Logger.d(TAG, "PropertyProcessor render shouldRender ${_campaignData.campaignId}")
        Logger.d(TAG, "PropertyProcessor render shouldRender ${_campaignData.propertyId}")
        Logger.d(TAG, "PropertyProcessor render targetViewId ${_campaignData.targetViewId}")
        Logger.d(TAG, "PropertyProcessor render variationId ${_campaignData.variationId}")*/
        var renderEngine: RenderEngine? = null
        if (!_campaignData.parserType.isNullOrEmpty()) {
            _campaignData.content?.let {
                if (JsonKeys.STATIC.equals(_campaignData.parserType, ignoreCase = true)) {
                    renderEngine = StaticRenderEngine(
                        campaignId,
                        activityWeakReference,
                        targetParent!!,
                        _campaignData.content!!, coroutineScope
                    )

                } else if (JsonKeys.DYNAMIC.equals(
                        _campaignData.parserType,
                        ignoreCase = true
                    )
                ) {
                    renderEngine = DynamicRenderEngine(
                        campaignId,
                        activityWeakReference, targetParent!!,
                        _campaignData.content!!, campaignScope
                    )
                }
            }
            renderEngine?.render()
        }
        return renderEngine
    }

    private fun getDefaultData(): String? {
        val contentMap = propertyDetail["content"]
        return if (null != contentMap) {
            variationId = propertyDetail["id"] as String
            val defaultContent = mutableMapOf<String, Any>()
            defaultContent["parser_type"] = JsonKeys.STATIC as Any
            defaultContent["target_view"] = targetViewId as Any
            defaultContent["content"] = contentMap as Any
            val defaultCampaignData = JSONObject(defaultContent as Map<*, *>)
            defaultCampaignData.toString()
        } else {
            null
        }
    }

    /**
     * Attaching detached listener on target view
     * **/
    private fun attachDetachedListener(campaignId: String) {
        targetParent?.let {
            it.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(p0: View) {
                }

                override fun onViewDetachedFromWindow(p0: View) {
                    Logger.d(TAG, "prepareForRenderingUI Target View detached from window " +
                            "renderEngine != null is ${renderEngine != null} " +
                            " TARGET_PROPERTY_MISSING campaignScope.isActive = ${campaignScope.isActive} ")

                    if (campaignScope.isActive) {
                        campaignScope.cancel(CancellationException(InlineExceptionType.TARGET_PROPERTY_MISSING.name))
                    }
                    renderEngine?.clearViewParser()
                    renderEngine = null
                    WECallbackDispatcher.unregisterCampaignViewClickInternalCallback(campaignId)
                }
            })
        }
    }

    private fun checkAndAttachListeners(campaignId: String) {
        WECallbackDispatcher.registerCampaignViewClickInternalCallback(
            campaignId,
            this@PropertyProcessor
        )
        WECallbackDispatcher.onRendered(campaignData!!)
        try {
            //Impression Listener
            renderEngine?.getWERenderedLayout()?.let {
                if (!it.viewTreeObserver.isAlive) {
                    return
                }
                it.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        //check whether the view is already in viewport
                        targetViewInPort = it.isInViewport()
                        if (targetViewInPort) {
                            WECallbackDispatcher.onCampaignShown(
                                propertyId,
                                variationId,
                                campaignId,
                                campaignData!!
                            )
                        } else {
                            //Attach a listener for impression
                            if (it.viewTreeObserver != null && it.viewTreeObserver.isAlive) {
                                var scrollChangedListener: ViewTreeObserver.OnScrollChangedListener? =
                                    null
                                scrollChangedListener = ViewTreeObserver.OnScrollChangedListener {
                                    val inViewport = it.isInViewport()
                                    if (targetViewInPort != inViewport) {
                                        if (inViewport && !targetViewInPort) {
                                            targetViewInPort = true
                                            WECallbackDispatcher.onCampaignShown(
                                                propertyId,
                                                variationId,
                                                campaignId,
                                                campaignData!!
                                            )
                                            it.viewTreeObserver.removeOnScrollChangedListener(
                                                scrollChangedListener
                                            )
                                        }
                                    }
                                }
                                it.viewTreeObserver.addOnScrollChangedListener(scrollChangedListener)
                            } else {
                                Logger.e(TAG, "Target view onViewAttachedToWindow not alive for $targetViewId")
                            }

                        }
                    }

                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (targetParent == null) {
            Logger.d("TAG", "Found targetParent is null for $targetViewId")
        }
    }

    override fun handleCampaignViewClicked(campaignId: String, deepLink: String, actionId: String) {
        val isClickHandledByClient =
            WECallbackDispatcher.onCampaignClicked(
                propertyId,
                variationId,
                actionId,
                deepLink,
                campaignData!!
            )
        if (!isClickHandledByClient) {
            handleActionURL(deepLink)
        }
    }

    private fun handleActionURL(deepLink: String) {
        try {
            val context = activityWeakReference.get()?.applicationContext
            val uri = Uri.parse(deepLink)
            val deeplinkIntent = Intent(Intent.ACTION_VIEW, uri)
            deeplinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (context != null) {
                val resolveInfoList: List<ResolveInfo> = context.packageManager
                    .queryIntentActivities(deeplinkIntent, PackageManager.MATCH_DEFAULT_ONLY)
                if (resolveInfoList.isNotEmpty()) {
                    for (resolveInfo in resolveInfoList) {
                        if (resolveInfo.activityInfo != null && context.packageName == resolveInfo.activityInfo.packageName) {
                            deeplinkIntent.setPackage(context.packageName)
                        }
                        break
                    }
                    context.startActivity(deeplinkIntent)
                }
            }
        } catch (e: Exception) {
            Logger.d(
                TAG,
                "Error occurred while handleActionURL for deeplink: $deepLink " + e.printStackTrace()
            )
        }
    }


    /**
     * - Resource fetching failed. Default/Callback triggered
     * - Campaign failed to render in set time. Default/Callback triggered
     * - Target property missing. Callback triggered
     * - Campaign fetching failed. Default/Callback triggered
     */
    private fun handleException(exceptionType: String?) {
        var message = ""
        message = when (exceptionType) {
            InlineExceptionType.RESOURCE_FETCHING_FAILED.name -> {
                "Resource fetching failed."
            }
            InlineExceptionType.CAMPAIGN_FETCHING_FAILED.name -> {
                "Campaign fetching failed."
            }
            InlineExceptionType.CAMPAIGN_RENDER_TIMEOUT.name -> {
                "Campaign failed to render in set time."
            }
            InlineExceptionType.TARGET_PROPERTY_MISSING.name -> {
                "Target property missing."
            }
            InlineExceptionType.NON_QUALIFIED_CAMPAIGN_CLEARED.name -> {
                "Campaign cleared from non qualified property."
            }
            else -> {
                "Error occurred"
            }
        }
        Logger.d(TAG, "PropertyProcessor handleException exceptionType: $exceptionType ")
        val isDefaultContentAvailable: Boolean = propertyDetail["content"] != null
        if (WECallbackDispatcher.hasCampaignCallbacksAttached() && isDefaultContentAvailable) {
            message += " Default and Callback triggered"
        } else if (WECallbackDispatcher.hasCampaignCallbacksAttached() && !isDefaultContentAvailable) {
            message += " Callback triggered"
        } else if (!WECallbackDispatcher.hasCampaignCallbacksAttached() && isDefaultContentAvailable) {
            message += " Default triggered"
        }
        var campaignId: String? = null
        propertyDetail["campaign"]?.let {
            campaignId =
                (it as HashMap<*, *>)["notificationEncId"] as String?
        }
        var journeyId =""
        propertyDetail["journeyId"]?.let {
            journeyId =
                (it as HashMap<*, *>)["journeyId"] as String
        }

        val variationId: String? = propertyDetail["variationId"] as String?
        WECallbackDispatcher.onCampaignException(
            campaignId, variationId,
            propertyId, targetViewId, Exception(message)
        )
        WECallbackDispatcher.onPlaceholderException(
            campaignData?.campaignId,
            targetViewId,
            Exception(message)
        )
        EventDispatcher.trackExceptionOccurred(
            campaignId, variationId, propertyId,
            targetViewId,journeyId, message
        )
    }
}