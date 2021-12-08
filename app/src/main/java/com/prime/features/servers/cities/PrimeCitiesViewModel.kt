package com.prime.features.servers.cities

import com.prime.features.servers.PrimeServersViewModel
import com.prime.features.servers.ServerUi
import com.protonvpn.android.api.ProtonApiRetroFit
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.models.vpn.Server
import com.protonvpn.android.ui.home.ServerListUpdater
import com.protonvpn.android.utils.ServerManager
import com.protonvpn.android.vpn.VpnStateMonitor

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeCitiesViewModel constructor(
    private val flag: String,
    serverManager: ServerManager,
    serverListUpdater: ServerListUpdater,
    vpnStateMonitor: VpnStateMonitor,
    userData: UserData,
    api: ProtonApiRetroFit
) : PrimeServersViewModel(serverManager, serverListUpdater, vpnStateMonitor, userData, api) {

    override fun getServerList(): List<ServerUi> {
        val selected = userData.defaultConnection

        return serverManager.getVpnCountries().first { it.flag == flag }
            .serverList
            .sortedBy(Server::serverName)
            .map {
                ServerUi.City(
                    it.flag,
                    it.flagImage,
                    it.serverName,
                    it.isPremiumServer,
                    selected?.wrapper?.serverId == it.serverId
                )
            }
            .toMutableList<ServerUi>()
            .apply {
                add(0, ServerUi.OptimalLocation(selected == null))
            }
    }
}