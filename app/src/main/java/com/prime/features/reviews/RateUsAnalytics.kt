package com.prime.features.reviews

import com.di.AppScope
import com.prime.data.analytics.Analytics
import com.prime.data.analytics.AnalyticsManager
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@AppScope
class RateUsAnalytics @Inject constructor(
    private val analytics: AnalyticsManager
) {
    fun logRatedToPlayStore() = analytics.logEvent(Analytics.RateUs.Key.RATED_AND_OPENED_STORE)
}