package com.protonvpn.android.api.newa

import com.protonvpn.android.appconfig.AppConfigResponse
import com.protonvpn.android.models.vpn.ConnectingDomain
import com.protonvpn.android.models.vpn.ConnectingDomainResponse
import com.protonvpn.android.models.vpn.LoadsResponse
import com.protonvpn.android.models.vpn.Location
import com.protonvpn.android.models.vpn.Server
import com.protonvpn.android.models.vpn.ServerList
import me.proton.core.network.data.protonApi.RefreshTokenRequest
import me.proton.core.network.data.protonApi.RefreshTokenResponse

/**
 * Developed by
 * @author Aleksandr Artemov
 */
object FakeTempApi : PrimeVpnApi {
    override suspend fun getServers() = ServerList(
        listOf(
            Server(
                serverId = "267599142",
                entryCountry = "US",
                exitCountry = "US",
                serverName = "New York #1",
                connectingDomains = listOf(
                    ConnectingDomain(
                        entryIp = "159.65.43.245",
                        exitIp = "159.65.43.245",
                        id = "267599142",
                        entryDomain = "",
                        isOnline = true,
                        label = ""
                    )
                ),
                domain = "",
                load = 0f,
                tier = 1,
                city = "",
                features = 0,
                location = Location("40", "40"),
                score = 1f,
                isOnline = true,
                flagImage = "https://prime.y0.com/flags/us.png"
            ),
            Server(
                serverId = "271217479",
                entryCountry = "US",
                exitCountry = "US",
                serverName = "New York #2",
                connectingDomains = listOf(
                    ConnectingDomain(
                        entryIp = "104.131.176.210",
                        exitIp = "104.131.176.210",
                        id = "271217479",
                        entryDomain = "",
                        isOnline = false,
                        label = ""
                    )
                ),
                domain = "",
                load = 0f,
                tier = 0,
                city = "",
                features = 0,
                location = Location("40", "40"),
                score = 1f,
                isOnline = false,
                flagImage = "https://prime.y0.com/flags/us.png"
            ),
        )
    )

    override suspend fun getAppConfig(): AppConfigResponse = AppConfigResponse()

    override suspend fun getLoads() = LoadsResponse(emptyList())

    override suspend fun getServerDomain(serverId: String): ConnectingDomainResponse =
        throw RuntimeException("Not yet implemented")

    override suspend fun refreshToken(body: RefreshTokenRequest): RefreshTokenResponse =
        throw RuntimeException("Not yet implemented")

    override suspend fun ping() = Unit
}