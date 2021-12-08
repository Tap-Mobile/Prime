package com.prime.ui

import android.os.Bundle
import com.prime.ui.base.PrimeVpnActivity
import com.protonvpn.android.bus.ConnectToProfile
import com.protonvpn.android.bus.ConnectToServer
import com.protonvpn.android.models.profiles.Profile.Companion.getTempProfile
import com.protonvpn.android.ui.home.ServerListUpdater
import com.protonvpn.android.utils.ServerManager
import com.protonvpn.android.utils.ViewModelFactory
import com.protonvpn.android.vpn.VpnStateMonitor
import com.squareup.otto.Subscribe
import com.vpn.prime.databinding.PrimeMainActBinding
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeMainActivity : PrimeVpnActivity() {

    @Inject lateinit var serverManager: ServerManager
    @Inject lateinit var vpnStateMonitor: VpnStateMonitor
    @Inject lateinit var serverListUpdater: ServerListUpdater
    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: PrimeMainActBinding

    override val isRegisteredForEvents: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = PrimeMainActBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @Subscribe fun onConnectToServer(connectTo: ConnectToServer) {
        if (connectTo.server == null) {
            vpnConnectionManager.disconnect()
        } else {
            val server = connectTo.server
            onConnect(getTempProfile(server, serverManager, server.exitCountry))
        }
    }

    @Subscribe fun onConnectToProfile(connectTo: ConnectToProfile) {
        onConnect(connectTo.profile)
    }
}