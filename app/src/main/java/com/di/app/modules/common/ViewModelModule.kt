package com.di.app.modules.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.protonvpn.android.utils.ViewModelFactory
import dagger.Binds
import dagger.MapKey
import dagger.Module
import kotlin.reflect.KClass
/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(vmFactory: ViewModelFactory): ViewModelProvider.Factory
}