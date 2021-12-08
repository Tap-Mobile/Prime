package com.di.app.modules.prime

import android.app.Activity
import com.prime.features.main.PrimeMainFragment
import com.prime.features.servers.cities.PrimeCitiesFragment
import com.prime.features.servers.countries.PrimeCountriesFragment
import com.prime.features.settings.menu.PrimeMenuFragment
import com.prime.features.settings.support.info.PrimeSupportInfoFragment
import com.prime.features.settings.support.message.PrimeSupportMessageFragment
import com.prime.ui.PrimeMainActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PrimeMainActivityModule {

    @Binds
    abstract fun provideActivity(activity: PrimeMainActivity): Activity

    @ContributesAndroidInjector
    abstract fun bindMain(): PrimeMainFragment

    @ContributesAndroidInjector
    abstract fun bindCountries(): PrimeCountriesFragment

    @ContributesAndroidInjector(modules = [PrimeCitiesModule::class])
    abstract fun bindCities(): PrimeCitiesFragment

    @ContributesAndroidInjector
    abstract fun bindMenu(): PrimeMenuFragment

    @ContributesAndroidInjector
    abstract fun bindSupportInfo(): PrimeSupportInfoFragment

    @ContributesAndroidInjector
    abstract fun bindSupportMessage(): PrimeSupportMessageFragment
}
