package com.prime.features.ads

import com.di.AppScope
import com.prime.data.keys.Keys.Ads.LAST_CONSENT_SHOWN
import com.protonvpn.android.utils.Storage
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@AppScope
class AdsStorage @Inject constructor(

) {
    fun getLastConsentShown(defValue: Long): Long = Storage.getLong(LAST_CONSENT_SHOWN, defValue)

    fun setLastConsentShown(value: Long) = Storage.saveLong(LAST_CONSENT_SHOWN, value)
}