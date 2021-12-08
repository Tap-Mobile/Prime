package com.prime.data

import timber.log.Timber

/**
 * Developed by
 * @author Aleksandr Artemov
 */

object AppCrashlytics {
    fun recordException(ex: Throwable?, log: Boolean = true) {
        if (ex != null) {
            if (log) {
                Timber.e(ex)
            }
            //FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    fun setString(key: String, value: String) {
        //FirebaseCrashlytics.getInstance().setCustomKey(key, value)
    }
}