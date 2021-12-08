package com.prime.features.navigation.data

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.di.AppScope
import com.prime.data.activity_tracker.ActivityTracker
import timber.log.Timber
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@AppScope
class NavigationScreensTracker @Inject constructor(
    private val analytics: NavigationAnalytics
) : ActivityTracker {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                .registerFragmentLifecycleCallbacks(
                    object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                            super.onFragmentStarted(fm, f)

                            logFragment(f)
                        }
                    }, true
                )
        }
    }

    override fun onActivityStarted(activity: Activity) {

    }

    private fun logFragment(f: Fragment) = when (f) {
      //  is IapFragment -> analytics.logIapScreen()

        else -> Timber.w("Fragment wasn't added to NavigationScreensTracker $f")
    }

    /**
     * Unused methods
     * */

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
}