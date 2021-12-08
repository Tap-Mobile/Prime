package com.prime.features.reviews

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.di.AppScope
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */

enum class RatingCountryBehavior {
    BAD_RATING,
    GOOD_RATING,
}

enum class RateUsPlacement {
    MENU;
}

interface RateUsManager {
    fun showRateUs(activity: FragmentActivity, placement: RateUsPlacement): Boolean
}

@AppScope
class RateUsManagerImpl @Inject constructor(
    private val analytics: RateUsAnalytics,
) : RateUsManager {

    override fun showRateUs(activity: FragmentActivity, placement: RateUsPlacement): Boolean {
        Toast.makeText(activity, "Not yet implemented", Toast.LENGTH_SHORT).show()
        return true
    }
}