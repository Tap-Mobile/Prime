package com.di.app.modules.prime

import androidx.lifecycle.ViewModel
import com.di.app.modules.common.ViewModelKey
import com.prime.features.main.PrimeMainViewModel
import com.prime.features.servers.countries.PrimeCountriesViewModel
import com.prime.features.settings.support.info.PrimeSupportInfoViewModel
import com.prime.features.settings.support.message.PrimeSupportMessageViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Module
abstract class PrimeViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(PrimeCountriesViewModel::class)
    abstract fun bindsPrimeCountriesViewModel(viewModel: PrimeCountriesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PrimeMainViewModel::class)
    abstract fun bindsPrimeMainViewModel(viewModel: PrimeMainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PrimeSupportMessageViewModel::class)
    abstract fun bindsPrimeSupportMessageViewModel(viewModel: PrimeSupportMessageViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PrimeSupportInfoViewModel::class)
    abstract fun bindsPrimeSupportInfoViewModel(viewModel: PrimeSupportInfoViewModel): ViewModel
}