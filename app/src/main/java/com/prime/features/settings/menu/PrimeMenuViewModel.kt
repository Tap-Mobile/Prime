package com.prime.features.settings.menu

import androidx.lifecycle.ViewModel
import com.protonvpn.android.models.config.UserData
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeMenuViewModel @Inject constructor(
    val userData: UserData,
) : ViewModel() {

}