package com.di.app.modules.prime

import com.di.AppScope
import com.prime.features.ads.AdsUserListener
import dagger.Module
import dagger.Provides

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Module
abstract class AdsModule {
    companion object {
        @Provides
        @AppScope
        fun provideAdsUserListener() = AdsUserListener { true }
    }
}