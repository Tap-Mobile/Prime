/*
 * Copyright (c) 2020 Proton Technologies AG
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
package com.protonvpn.android.appconfig

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.protonvpn.android.api.ProtonApiRetroFit
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.utils.Constants
import com.protonvpn.android.utils.ReschedulableTask
import com.protonvpn.android.utils.Storage
import kotlinx.coroutines.CoroutineScope
import me.proton.core.network.domain.ApiResult
import java.util.concurrent.TimeUnit

class AppConfig(scope: CoroutineScope, val api: ProtonApiRetroFit, val userData: UserData) {

    private var appConfigResponseObservable: MutableLiveData<AppConfigResponse>

    val appConfigResponse get() = appConfigResponseObservable.value!!

    private var updateTask = ReschedulableTask(scope, SystemClock::elapsedRealtime) {
        update()
    }

    init {
        appConfigResponseObservable = MutableLiveData(Storage.load(AppConfigResponse::class.java, getDefaultConfig()))
        updateTask.scheduleIn(0)
    }

    suspend fun update() {
        val result = api.getAppConfig()
        result.valueOrNull?.let { config ->
            Storage.save(config)
            appConfigResponseObservable.value = config
        }
        updateTask.scheduleIn(if (result is ApiResult.Error.Connection) UPDATE_DELAY_FAIL else UPDATE_DELAY)
    }

    fun getMaintenanceTrackerDelay(): Long = maxOf(Constants.MINIMUM_MAINTENANCE_CHECK_MINUTES,
        appConfigResponse.underMaintenanceDetectionDelay)

    fun isMaintenanceTrackerEnabled(): Boolean = appConfigResponse.featureFlags.maintenanceTrackerEnabled

    fun getOpenVPNPorts(): DefaultPorts = appConfigResponse.defaultPorts

    fun getFeatureFlags(): FeatureFlags = appConfigResponse.featureFlags

    fun getOpenVpnConfig(): String = appConfigResponse.openVpnTemplateConfig

    fun getLiveConfig(): LiveData<AppConfigResponse> = appConfigResponseObservable

    private fun getDefaultConfig(): AppConfigResponse {
        return AppConfigResponse()
    }

    companion object {
        private val UPDATE_DELAY = TimeUnit.DAYS.toMillis(1)
        private val UPDATE_DELAY_FAIL = TimeUnit.HOURS.toMillis(3)
    }
}
