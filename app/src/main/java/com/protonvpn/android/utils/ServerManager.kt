/*
 * Copyright (c) 2017 Proton Technologies AG
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
package com.protonvpn.android.utils

import android.content.Context
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.models.profiles.Profile
import com.protonvpn.android.models.profiles.SavedProfilesV3
import com.protonvpn.android.models.profiles.ServerDeliver
import com.protonvpn.android.models.profiles.ServerWrapper
import com.protonvpn.android.models.profiles.ServerWrapper.Companion.makePreBakedFastest
import com.protonvpn.android.models.profiles.ServerWrapper.ProfileType
import com.protonvpn.android.models.vpn.ConnectingDomain
import com.protonvpn.android.models.vpn.LoadUpdate
import com.protonvpn.android.models.vpn.Server
import com.protonvpn.android.models.vpn.StreamingServicesResponse
import com.protonvpn.android.models.vpn.VpnCountry
import com.protonvpn.android.ui.home.ServerListUpdater
import com.vpn.prime.R
import org.jetbrains.annotations.TestOnly
import org.joda.time.DateTime
import java.io.Serializable

class ServerManager(
    @Transient private val appContext: Context,
    @Transient val userData: UserData
) : Serializable, ServerDeliver {

    private val vpnCountries = mutableListOf<VpnCountry>()
    private val secureCoreEntryCountries = mutableListOf<VpnCountry>()
    private val secureCoreExitCountries = mutableListOf<VpnCountry>()

    var streamingServices: StreamingServicesResponse? = null
        private set

    var updatedAt: DateTime? = null
        private set

    @Transient
    private val savedProfiles: SavedProfilesV3 =
        Storage.load(SavedProfilesV3::class.java, SavedProfilesV3.defaultProfiles(appContext, this))

    @Transient val updateEvent = LiveEvent()
    @Transient val profilesUpdateEvent = LiveEvent()

    val isDownloadedAtLeastOnce: Boolean
        get() = updatedAt != null && vpnCountries.isNotEmpty()

    val isOutdated: Boolean
        get() = updatedAt == null || vpnCountries.isEmpty() ||
            DateTime().millis - updatedAt!!.millis >= ServerListUpdater.LIST_CALL_DELAY

    private val allServers
        get() = sequenceOf(vpnCountries, secureCoreEntryCountries, secureCoreExitCountries)
            .flatten().flatMap { it.serverList.asSequence() }

    fun getServerById(id: String) = allServers.firstOrNull { it.serverId == id }

    private fun getEntryCountries(secureCore: Boolean) = if (secureCore)
        secureCoreEntryCountries else vpnCountries

    private fun getExitCountries(secureCore: Boolean) = if (secureCore)
        secureCoreExitCountries else vpnCountries

    init {
        val oldManager =
            Storage.load(ServerManager::class.java)
        if (oldManager != null) {
            vpnCountries.addAll(oldManager.getVpnCountries())
            secureCoreExitCountries.addAll(oldManager.getSecureCoreExitCountries())
            secureCoreEntryCountries.addAll(oldManager.getSecureCoreEntryCountries())
            streamingServices = oldManager.streamingServices
            updatedAt = oldManager.updatedAt
        }
        reInitProfiles()
    }

    override fun toString() = "vpnCountries: ${vpnCountries.size} entry: ${secureCoreEntryCountries.size}" +
        " exit: ${secureCoreExitCountries.size} saved: ${savedProfiles.profileList?.size} " +
        "ServerManager Updated: $updatedAt "

    // Whole profile providing should be moved to separate class outside of ServerManager
    private fun reInitProfiles() {
        savedProfiles.profileList.forEach {
            it.wrapper.setDeliverer(this)
        }
        sequenceOf(vpnCountries, secureCoreEntryCountries, secureCoreExitCountries).flatten().forEach {
            it.deliverer = this
        }
    }

    fun clearCache() {
        updatedAt = null
        Storage.delete(ServerManager::class.java)
    }

    fun setServers(serverList: List<Server>) {
        vpnCountries.clear()
        secureCoreEntryCountries.clear()
        secureCoreExitCountries.clear()
        val countries = serverList.asSequence().map(Server::flag).toSet()
        for (country in countries) {
            val servers = serverList.filter { server ->
                !server.isSecureCoreServer && server.flag == country
            }
            val image = serverList.first { it.flag == country }.flagImage

            vpnCountries.add(VpnCountry(country, image, servers, this))
        }
        //TODO R2 preload flags in another class?
        // val flagImages = serverList.asSequence().map(Server::flagImage).toSet()
        // for (image in flagImages){
        //     Glide.with(appContext)
        //         .load(image)
        //         .circleCrop()
        //         .apply(
        //             RequestOptions()
        //                 .fitCenter()
        //                 .priority(Priority.NORMAL)
        //                 .override(maxSize, maxSize)
        //                 .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        //         )
        //         .preload(maxSize, maxSize)
        // }


        //Secure core is not supported
        // for (country in countries) {
        //     if (country == "IS" || country == "SE" || country == "CH") {
        //         val servers = serverList.filter { server ->
        //             server.isSecureCoreServer && server.entryCountry.equals(country, ignoreCase = true)
        //         }
        //         val image = serverList.first { it.flag == country }.flagImage
        //
        //         secureCoreEntryCountries.add(VpnCountry(country, image, servers, this))
        //     }
        // }
        // for (country in countries) {
        //     val servers = serverList.filter { server ->
        //         server.isSecureCoreServer && server.exitCountry.equals(country, ignoreCase = true)
        //     }
        //     val image = serverList.first { it.flag == country }.flagImage
        //     if (servers.isNotEmpty())
        //         secureCoreExitCountries.add(VpnCountry(country, image, servers, this))
        // }
        updatedAt = DateTime()
        Storage.save(this)
        updateEvent.emit()
        profilesUpdateEvent.emit()
    }

    fun updateServerDomainStatus(connectingDomain: ConnectingDomain) {
        allServers.asSequence().flatMap { it.connectingDomains.asSequence() }
            .find { it.id == connectingDomain.id }?.let {
                it.isOnline = connectingDomain.isOnline
            }

        Storage.save(this)
        updateEvent.emit()
        profilesUpdateEvent.emit()
    }

    fun updateLoads(loadsList: List<LoadUpdate>) {
        val loadsMap = loadsList.asSequence().map { it.id to it }.toMap()
        allServers.forEach { server ->
            loadsMap[server.serverId]?.let {
                server.load = it.load
                server.score = it.score
            }
        }
        Storage.save(this)
        updateEvent.emit()
        profilesUpdateEvent.emit()
    }

    fun getVpnCountries(): List<VpnCountry> = vpnCountries

    val defaultFallbackConnection = getSavedProfiles()[0]

    val defaultConnection: Profile
        get() = (if (userData.defaultConnection == null)
            getSavedProfiles()[0] else userData.defaultConnection).also {
            it.wrapper.setDeliverer(this)
        }

    val fastestConnection: Profile
        get() = Profile(
            appContext.getString(R.string.profileFastest),
            "#27272c",
            makePreBakedFastest(this)
        )

    fun getSecureCoreEntryCountries(): List<VpnCountry> = secureCoreEntryCountries

    fun getVpnEntryCountry(country: String, secureCoreCountry: Boolean): VpnCountry? =
        getEntryCountries(secureCoreCountry).firstOrNull { it.flag == country }

    fun getVpnExitCountry(country: String, secureCoreCountry: Boolean): VpnCountry? =
        getExitCountries(secureCoreCountry).firstOrNull { it.flag == country }

    fun getBestScoreServer(country: VpnCountry): Server? =
        getBestScoreServer(country.serverList)

    fun getBestScoreServer(): Server? {
        val countries = getExitCountries(userData.isSecureCoreEnabled)
        val map = countries.asSequence()
            .map(VpnCountry::serverList)
            .mapNotNull(::getBestScoreServer)
            .groupBy(::hasAccessToServer)
            .mapValues { it.value.minBy(Server::score) }
        return map[true] ?: map[false]
    }

    fun getBestScoreServer(serverList: List<Server>): Server? {
        val map = serverList.asSequence()
            .filter { "tor" !in it.keywords && it.online }
            .groupBy(::hasAccessToServer)
            .mapValues { it.value.minBy(Server::score) }
        return map[true] ?: map[false]
    }

    private fun getRandomServer(): Server? {
        val allCountries = getExitCountries(userData.isSecureCoreEnabled)
        val accessibleCountries = allCountries.filter { it.hasAccessibleOnlineServer(userData) }
        return (if (accessibleCountries.isEmpty())
            allCountries else accessibleCountries).randomNullable()?.let(::getRandomServer)
    }

    private fun getRandomServer(country: VpnCountry): Server? {
        val online = country.serverList.filter(Server::online)
        val accessible = online.filter(::hasAccessToServer)
        return (if (accessible.isEmpty())
            online else accessible).randomNullable()
    }

    fun getSavedProfiles(): List<Profile> =
        savedProfiles.profileList

    fun deleteSavedProfiles() {
        val defaultProfiles =
            SavedProfilesV3.defaultProfiles(appContext, this).profileList
        for (profile in getSavedProfiles()) {
            if (profile !in defaultProfiles) {
                deleteProfile(profile)
            }
        }
    }

    fun addToProfileList(serverName: String?, color: String?, server: Server) {
        val newProfile =
            Profile(serverName!!, color!!, ServerWrapper.makeWithServer(server, this))
        newProfile.wrapper.setSecureCore(userData.isSecureCoreEnabled)
        addToProfileList(newProfile)
    }

    fun addToProfileList(profileToSave: Profile?): Boolean {
        if (!savedProfiles.profileList.contains(profileToSave)) {
            savedProfiles.profileList.add(profileToSave)
            Storage.save(savedProfiles)
            profilesUpdateEvent.emit()
            return true
        }
        return false
    }

    fun editDefaultProfile(profileToSave: Profile?) {
        userData.defaultConnection = profileToSave
    }

    fun editProfile(oldProfile: Profile, profileToSave: Profile?) {
        if (oldProfile == defaultConnection) {
            userData.defaultConnection = profileToSave
        }
        savedProfiles.profileList[savedProfiles.profileList.indexOf(oldProfile)] = profileToSave
        Storage.save(savedProfiles)
        profilesUpdateEvent.emit()
    }

    fun deleteProfile(profileToSave: Profile?) {
        savedProfiles.profileList.remove(profileToSave)
        Storage.save(savedProfiles)
        profilesUpdateEvent.emit()
    }

    fun getSecureCoreExitCountries(): List<VpnCountry> =
        secureCoreExitCountries

    override fun getServer(wrapper: ServerWrapper): Server? = when (wrapper.type) {
        ProfileType.FASTEST ->
            getBestScoreServer()
        ProfileType.RANDOM ->
            getRandomServer()
        ProfileType.RANDOM_IN_COUNTRY ->
            getVpnExitCountry(wrapper.country, wrapper.isSecureCore)?.let {
                getRandomServer(it)
            }
        ProfileType.FASTEST_IN_COUNTRY ->
            getVpnExitCountry(wrapper.country, wrapper.isSecureCore)?.let {
                getBestScoreServer(it)
            }
        ProfileType.DIRECT ->
            getServerById(wrapper.serverId!!)
    }

    override fun hasAccessToServer(server: Server): Boolean =
        userData.hasAccessToServer(server)

    // Sorted by score (best at front)
    fun getOnlineServers(secureCore: Boolean): List<Server> =
        getExitCountries(secureCore).asSequence().flatMap { country ->
            country.serverList.filter { it.online }.asSequence()
        }.sortedBy { it.score }.toList()

    @get:TestOnly val firstNotAccessibleVpnCountry
        get() =
            getVpnCountries().firstOrNull { !it.hasAccessibleOnlineServer(userData) }
                ?: throw UnsupportedOperationException("Should only use this method on free tiers")
}
