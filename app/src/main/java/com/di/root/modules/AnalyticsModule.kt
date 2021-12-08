package com.di.root.modules

import com.prime.data.activity_tracker.ActivityTracker
import com.prime.data.analytics.AnalyticsManager
import com.prime.data.analytics.AnalyticsManagerImpl
import com.prime.data.analytics.AnalyticsProvider
import com.prime.data.analytics.providers.TimberAnalytics
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import javax.inject.Singleton

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Module
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun provideAnalyticsManager(analytics: AnalyticsManagerImpl): AnalyticsManager

    @Binds
    @IntoSet
    @Singleton
    abstract fun provideTimber(analytics: TimberAnalytics): AnalyticsProvider

    @Binds
    @IntoSet
    @Singleton
    abstract fun provideActivityTracker(analytics: AnalyticsManagerImpl): ActivityTracker
}
