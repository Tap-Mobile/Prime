package com.di.root

import android.content.Context
import com.di.app.AppDependencies
import com.di.root.modules.AnalyticsModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Component(
    modules = [
        AnalyticsModule::class,
    ]
)
@Singleton
interface RootComponent : AppDependencies {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): RootComponent
    }
}