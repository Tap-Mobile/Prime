package com.prime.features.ads

import com.di.AppScope
import com.prime.data.analytics.Analytics
import com.prime.data.analytics.AnalyticsManager
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@AppScope
class AdsAnalytics @Inject constructor(
    private val analytics: AnalyticsManager
) {
    fun logAdClicked(ad: String) = analytics.logEvent(
        Analytics.Ads.Key.CLICKED,
        mapOf(Analytics.Ads.Param.AD_TYPE to ad)
    )

    fun logAdOpened(ad: String) = analytics.logEvent(
        Analytics.Ads.Key.OPENED,
        mapOf(Analytics.Ads.Param.AD_TYPE to ad)
    )
}