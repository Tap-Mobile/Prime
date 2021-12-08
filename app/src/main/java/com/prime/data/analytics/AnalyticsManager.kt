package com.prime.data.analytics

import android.app.Activity
import android.os.Bundle
import com.prime.data.activity_tracker.ActivityTracker
import com.prime.data.analytics.providers.TimberAnalytics
import com.vpn.prime.BuildConfig
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Developed by
 * @author Aleksandr Artemov
 */
interface AnalyticsManager {
    fun logEvent(event: String, params: Map<String, Any>? = null)
}

@Singleton
class AnalyticsManagerImpl @Inject constructor(
    private val analytics: Set<@JvmSuppressWildcards AnalyticsProvider>
) : AnalyticsManager, ActivityTracker {

    init {
        Timber.i("Analytics enabled: ${analytics.joinToString(", ", "[", "]") { it::class.java.simpleName }}")
    }

    override fun logEvent(event: String, params: Map<String, Any>?) {
        analytics
            .filter { BuildConfig.DEBUG.not() || it is TimberAnalytics } // Do not send real events on debug build
            .forEach { it.logEvent(event, params) }
    }

    /**
     * ActivityTracker
     * */

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = analytics.forEach {
        (it as? ActivityTracker)?.onActivityCreated(activity, savedInstanceState)
    }

    override fun onActivityStarted(activity: Activity) = analytics.forEach {
        (it as? ActivityTracker)?.onActivityStarted(activity)
    }

    override fun onActivityResumed(activity: Activity) = analytics.forEach {
        (it as? ActivityTracker)?.onActivityResumed(activity)
    }

    override fun onActivityPaused(activity: Activity) = analytics.forEach {
        (it as? ActivityTracker)?.onActivityPaused(activity)
    }

    override fun onActivityStopped(activity: Activity) = analytics.forEach {
        (it as? ActivityTracker)?.onActivityStopped(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = analytics.forEach {
        (it as? ActivityTracker)?.onActivitySaveInstanceState(activity, outState)
    }

    override fun onActivityDestroyed(activity: Activity) = analytics.forEach {
        (it as? ActivityTracker)?.onActivityDestroyed(activity)
    }
}