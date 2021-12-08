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
package com.protonvpn.android.api

import com.prime.data.DebugKeys.Vpn.UseFakeServers
import com.prime.data.debugConfig
import com.protonvpn.android.api.newa.FakeTempApi
import com.protonvpn.android.api.newa.PrimeVpnApi
import com.protonvpn.android.components.LoaderUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.proton.core.network.domain.ApiManager
import me.proton.core.network.domain.ApiResult

//TO DO: remove dependencies on activity/network loaders, refactor callbacks to suspending functions
open class ProtonApiRetroFit(val scope: CoroutineScope, private val manager: ApiManager<PrimeVpnApi>) {

    open suspend fun getAppConfig() = manager { getAppConfig() }

    open suspend fun getServerList(loader: LoaderUI?) = makeCall(loader) {
        if (debugConfig(UseFakeServers))
            FakeTempApi.getServers()
        else
            it.getServers()
    }

    open suspend fun getLoads() = manager { getLoads() }

    //enable maintenanceTrackerEnabled in feature flags after implementation
    open suspend fun getConnectingDomain(domainId: String) = ApiResult.Success(FakeTempApi.getServerDomain(domainId))

    private suspend fun <T> makeCall(
        loader: LoaderUI?,
        callFun: suspend (PrimeVpnApi) -> T
    ): ApiResult<T> {
        loader?.switchToLoading()
        val result = manager(block = callFun)
        when (result) {
            is ApiResult.Success -> {
                loader?.switchToEmpty()
            }
            is ApiResult.Error -> {
                loader?.switchToRetry(result)
            }
        }
        return result
    }

    private fun <T> makeCall(
        callback: NetworkResultCallback<T>,
        loader: LoaderUI? = null,
        callFun: suspend (PrimeVpnApi) -> T
    ) = scope.launch {
        loader?.switchToLoading()
        when (val result = manager(block = callFun)) {
            is ApiResult.Success -> {
                loader?.switchToEmpty()
                callback.onSuccess(result.value)
            }
            is ApiResult.Error -> {
                loader?.switchToRetry(result)
                callback.onFailure()
            }
        }
    }
}