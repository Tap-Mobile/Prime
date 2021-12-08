/*
 * Copyright (c) 2019 Proton Technologies AG
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
package com.protonvpn.android.appconfig

import com.protonvpn.android.utils.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AppConfigResponse(
    @SerialName(value = "Template") val openVpnTemplateConfig: String = BASE_VPN_TEMPLATE,

    //Not added on server side
    @SerialName(value = "OpenVPNConfig") val openVPNConfigResponse: OpenVPNConfigResponse = OpenVPNConfigResponse(
        DefaultPorts.defaults
    ),
    @SerialName(value = "ServerRefreshInterval") val underMaintenanceDetectionDelay: Long = Constants.DEFAULT_MAINTENANCE_CHECK_MINUTES,
    @SerialName(value = "FeatureFlags") val featureFlags: FeatureFlags = FeatureFlags()
) {
    val defaultPorts: DefaultPorts get() = openVPNConfigResponse.defaultPorts
}

private const val BASE_VPN_TEMPLATE =
    "client\n" +
        "verb 0\n" +
        "comp-lzo no\n" +
        "connect-retry-max 5\n" +
        "tun-mtu 1500 \n" +
        "dev tun\n" +
        "remote %s 1194 udp\n" +
        "route 0.0.0.0 0.0.0.0 vpn_gateway\n" +
        "remote-cert-tls server\n" +
        "persist-tun\n" +
        "mssfix 1353\n" +
        "dhcp-option DNS 10.8.0.1\n" +
        "dhcp-option DNS 1.1.1.1\n" +
        "route %s 255.255.255.255 net_gateway\n" +
        "<ca>\n" +
        "%s\n" +
        "</ca>\n" +
        "<key>\n" +
        "%s\n" +
        "</key>\n" +
        "<cert>\n" +
        "%s\n" +
        "</cert>"