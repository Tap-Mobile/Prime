package com.prime.features.servers.core

import com.di.AppScope
import com.prime.data.analytics.Analytics
import com.prime.data.analytics.AnalyticsManager
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@AppScope
class ServersAnalytics @Inject constructor(
    private val analytics: AnalyticsManager
) {
    fun logServerSelected(name: String) = analytics.logEvent(
        Analytics.Servers.Key.COUNTRY_SELECTED,
        mapOf(Analytics.Servers.Param.NAME to name)
    )
}