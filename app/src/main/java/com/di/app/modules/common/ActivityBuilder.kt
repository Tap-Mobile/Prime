package com.di.app.modules.common

import com.prime.ui.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [SplashModule::class])
    abstract fun bindSplashActivity(): SplashActivity
}