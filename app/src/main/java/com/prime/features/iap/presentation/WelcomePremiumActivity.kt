package com.prime.features.iap.presentation

import android.content.Context
import android.content.Intent
import com.prime.data.keys.AppKeyStorage
import com.prime.features.iap.presentation.onboarding.PrimeOnboardingActivity
import com.prime.ui.PrimeMainActivity
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
abstract class WelcomePremiumActivity : BasePremiumActivity() {

    @Inject lateinit var appStorage: AppKeyStorage

    companion object {
        @JvmStatic
        fun getOpenIntent(context: Context): Intent = Intent(context, PrimeOnboardingActivity::class.java)
    }

    override fun onSubClicked() {
        subscribe()
        markScreenAsVisited()
    }

    override fun onBackPressed() {
        if (!blockCloseFlag) {
            closeScreen()
        }
    }

    private fun closeScreen() {
        markScreenAsVisited()
        startActivity(
            Intent(this, PrimeMainActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
        )
    }

    private fun markScreenAsVisited() {
        appStorage.isNeedShowWelcome = false
    }

    override fun subscribeTo() = closeScreen()
}