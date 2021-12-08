package com.lensy.library.extensions

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Developed by
 * @author Aleksandr Artemov
 */

class AutoClearedValueInActivity<T : Any>(val activity: AppCompatActivity, val clear: (T.() -> Unit)? = null) :
    ReadWriteProperty<AppCompatActivity, T> {
    private var _value: T? = null

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                _value?.let { clear?.invoke(it) }
                _value = null
            }
        })
    }

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        return _value ?: throw IllegalStateException(
            "should never call auto-cleared-value get when it might not be available"
        )
    }

    override fun setValue(thisRef: AppCompatActivity, property: KProperty<*>, value: T) {
        _value = value
    }
}

fun <T : Any> AppCompatActivity.autoCleared(clear: (T.() -> Unit)? = null) = AutoClearedValueInActivity(this, clear)

val FragmentActivity.currentNavigationFragment: Fragment?
    get() = supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.first()

fun FragmentActivity.findFragmentIn(container: Int): Fragment? =
    supportFragmentManager.findFragmentById(container)

fun Activity.enableFullScreen() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

fun <T : Any> AppCompatActivity.autoLifecycle(initializer: () -> T) = autoLifecycle(initializer, null)

fun <T : Any> AppCompatActivity.autoLifecycle(initializer: () -> T, cleaner: (T.() -> Unit)? = null) =
    ActivityAutoLifecycleValue(this, initializer, cleaner)

class ActivityAutoLifecycleValue<T : Any>(val activity: AppCompatActivity, val initializer: () -> T, val cleaner: (T.() -> Unit)? = null) :
    ReadOnlyProperty<AppCompatActivity, T> {
    private var _value: T? = null

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                _value = initializer()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                _value?.let { cleaner?.invoke(it) }
                _value = null
            }
        })
    }

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T =
        _value ?: throw IllegalStateException(
            "should never call lifecycle-value get when it might not be available"
        )
}
