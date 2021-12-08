/*
 * Copyright (c) 2018 Proton Technologies AG
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
import com.protonvpn.android.components.Listable
import com.protonvpn.android.components.Markable
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.models.profiles.ServerDeliver
import com.protonvpn.android.models.profiles.ServerWrapper
import com.protonvpn.android.utils.CountryTools
import java.io.Serializable
import java.util.Collections

class VpnCountry(
    val flag: String,
    val flagImage: String,
    serverList: List<Server>,
    deliverer: ServerDeliver
) : Markable, Serializable, Listable {
    val serverList: List<Server>
    private val keywords: MutableList<String>

    @Transient var deliverer: ServerDeliver

    val countryName: String
        get() = CountryTools.getFullName(flag)

    val wrapperServers: List<ServerWrapper>
        get() {
            val wrapperList = ArrayList<ServerWrapper>()
            if (connectableServers.size > 1) {
                wrapperList.add(ServerWrapper.makeFastestForCountry(flag, deliverer))
                wrapperList.add(ServerWrapper.makeRandomForCountry(flag, deliverer))
            }
            connectableServers.mapTo(wrapperList) { ServerWrapper.makeWithServer(it, deliverer) }
            return wrapperList
        }

    val fastestServer: Server?
        get() = ServerWrapper.makeFastestForCountry(flag, deliverer).server

    init {
        this.serverList = sortServers(serverList)
        this.deliverer = deliverer
        this.keywords = ArrayList()
        initKeywords()
    }

    private fun initKeywords() {
        for (server in serverList) {
            server.keywords
                .filterNot { keywords.contains(it) }
                .forEach { keywords.add(it) }
        }
    }

    fun hasAccessibleServer(userData: UserData): Boolean =
        serverList.any { userData.hasAccessToServer(it) }

    fun hasAccessibleOnlineServer(userData: UserData): Boolean =
        serverList.any { userData.hasAccessToServer(it) && it.online }

    fun isUnderMaintenance(): Boolean = !serverList.any { it.online }

    private fun sortServers(serverList: List<Server>): List<Server> {
        Collections.sort(serverList,
            compareBy<Server> { !it.isFreeServer }
                .thenBy { it.serverNumber >= 100 }
                .thenBy { it.domain }
                .thenBy { it.tier })
        return serverList
    }

    fun getKeywords(): List<String> = keywords

    fun hasConnectedServer(server: Server?): Boolean {
        if (server == null) {
            return false
        }
        return serverList.any { it.domain == server.domain }
    }

    override fun isSecureCoreMarker(): Boolean = isSecureCoreCountry()

    override fun getMarkerText(): String = countryName

    override fun getConnectableServers(): List<Server> = serverList

    override fun getLabel(context: Context): String = countryName

    fun isSecureCoreCountry(): Boolean = false//flag =="IS" || flag == "SE" || flag == "CH"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VpnCountry

        if (flag != other.flag) return false
        if (serverList != other.serverList) return false
        if (keywords != other.keywords) return false
        if (deliverer != other.deliverer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = flag.hashCode()
        result = 31 * result + serverList.hashCode()
        result = 31 * result + keywords.hashCode()
        result = 31 * result + deliverer.hashCode()
        return result
    }
}
