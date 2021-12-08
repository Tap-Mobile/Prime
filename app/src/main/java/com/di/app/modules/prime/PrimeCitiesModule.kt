package com.di.app.modules.prime

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.navArgs
import com.di.app.modules.common.ViewModelKey
import com.prime.features.servers.cities.PrimeCitiesFragment
import com.prime.features.servers.cities.PrimeCitiesFragmentArgs
import com.prime.features.servers.cities.PrimeCitiesViewModel
import com.protonvpn.android.api.ProtonApiRetroFit
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.ui.home.ServerListUpdater
import com.protonvpn.android.utils.ServerManager
import com.protonvpn.android.vpn.VpnStateMonitor
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Module
class PrimeCitiesModule {

    @Provides
    @IntoMap
    @ViewModelKey(PrimeCitiesViewModel::class)
    fun bindsPrimeCitiesViewModel(
        fragment: PrimeCitiesFragment,
        serverManager: ServerManager,
        serverListUpdater: ServerListUpdater,
        vpnStateMonitor: VpnStateMonitor,
        userData: UserData,
        api: ProtonApiRetroFit
    ): ViewModel = PrimeCitiesViewModel(
        fragment.navArgs<PrimeCitiesFragmentArgs>().value.flag,
        serverManager,
        serverListUpdater,
        vpnStateMonitor,
        userData,
        api
    )
}