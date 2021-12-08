package com.prime.features.ads

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.di.AppScope
import com.prime.data.DebugKeys.ForceAds
import com.prime.data.DebugKeys.ForceNoAds
import com.prime.data.debugConfig
import com.prime.data.keys.AppKeyStorage
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@AppScope
class AdsManager @Inject constructor(
    private val user: AdsUserListener,
    appStorage: AppKeyStorage,
) {
    companion object {
        private val LOG_TAG = AdsManager::class.java.simpleName

        private const val ADS_FEATURE_DISABLED = true
    }

    private val isFirstOpen = appStorage.isAppOpenedFirstTime
    val adsDisabled: Boolean
        get() = ADS_FEATURE_DISABLED
            || (debugConfig(ForceAds).not()
            && (debugConfig(ForceNoAds) || isFirstOpen || user.isPremium))

    private var activity: WeakReference<FragmentActivity?>? = null
    private var lastAdTime: Long = -1
    private var lastLoadingTryTime: Long = -1

    val isAdLoaded get() = adsDisabled.not()

    init {
        load()
    }

    fun attachActivity(activity: FragmentActivity?) {
        this.activity = WeakReference(activity)
        load()
    }

    fun detachActivity(activity: Activity) {
        if (this.activity?.get() == activity) {
            this.activity?.clear()
            this.activity = null
        }
    }

    private fun load() {
        if (adsDisabled) return

        if (user.isPremium.not()) {
            forceLoad()
        }
    }

    private fun forceLoad() {
        Timber.tag(LOG_TAG).i("load ad")
        lastLoadingTryTime = System.currentTimeMillis()
    }

    fun show(onSplash: Boolean, activity: FragmentActivity?): Boolean {
        if (adsDisabled || onSplash && isFirstOpen) return false

        val diff = System.currentTimeMillis() - lastAdTime
        Timber.tag(LOG_TAG).i("show ads diff %s", diff)

        if (user.isPremium.not()) {
            Timber.tag(LOG_TAG).w("show ads... %s", onSplash)
            return true
        }
        return false
    }

}
