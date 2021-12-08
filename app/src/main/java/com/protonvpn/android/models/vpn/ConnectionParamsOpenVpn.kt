/*
 * Copyright (c) 2020 Proton Technologies AG
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
package com.protonvpn.android.models.vpn

import android.content.Context
import android.os.Build
import com.protonvpn.android.appconfig.AppConfig
import com.protonvpn.android.models.config.TransmissionProtocol
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.models.config.VpnProtocol
import com.protonvpn.android.models.profiles.Profile
import de.blinkt.openvpn.core.ConfigParser
import java.io.StringReader

class ConnectionParamsOpenVpn(
    profile: Profile,
    server: Server,
    connectingDomain: ConnectingDomain,
    private val transmissionProtocol: TransmissionProtocol,
    private val port: Int
) : ConnectionParams(profile, server, connectingDomain, VpnProtocol.OpenVPN), java.io.Serializable {

    override val info get() = "${super.info} $transmissionProtocol port=$port"
    override val transmission get() = transmissionProtocol

    fun openVpnProfile(
        context: Context,
        userData: UserData,
        appConfig: AppConfig
    ) = ConfigParser().let { parser ->
        val ipAddress = connectingDomain!!.entryIp
        val template = appConfig.getOpenVpnConfig()
        parser.parseConfig(
            StringReader(
                template.format(
                    ipAddress,
                    ipAddress,
                    CA_CERT,
                    CLIENT_KEY,
                    CLIENT_CERT
                )
            )
        )
        parser.convertProfile()
    }.apply {
        mName = Build.MODEL
        mProfileCreator = context.packageName
        mAllowLocalLAN = userData.bypassLocalTraffic()
        mSearchDomain = ""

        //TODO R2 check if we can set allowed ips:
        if (userData.useSplitTunneling) {
            // if (userData.splitTunnelIpAddresses.isNotEmpty()) {
            //     mUseDefaultRoute = false
            //     mExcludedRoutes = userData.splitTunnelIpAddresses.joinToString(" ")
            // }
            mAllowedAppsVpn = HashSet<String>(userData.splitTunnelApps)

            //Server support only 1 port so far and it set in template from appConfig
            //mConnections[0] = Connection().apply {
            //     mServerName = connectingDomain!!.entryIp
            //     mUseUdp = transmissionProtocol == TransmissionProtocol.UDP
            //     mServerPort = port.toString()
            //     mCustomConfiguration = ""
            // }
        }
    }

    override fun hasSameProtocolParams(other: ConnectionParams) =
        other is ConnectionParamsOpenVpn && other.transmissionProtocol == transmissionProtocol && other.port == port

    companion object {

        private const val CA_CERT = "-----BEGIN CERTIFICATE-----\n" +
            " not implemented yet\n" +
            "-----END CERTIFICATE-----\n"

        private const val CLIENT_CERT = "-----BEGIN CERTIFICATE-----\n" +
            " not implemented yet\n" +
            "-----END CERTIFICATE-----\n"

        private const val CLIENT_KEY = "-----BEGIN PRIVATE KEY-----\n" +
            " not implemented yet\n" +
            "-----END PRIVATE KEY-----\n"
    }
}