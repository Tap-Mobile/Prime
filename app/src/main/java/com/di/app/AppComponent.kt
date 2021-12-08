package com.di.app

import com.PrimeApp
import com.di.AppScope
import com.di.app.modules.common.ActivityBuilder
import com.di.app.modules.common.ActivityTrackerModule
import com.di.app.modules.common.AppModule
import com.di.app.modules.common.RateUsModule
import com.di.app.modules.common.ServiceBuilder
import com.di.app.modules.common.SplashModule
import com.di.app.modules.common.ViewModelModule
import com.di.app.modules.prime.AdsModule
import com.di.app.modules.prime.PrimeActivityBuilder
import com.di.app.modules.prime.PrimeMainActivityModule
import com.di.app.modules.prime.PrimeViewModelModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication

/**
 * Developed by
 * @author Aleksandr Artemov
 */

interface AppComponent : AndroidInjector<DaggerApplication> {

    fun inject(app: PrimeApp)

    interface Builder {
        fun appDependencies(dependencies: AppDependencies): Builder

        fun build(): AppComponent
    }
}

@AppScope
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBuilder::class,
        PrimeActivityBuilder::class,
        AppModule::class,
        PrimeMainActivityModule::class,
        SplashModule::class,
        ActivityTrackerModule::class,
        RateUsModule::class,
        PrimeViewModelModule::class,
        ViewModelModule::class,
        ServiceBuilder::class,
        AdsModule::class,
    ],
    dependencies = [
        AppDependencies::class,
    ]
)
interface PrimeAppComponent : AppComponent {

    @Component.Builder
    interface Builder : AppComponent.Builder
}
