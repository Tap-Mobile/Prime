package com.di

import android.app.Application
import com.di.app.AppComponent
import com.di.app.DaggerPrimeAppComponent
import com.di.root.DaggerRootComponent
import com.di.root.RootComponent

/**
 * Developed by
 * @author Aleksandr Artemov
 */

object AppInjector {

    lateinit var rootComponent: RootComponent
    val appComponent: AppComponent
        get() = getComponent(AppComponent::class.java)

    private val componentMap: MutableMap<Class<out Any>, Any> = mutableMapOf()

    fun init(app: Application): AppInjector {
        rootComponent = DaggerRootComponent
            .builder()
            .context(app)
            .build()

        return this
    }

    private val appComponentBuilder: AppComponent.Builder
        get() = DaggerPrimeAppComponent.builder()

    private fun loadComponent(component: Class<out Any>) =
        when (component) {
            AppComponent::class.java -> installComponent(component) {
                appComponentBuilder
                    .appDependencies(rootComponent)
                    .build()
            }

            else -> throw IllegalStateException("Unknown component: $component")
        }

    @Suppress("UNCHECKED_CAST")
    fun <Component : Any> getComponent(componentClass: Class<Component>): Component {
        return if (componentMap.containsKey(componentClass)) {
            componentMap[componentClass] as Component
        } else {
            loadComponent(componentClass) as Component
        }
    }

    private fun installComponent(component: Class<out Any>, install: () -> Any) =
        install().also {
            componentMap[component] = it
        }

    fun clearComponent(component: Class<out Any>) {
        componentMap.remove(component)
    }
}
