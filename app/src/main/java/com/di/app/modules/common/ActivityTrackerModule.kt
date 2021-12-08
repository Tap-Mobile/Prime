package com.di.app.modules.common

import com.di.AppScope
import com.prime.data.activity_tracker.ActivityTracker
import com.prime.data.analytics.AnalyticsManagerImpl
import com.prime.features.navigation.data.NavigationScreensTracker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Module
abstract class ActivityTrackerModule {

    @Binds
    @IntoSet
    @AppScope
    abstract fun bindAnalyticsTracker(provider: AnalyticsManagerImpl): ActivityTracker

    @Binds
    @IntoSet
    @AppScope
    abstract fun bindNavigationAnalyticsTracker(provider: NavigationScreensTracker): ActivityTracker
}