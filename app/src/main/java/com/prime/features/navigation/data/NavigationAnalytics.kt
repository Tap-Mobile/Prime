package com.prime.features.navigation.data

import com.di.AppScope
import com.prime.data.analytics.Analytics
import com.prime.data.analytics.AnalyticsManager
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@AppScope
class NavigationAnalytics @Inject constructor(
    private val analytics: AnalyticsManager
) {

    /**
     * ----------------------------------   SCREENS   -------------------------------------------------//
     *
     */
    fun logIapScreen(screen: String) = analytics.logEvent(
        Analytics.Navigation.Key.Screen.IAP, mapOf(Analytics.Navigation.Param.SCREEN to screen)
    )

    /**
     * ----------------------------------   DIALOGS   -------------------------------------------------//
     *
     */
}