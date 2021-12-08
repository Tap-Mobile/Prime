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
package com.prime.ui.base

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.prime.features.ads.AdsManager
import com.vpn.prime.R
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

@TargetApi(Build.VERSION_CODES.N)
fun Activity.showNoVpnPermissionDialog() {
    MaterialDialog.Builder(this).theme(Theme.DARK)
        .title(R.string.error_prepare_vpn_title)
        .content(
            getString(R.string.error_prepare_vpn_description)
        )
        .positiveText(R.string.error_prepare_vpn_settings)
        .onPositive { _: MaterialDialog?, _: DialogAction? ->
            startActivity(Intent(Settings.ACTION_VPN_SETTINGS))
        }.show()
}

abstract class PrimeBaseActivity : DaggerAppCompatActivity() {

    @Inject protected lateinit var adsManager: AdsManager

    override fun onStart() {
        super.onStart()
        adsManager.attachActivity(this)
    }

    override fun onStop() {
        super.onStop()
        adsManager.detachActivity(this)
    }
}
