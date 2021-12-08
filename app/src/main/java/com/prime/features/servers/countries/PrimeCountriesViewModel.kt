package com.prime.features.servers.countries

import com.prime.features.servers.PrimeServersViewModel
import com.prime.features.servers.ServerUi
import com.protonvpn.android.api.ProtonApiRetroFit
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.models.vpn.VpnCountry
import com.protonvpn.android.ui.home.ServerListUpdater
import com.protonvpn.android.utils.ServerManager
import com.protonvpn.android.vpn.VpnStateMonitor
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeCountriesViewModel @Inject constructor(
    serverManager: ServerManager,
    serverListUpdater: ServerListUpdater,
    vpnStateMonitor: VpnStateMonitor,
    userData: UserData,
    api: ProtonApiRetroFit
) : PrimeServersViewModel(serverManager, serverListUpdater, vpnStateMonitor, userData, api) {

    override fun getServerList(): List<ServerUi> {
        val selected = userData.defaultConnection

        return serverManager.getVpnCountries()
            .sortedBy(VpnCountry::countryName)
            .map { country ->
                if (country.serverList.size > 1) {
                    ServerUi.Country(
                        country.flag,
                        country.flagImage,
                        country.countryName,
                        country.serverList.size,
                        selected?.country == country.flag
                    )
                } else {
                    val city = country.serverList.first()

                    ServerUi.City(
                        city.flag,
                        city.flagImage,
                        city.serverName,
                        city.isPremiumServer,
                        selected?.country == country.flag
                    )
                }
            }
            .toMutableList()
            .apply {
                add(0, ServerUi.OptimalLocation(selected == null))
            }
    }
}