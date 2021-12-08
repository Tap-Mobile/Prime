/*
 * Copyright (c) 2018 Proton Technologies AG
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
package com.di.app.modules.common

import android.os.SystemClock
import com.PrimeApp
import com.di.AppScope
import com.google.gson.Gson
import com.prime.features.servers.core.AppServersLoader
import com.prime.features.servers.core.AppServersLoaderManager
import com.protonvpn.android.api.ProtonApiRetroFit
import com.protonvpn.android.api.VpnApiClient
import com.protonvpn.android.api.VpnApiManager
import com.protonvpn.android.api.newa.PrimeVpnApi
import com.protonvpn.android.appconfig.AppConfig
import com.protonvpn.android.components.NotificationHelper
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.ui.home.ServerListUpdater
import com.protonvpn.android.utils.Constants.PRIMARY_VPN_API_URL
import com.protonvpn.android.utils.CoreLogger
import com.protonvpn.android.utils.ServerManager
import com.protonvpn.android.utils.Storage
import com.protonvpn.android.utils.TrafficMonitor
import com.protonvpn.android.vpn.ConnectivityMonitor
import com.protonvpn.android.vpn.MaintenanceTracker
import com.protonvpn.android.vpn.ProtonVpnBackendProvider
import com.protonvpn.android.vpn.RecentsManager
import com.protonvpn.android.vpn.VpnBackendProvider
import com.protonvpn.android.vpn.VpnConnectionErrorHandler
import com.protonvpn.android.vpn.VpnConnectionManager
import com.protonvpn.android.vpn.VpnStateMonitor
import com.protonvpn.android.vpn.ikev2.StrongSwanBackend
import com.protonvpn.android.vpn.openvpn.OpenVpnBackend
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.proton.core.network.data.ProtonCookieStore
import me.proton.core.network.data.di.ApiFactory
import me.proton.core.network.data.di.NetworkManager
import me.proton.core.network.data.di.NetworkPrefs
import me.proton.core.network.domain.ApiManager
import me.proton.core.network.domain.NetworkManager
import java.util.Random

@Module
class AppModule {

    private val scope = CoroutineScope(Dispatchers.Main)
    private val random = Random()

    @AppScope
    @Provides
    fun provideServerManager(userData: UserData) =
        ServerManager(PrimeApp.getAppContext(), userData)

    @AppScope
    @Provides
    fun provideServerListUpdater(
        api: ProtonApiRetroFit,
        serverManager: ServerManager,
        userData: UserData,
        vpnStateMonitor: VpnStateMonitor,
    ) = ServerListUpdater(scope, api, serverManager, userData, vpnStateMonitor)

    @AppScope
    @Provides
    fun provideAppServersLoader() = AppServersLoader()

    @AppScope
    @Provides
    fun provideAppNetworkLoader(
        serversLoader: AppServersLoader,
        serversUpdater: ServerListUpdater,
    ) = AppServersLoaderManager(serversLoader, serversUpdater)

    @AppScope
    @Provides
    fun provideAppConfig(api: ProtonApiRetroFit, userData: UserData): AppConfig = AppConfig(scope, api, userData)

    @AppScope
    @Provides
    fun provideNetworkManager(): NetworkManager =
        NetworkManager(PrimeApp.getAppContext())

    @AppScope
    @Provides
    fun provideVpnApiManager(
        networkManager: NetworkManager,
        apiClient: VpnApiClient,
        userData: UserData
    ): VpnApiManager {
        val appContext = PrimeApp.getAppContext()
        val logger = CoreLogger()
        val sessionProvider = userData.apiSessionProvider
        val cookieStore = ProtonCookieStore(appContext)
        val apiFactory = ApiFactory(
            PRIMARY_VPN_API_URL, apiClient, logger, networkManager,
            NetworkPrefs(appContext), sessionProvider, sessionProvider, cookieStore, scope,
            emptyArray(), emptyList()
        )
        return VpnApiManager(apiFactory, userData.apiSessionProvider)
    }

    @AppScope
    @Provides
    fun provideApiManager(
        vpnApiManager: VpnApiManager
    ): ApiManager<PrimeVpnApi> = vpnApiManager

    @AppScope
    @Provides
    fun provideApiClient(userData: UserData, vpnStateMonitor: VpnStateMonitor): VpnApiClient =
        VpnApiClient(scope, userData, vpnStateMonitor)

    @AppScope
    @Provides
    fun provideAPI(apiManager: ApiManager<PrimeVpnApi>) = ProtonApiRetroFit(scope, apiManager)

    @AppScope
    @Provides
    fun provideRecentManager(
        vpnStateMonitor: VpnStateMonitor,
        serverManager: ServerManager,
    ) = RecentsManager(scope, vpnStateMonitor, serverManager)

    @AppScope
    @Provides
    fun provideGson() = Gson()

    @AppScope
    @Provides
    fun provideUserPrefs(): UserData =
        Storage.load(UserData::class.java, UserData()).apply {
        }

    @AppScope
    @Provides
    fun provideVpnConnectionErrorHandler(
        api: ProtonApiRetroFit,
        appConfig: AppConfig,
        userData: UserData,
        serverManager: ServerManager,
        vpnStateMonitor: VpnStateMonitor,
        serverListUpdater: ServerListUpdater,
        notificationHelper: NotificationHelper,
        networkManager: NetworkManager,
        vpnBackendProvider: VpnBackendProvider,
    ) = VpnConnectionErrorHandler(
        scope, PrimeApp.getAppContext(), api, appConfig, userData,
        serverManager, vpnStateMonitor, serverListUpdater, notificationHelper, networkManager, vpnBackendProvider
    )

    @AppScope
    @Provides
    fun provideVpnConnectionManager(
        userData: UserData,
        backendManager: VpnBackendProvider,
        networkManager: NetworkManager,
        vpnConnectionErrorHandler: VpnConnectionErrorHandler,
        vpnStateMonitor: VpnStateMonitor,
        notificationHelper: NotificationHelper,
    ) = VpnConnectionManager(
        PrimeApp.getAppContext(),
        userData,
        backendManager,
        networkManager,
        vpnConnectionErrorHandler,
        vpnStateMonitor,
        notificationHelper,
        scope,
    )

    @AppScope
    @Provides
    fun provideVpnStateMonitor() = VpnStateMonitor()

    @AppScope
    @Provides
    fun provideConnectivityMonitor() = ConnectivityMonitor(scope, PrimeApp.getAppContext())

    @AppScope
    @Provides
    fun provideNotificationHelper(
        vpnStateMonitor: VpnStateMonitor,
        trafficMonitor: TrafficMonitor,
    ) = NotificationHelper(PrimeApp.getAppContext(), scope, vpnStateMonitor, trafficMonitor)

    @AppScope
    @Provides
    fun provideVpnBackendManager(
        userData: UserData,
        networkManager: NetworkManager,
        appConfig: AppConfig,
        serverManager: ServerManager
    ): VpnBackendProvider =
        ProtonVpnBackendProvider(
            StrongSwanBackend(random, networkManager, scope, System::currentTimeMillis),
            OpenVpnBackend(random, userData, appConfig, System::currentTimeMillis),
            serverManager
        )

    @AppScope
    @Provides
    fun provideMaintenanceTracker(
        appConfig: AppConfig,
        vpnStateMonitor: VpnStateMonitor,
        vpnErrorHandler: VpnConnectionErrorHandler
    ) = MaintenanceTracker(scope, PrimeApp.getAppContext(), appConfig, vpnStateMonitor, vpnErrorHandler)

    @AppScope
    @Provides
    fun provideTrafficMonitor(
        vpnStateMonitor: VpnStateMonitor,
        connectivityMonitor: ConnectivityMonitor
    ) = TrafficMonitor(
        PrimeApp.getAppContext(),
        scope,
        SystemClock::elapsedRealtime,
        vpnStateMonitor,
        connectivityMonitor
    )
}
