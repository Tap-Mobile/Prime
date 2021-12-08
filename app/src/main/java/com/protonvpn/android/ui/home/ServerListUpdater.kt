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
package com.protonvpn.android.ui.home

import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.protonvpn.android.api.NetworkLoader
import com.protonvpn.android.api.ProtonApiRetroFit
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.models.vpn.LoadsResponse
import com.protonvpn.android.models.vpn.ServerList
import com.protonvpn.android.utils.PrimeLogger
import com.protonvpn.android.utils.ReschedulableTask
import com.protonvpn.android.utils.ServerManager
import com.protonvpn.android.utils.Storage
import com.protonvpn.android.vpn.VpnStateMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.proton.core.network.domain.ApiResult
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

class ServerListUpdater(
    val scope: CoroutineScope,
    val api: ProtonApiRetroFit,
    val serverManager: ServerManager,
    val userData: UserData,
    val vpnStateMonitor: VpnStateMonitor,
) {
    companion object {
        private val LOADS_CALL_DELAY = TimeUnit.MINUTES.toMillis(15)
        private val ERROR_CALL_DELAY = TimeUnit.MILLISECONDS.toMillis(500)
        val LIST_CALL_DELAY = TimeUnit.HOURS.toMillis(3)
        private val MIN_CALL_DELAY = minOf(/*LOCATION_CALL_DELAY,*/LOADS_CALL_DELAY, LIST_CALL_DELAY)

        private const val KEY_LOADS_UPDATE_DATE = "LOADS_UPDATE_DATE"

        private fun now() = SystemClock.elapsedRealtime()
    }

    private var networkLoader: NetworkLoader? = null
    private var inForeground = false

    private val lastServerListUpdate
        get() =
            dateToRealtime(serverManager.updatedAt?.millis ?: 0L)
    private var lastLoadsUpdateInternal = Long.MIN_VALUE

    init {
        scope.launch {
            vpnStateMonitor.onDisconnectedByUser.collect {
                task.scheduleIn(0)
            }
        }
    }

    private val task = ReschedulableTask(scope, ::now) {
        PrimeLogger.log("ServerListUpdater task is running")
        if (userData.isLoggedIn) {
            val result =
                if (serverManager.isOutdated || inForeground && now() >= lastServerListUpdate + LIST_CALL_DELAY)
                    updateServerList(networkLoader)
                else if (inForeground && now() >= lastLoadsUpdate + LOADS_CALL_DELAY)
                    updateLoads()
                else null

            PrimeLogger.log("ServerListUpdater task finished: ${result?.let { it::class.simpleName }}")
            if (inForeground) {
                when (result) {
                    is ApiResult.Success -> scheduleIn(MIN_CALL_DELAY)
                    is ApiResult.Error -> scheduleIn(ERROR_CALL_DELAY)
                    null -> Unit //Do nothing
                }
            }
        }
    }

    private val lastLoadsUpdate
        get() = lastLoadsUpdateInternal.coerceAtLeast(lastServerListUpdate)

    private fun dateToRealtime(date: Long) =
        now() - (DateTime().millis - date).coerceAtLeast(0)

    init {
        lastLoadsUpdateInternal = dateToRealtime(Storage.getLong(KEY_LOADS_UPDATE_DATE, 0L))
    }

    fun startSchedule(lifecycle: Lifecycle, loader: NetworkLoader?) {
        networkLoader = loader
        if (serverManager.isOutdated)
            task.scheduleIn(0)

        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                inForeground = true
                task.scheduleIn(0)
            }


            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                inForeground = false
                task.cancelSchedule()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                networkLoader = null
            }
        })
    }

    fun getServersList(networkLoader: NetworkLoader?): Job = scope.launch(Dispatchers.Main) {
        updateServerList(networkLoader)
    }

    private suspend fun updateLoads(): ApiResult<LoadsResponse> {
        val result = api.getLoads()
        if (result is ApiResult.Success) {
            serverManager.updateLoads(result.value.loadsList)
            lastLoadsUpdateInternal = now()
            Storage.saveLong(KEY_LOADS_UPDATE_DATE, DateTime().millis)
        }
        return result
    }

    suspend fun updateServerList(
        networkLoader: NetworkLoader? = null
    ): ApiResult<ServerList> {
        val loaderUI = networkLoader?.networkFrameLayout

        loaderUI?.setRetryListener {
            scope.launch(Dispatchers.Main) {
                updateServerList(networkLoader)
            }
        }

        val result = api.getServerList(loaderUI)

        if (result is ApiResult.Success) {
            serverManager.setServers(result.value.serverList)
        }
        return result
    }
}
