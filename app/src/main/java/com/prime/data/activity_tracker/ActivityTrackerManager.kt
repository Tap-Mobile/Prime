package com.prime.data.activity_tracker

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.di.AppScope
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
typealias ActivityTracker = Application.ActivityLifecycleCallbacks

open class BaseActivityTracker : ActivityTracker {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}

@AppScope
class ActivityTrackerManager @Inject constructor(
    private val trackers: Set<@JvmSuppressWildcards ActivityTracker>
) : ActivityTracker {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = trackers.forEach {
        it.onActivityCreated(activity, savedInstanceState)
    }

    override fun onActivityStarted(activity: Activity) = trackers.forEach {
        it.onActivityStarted(activity)
    }

    override fun onActivityResumed(activity: Activity) = trackers.forEach {
        it.onActivityResumed(activity)
    }

    override fun onActivityPaused(activity: Activity) = trackers.forEach {
        it.onActivityPaused(activity)
    }

    override fun onActivityStopped(activity: Activity) = trackers.forEach {
        it.onActivityStopped(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = trackers.forEach {
        it.onActivitySaveInstanceState(activity, outState)
    }

    override fun onActivityDestroyed(activity: Activity) = trackers.forEach {
        it.onActivityDestroyed(activity)
    }
}