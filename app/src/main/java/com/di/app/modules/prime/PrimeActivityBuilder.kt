package com.di.app.modules.prime

import com.prime.features.iap.presentation.PrimeIapActivity
import com.prime.features.iap.presentation.onboarding.PrimeOnboardingActivity
import com.prime.ui.PrimeMainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Module
abstract class PrimeActivityBuilder {
    @ContributesAndroidInjector(modules = [PrimeMainActivityModule::class])
    abstract fun bindMainActivity(): PrimeMainActivity

    @ContributesAndroidInjector
    abstract fun bindPrimeOnboardingActivity(): PrimeOnboardingActivity

    @ContributesAndroidInjector
    abstract fun bindPrimeIapActivity(): PrimeIapActivity
}