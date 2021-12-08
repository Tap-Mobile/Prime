package com.prime.data

import com.di.AppScope
import com.prime.data.activity_tracker.ActivityTrackerManager
import com.prime.data.analytics.AnalyticsManager
import javax.inject.Inject

/**
 * Class to Inject it to Application to trigger all necessary dependencies initializations on app's launch
 *
 * Developed by
 * @author Aleksandr Artemov
 */
@AppScope
class AppDependenciesLauncher @Inject constructor(
    private val analytics: AnalyticsManager,
    val activityTracker: ActivityTrackerManager
) {

    fun onCreateApp() {
    }
}