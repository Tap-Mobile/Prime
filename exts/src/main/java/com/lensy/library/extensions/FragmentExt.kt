package com.lensy.library.extensions

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Developed by
 * @author Aleksandr Artemov
 */

/**
 * A lazy property that gets cleaned up when the fragment's view is destroyed.
 */
class AutoClearedValue<T : Any>(val fragment: Fragment, val clear: (T.() -> Unit)? = null) :
    ReadWriteProperty<Fragment, T> {
    private var _value: T? = null

    init {
        fragment.observeViewLifecycle(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                _value?.let { clear?.invoke(it) }
                _value = null
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return _value ?: throw IllegalStateException(
            "should never call auto-cleared-value get when it might not be available"
        )
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        _value = value
    }
}

class AutoLifecycleValue<T : Any>(val fragment: Fragment, val initializer: () -> T, val cleaner: (T.() -> Unit)? = null) :
    ReadOnlyProperty<Fragment, T> {
    private var _value: T? = null

    init {
        fragment.observeViewLifecycle(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                _value = initializer()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                clear()
                _value = null
            }
        })
    }

    private fun clear() {
        _value?.let { cleaner?.invoke(it) }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T =
        _value ?: throw IllegalStateException(
            "should never call lifecycle-value get when it might not be available"
        )
}

fun <T : Any> Fragment.autoCleared(clear: (T.() -> Unit)? = null) = AutoClearedValue(this, clear)

fun <T : Any> Fragment.autoLifecycle(initializer: () -> T) = autoLifecycle(initializer, null)

fun <T : Any> Fragment.autoLifecycle(initializer: () -> T, cleaner: (T.() -> Unit)? = null) =
    AutoLifecycleValue(this, initializer, cleaner)

fun Fragment.changeNavigationBarColor(@ColorRes colorRes: Int) {
    requireActivity().window.navigationBarColor = ContextCompat.getColor(requireContext(), colorRes)
}

fun Fragment.onBackPressed(block: () -> Unit) =
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                block()
            }
        })

inline fun <reified Provider, Dep> Fragment.requireParentToProvide(block: Provider.() -> Dep): Dep {
    return if (parentFragment is Provider) {
        (parentFragment as Provider).block()
    } else {
        throw IllegalArgumentException("parent fragment has to implements ${Provider::class.simpleName}")
    }
}

inline fun <reified Provider, Dep> Fragment.requireActivityToProvide(block: Provider.() -> Dep): Dep {
    val activity = requireActivity()
    return if (activity is Provider) {
        activity.block()
    } else {
        throw IllegalArgumentException("activity has to implements ${Provider::class.simpleName}")
    }
}

fun Fragment.observeViewLifecycle(@NonNull lifecycleObserver: LifecycleObserver) {
    val fragment = this
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                viewLifecycleOwner?.lifecycle?.addObserver(lifecycleObserver)
            }
        }
    })
}

fun Fragment.enableFullScreen() = activity?.enableFullScreen()