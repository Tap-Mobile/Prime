package com.prime.features.main.model

import com.protonvpn.android.vpn.VpnState
import java.util.Locale

/**
 * Developed by
 * @author Aleksandr Artemov
 */
sealed class VpnStateUI {
    object LoadingServers : VpnStateUI()

    object Disabled : VpnStateUI()

    object Connecting : VpnStateUI()

    object Connected : VpnStateUI()
    object Disconnecting : VpnStateUI()

    object WaitingForNetwork : VpnStateUI()
    object Error : VpnStateUI()

    val name = javaClass.simpleName.toUpperCase(Locale.ROOT)
    override fun toString() = name
}

fun VpnState.toUI(isServersLoaded: Boolean) =
    if (isServersLoaded)
        when (this) {
            VpnState.Disabled -> VpnStateUI.Disabled

            VpnState.CheckingAvailability,
            VpnState.Reconnecting,
            VpnState.ScanningPorts,
            VpnState.Connecting -> VpnStateUI.Connecting

            VpnState.Connected -> VpnStateUI.Connected

            VpnState.Disconnecting -> VpnStateUI.Disconnecting

            VpnState.WaitingForNetwork -> VpnStateUI.WaitingForNetwork
            is VpnState.Error -> VpnStateUI.Error
        }
    else
        VpnStateUI.LoadingServers