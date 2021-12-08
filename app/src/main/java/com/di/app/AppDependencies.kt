package com.di.app

import android.content.Context
import com.prime.data.analytics.AnalyticsManager
import com.prime.data.analytics.AnalyticsManagerImpl

/**
 * Developed by
 * @author Aleksandr Artemov
 */
interface AppDependencies {
    fun context(): Context

    fun analytics(): AnalyticsManager

    fun analyticsManager(): AnalyticsManagerImpl
}