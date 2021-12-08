package com.prime.features.servers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prime.features.servers.model.PrimeServersNavigation
import com.protonvpn.android.api.ProtonApiRetroFit
import com.protonvpn.android.bus.ConnectToProfile
import com.protonvpn.android.bus.EventBus
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.models.profiles.Profile
import com.protonvpn.android.models.vpn.Server
import com.protonvpn.android.models.vpn.VpnCountry
import com.protonvpn.android.ui.home.ServerListUpdater
import com.protonvpn.android.utils.ServerManager
import com.protonvpn.android.vpn.VpnStateMonitor

/**
 * Developed by
 * @author Aleksandr Artemov
 */
abstract class PrimeServersViewModel constructor(
    val serverManager: ServerManager,
    val serverListUpdater: ServerListUpdater,
    val vpnStateMonitor: VpnStateMonitor,
    val userData: UserData,
    val api: ProtonApiRetroFit
) : ViewModel() {

    protected val _navigationAction = MutableLiveData<PrimeServersNavigation?>(null)
    val navigationAction: LiveData<PrimeServersNavigation?>
        get() = _navigationAction

    fun onServerSelected(server: ServerUi) {

        when (server) {
            is ServerUi.OptimalLocation -> {
                saveAndConnect(null)
                _navigationAction.value = PrimeServersNavigation.CloseFlow
            }
            is ServerUi.Country -> {
                if (server.hasManyCities()) {
                    _navigationAction.value = PrimeServersNavigation.Countries.OpenCountry(server.flag)
                } else {
                    val country = serverManager.getVpnCountries().first { it.flag == server.flag }

                    checkAccessAndConnect(country) { country.fastestServer!! }
                }
            }
            is ServerUi.City -> {
                val country = serverManager.getVpnCountries().first { it.flag == server.flag }

                checkAccessAndConnect(country) { country.serverList.first { it.serverName == server.name } }
            }
        }
    }

    private fun checkAccessAndConnect(country: VpnCountry, serverProvider: (VpnCountry) -> Server) {
        val server = serverProvider(country)

        if (userData.hasAccessToServer(server)) {
            saveAndConnect(Profile.getTempProfile(server, country.deliverer))
            _navigationAction.value = PrimeServersNavigation.CloseFlow
        } else {
            _navigationAction.value = PrimeServersNavigation.OpenIap
        }
    }

    private fun saveAndConnect(profile: Profile?) {
        serverManager.editDefaultProfile(profile)
        EventBus.post(ConnectToProfile(profile ?: serverManager.fastestConnection))
    }

    abstract fun getServerList(): List<ServerUi>

    fun onNavigationActionHandled() {
        _navigationAction.value = null
    }

    fun onGoPremiumClicked() {
        _navigationAction.value = PrimeServersNavigation.OpenIap
    }

    fun onAccessAllClicked() {
        _navigationAction.value = PrimeServersNavigation.OpenIap
    }

    fun onCloseClicked() {
        _navigationAction.value = PrimeServersNavigation.Close
    }
}