package com.di.app.modules.common

import com.di.AppScope
import com.prime.features.reviews.RateUsManager
import com.prime.features.reviews.RateUsManagerImpl
import dagger.Binds
import dagger.Module

/**
 * Developed by
 * @author Aleksandr Artemov
 */

@Module
abstract class RateUsModule {

    @Binds
    @AppScope
    abstract fun bindRateUsManager(rateUsManager: RateUsManagerImpl): RateUsManager
}