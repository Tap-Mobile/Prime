package com.prime.data

import com.vpn.prime.BuildConfig

/**
 * Developed by
 * @author Aleksandr Artemov
 */
sealed class DebugKeys {
    sealed class Iap : DebugKeys() {
        object ForcePremium : Iap()
        object UseFakeBilling : Iap()
    }

    object ForceRateUs : DebugKeys()

    sealed class Vpn :DebugKeys() {
        object UseFakeServers : Vpn()
    }

    object ForceAds : DebugKeys()
    object ForceNoAds : DebugKeys()
}

object DebugAppConfig {
    private val FORCE_CONFIG: Boolean = BuildConfig.DEBUG

    //always returns false for release build
    fun get(key: DebugKeys) = FORCE_CONFIG && when (key) {
        is DebugKeys.Iap -> when (key) {
            DebugKeys.Iap.ForcePremium -> false
            DebugKeys.Iap.UseFakeBilling -> false
        }
        DebugKeys.ForceRateUs -> false
        DebugKeys.Vpn.UseFakeServers -> false
        DebugKeys.ForceAds -> false
        DebugKeys.ForceNoAds -> false
    }
}

fun debugConfig(key : DebugKeys) = DebugAppConfig.get(key)