package com.prime.features.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.lensy.library.extensions.combineWith
import com.prime.features.ads.AdsManager
import com.prime.features.main.model.MainSelectedCountry
import com.prime.features.main.model.VpnStateUI
import com.prime.features.main.model.toUI
import com.prime.features.servers.core.AppServersLoader
import com.protonvpn.android.bus.ConnectToProfile
import com.protonvpn.android.bus.EventBus
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.utils.ServerManager
import com.protonvpn.android.vpn.VpnConnectionManager
import com.protonvpn.android.vpn.VpnStateMonitor
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeMainViewModel @Inject constructor(
    val vpnStateMonitor: VpnStateMonitor,
    val vpnConnectionManager: VpnConnectionManager,
    val serverManager: ServerManager,
    val userData: UserData,
    val appServersLoader: AppServersLoader,
    val adsManager: AdsManager,
) : ViewModel() {

    private val isServersDownloaded = MutableLiveData<Boolean>(serverManager.isDownloadedAtLeastOnce)

    val vpnStatus
        get() = vpnStateMonitor.status.asLiveData().combineWith(isServersDownloaded) { status, serversReady ->
            status!!.state.toUI(serversReady!!)
        }

    val isShowTip: LiveData<Boolean> get() = vpnStatus.map { it == VpnStateUI.Disabled }

    val isBlockUI : LiveData<Boolean> get() = vpnStatus.map { it == VpnStateUI.LoadingServers }.distinctUntilChanged()

    private val _selectedCountry = MutableLiveData<MainSelectedCountry>()
    val selectedCountry: LiveData<MainSelectedCountry> get() = _selectedCountry

    init {
        updateSelectedCountry()
    }

    fun onConnectClicked() {
        when {
            vpnStateMonitor.isConnected -> vpnConnectionManager.disconnect()
            vpnStateMonitor.isEstablishingConnection -> vpnConnectionManager.disconnect()
            else -> EventBus.post(ConnectToProfile(serverManager.defaultConnection))
        }
    }

    fun onServerUpdated() {
        isServersDownloaded.value = serverManager.isDownloadedAtLeastOnce
    }

    fun requestUpdateSelectedCountry() {
        updateSelectedCountry()
    }

    private fun updateSelectedCountry() {
        _selectedCountry.value = when (userData.defaultConnection) {
            null -> MainSelectedCountry.OptimalLocation
            else -> {
                val flag = userData.defaultConnection.country
                val countries = serverManager.getVpnCountries()
                val country = countries.firstOrNull { it.flag == flag }

                if (country != null)
                    MainSelectedCountry.Country(flag, country.flagImage, country.countryName)
                else
                    MainSelectedCountry.OptimalLocation
            }
        }
    }
}