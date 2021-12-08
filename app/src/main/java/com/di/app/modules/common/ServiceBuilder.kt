package com.di.app.modules.common

import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.protonvpn.android.components.BootReceiver
import com.protonvpn.android.components.QuickTileService
import com.protonvpn.android.vpn.ikev2.ProtonCharonVpnService
import com.protonvpn.android.vpn.openvpn.OpenVPNWrapperService
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Module
abstract class ServiceBuilder {
    @ContributesAndroidInjector(modules = [CharonModule::class])
    abstract fun bindCharon(): ProtonCharonVpnService

    @ContributesAndroidInjector(modules = [OpenVPNWrapperModule::class])
    abstract fun bindOpenVPN(): OpenVPNWrapperService

    @ContributesAndroidInjector(modules = [BootUpModule::class])
    abstract fun bindBootReceiver(): BootReceiver

    @RequiresApi(VERSION_CODES.N)
    @ContributesAndroidInjector(modules = [QuickTileModule::class])
    abstract fun bindQuickTile(): QuickTileService
}