/*
 * Copyright (c) 2019 Proton Technologies AG
 * 
 * This file is part of ProtonVPN.
 * 
 * ProtonVPN is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ProtonVPN is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ProtonVPN.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.prime.ui

import android.content.Intent
import android.os.Bundle
import com.prime.data.keys.AppKeyStorage
import com.prime.features.iap.presentation.onboarding.PrimeOnboardingActivity
import com.protonvpn.android.utils.AndroidUtils.isTV
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {

    @Inject lateinit var appStorage: AppKeyStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val screen = when {
            isTV() -> throw IllegalStateException("TV is not supported")
            appStorage.isNeedShowWelcome -> PrimeOnboardingActivity::class.java
            else -> PrimeMainActivity::class.java
        }

        startActivity(Intent(this, screen))
    }
}
