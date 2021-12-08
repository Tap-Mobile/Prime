package com.prime.features.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.prime.features.iap.presentation.PrimeIapActivity

/**
 * Developed by
 * @author Aleksandr Artemov
 */
interface StartActivityController {
    fun startActivityResult(intent: Intent, requestCode: Int)
}

object NavigationHelper {
    const val REQUEST_PREMIUM_ACTIVITY = 1000

    fun startIapScreen(fragment: Fragment) = startIapScreen(fragment.requireContext(), object : StartActivityController {
        override fun startActivityResult(intent: Intent, requestCode: Int) {
            fragment.startActivityForResult(intent, requestCode)
        }
    })

    fun startIapScreen(activity: Activity) = startIapScreen(activity, object : StartActivityController {
        override fun startActivityResult(intent: Intent, requestCode: Int) {
            activity.startActivityForResult(intent, requestCode)
        }
    })

    private fun startIapScreen(context: Context, starter: StartActivityController) {
        starter.startActivityResult(Intent(context, PrimeIapActivity::class.java), REQUEST_PREMIUM_ACTIVITY)
    }
}