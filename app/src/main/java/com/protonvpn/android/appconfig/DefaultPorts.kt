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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DefaultPorts(
    @SerialName(value = "UDP") private val udpPorts: List<Int>,
    @SerialName(value = "TCP") private val tcpPorts: List<Int>
) {

    fun getUdpPorts(): List<Int> =
        if (udpPorts.isEmpty()) PORTS_CONFIG.defaultUdpList else udpPorts

    fun getTcpPorts(): List<Int> =
        if (tcpPorts.isEmpty()) PORTS_CONFIG.defaultTcpList else tcpPorts

    companion object {
        val defaults: DefaultPorts
            get() = DefaultPorts(PORTS_CONFIG.defaultUdpList, PORTS_CONFIG.defaultTcpList)

        val defaultsExtended: DefaultPorts
            get() = DefaultPorts(PORTS_CONFIG.extendedUdpList, PORTS_CONFIG.extendedTcpList)
    }
}
private val PORTS_CONFIG = Ports.PrimePorts

private sealed class Ports {
    abstract val extendedUdpList: List<Int>
    abstract val extendedTcpList: List<Int>

    abstract val defaultUdpList: List<Int>
    abstract val defaultTcpList: List<Int>

    // object ProtonPorts : Ports() {
    //     override val extendedUdpList get() = listOf(80, 443, 4569, 1194, 5060)
    //     override val extendedTcpList get() = listOf(443, 5995, 8443)
    //
    //     override val defaultUdpList: List<Int> get() = listOf(443)
    //     override val defaultTcpList: List<Int> get() = listOf(443)
    // }

    object PrimePorts : Ports() {
        override val extendedUdpList get() = listOf(1194)
        override val extendedTcpList get() = listOf<Int>()

        override val defaultUdpList get() = listOf(1194)
        override val defaultTcpList get() = listOf<Int>()
    }
}