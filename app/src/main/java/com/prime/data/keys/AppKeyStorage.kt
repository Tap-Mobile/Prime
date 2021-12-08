package com.prime.data.keys

import com.di.AppScope
import com.protonvpn.android.utils.Storage
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@AppScope
class AppKeyStorage @Inject constructor(
) {
    val isAppOpenedFirstTime by lazy(NONE) {
        Storage.getBoolean(Keys.App.OPENED_FIRST_TIME, true)
    }

    var isNeedShowWelcome: Boolean
        get() = isAppOpenedFirstTime && Storage.getBoolean(Keys.App.WELCOME_SHOWN, true)
        set(value) { Storage.saveBoolean(Keys.App.WELCOME_SHOWN, value) }

    fun setAppOpened() {
        Storage.saveBoolean(Keys.App.OPENED_FIRST_TIME, false)
    }
}