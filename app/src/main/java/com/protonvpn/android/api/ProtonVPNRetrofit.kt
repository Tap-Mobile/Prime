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
package com.protonvpn.android.api

import com.protonvpn.android.appconfig.AppConfigResponse
import com.protonvpn.android.models.login.SessionListResponse
import com.protonvpn.android.models.vpn.ConnectingDomainResponse
import com.protonvpn.android.models.vpn.LoadsResponse
import com.protonvpn.android.models.vpn.ServerList
import com.protonvpn.android.models.vpn.UserLocation
import me.proton.core.network.data.protonApi.BaseRetrofitApi
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@Suppress("ComplexInterface")
interface ProtonVPNRetrofit : BaseRetrofitApi {

    @GET("vpn/logicals")
    suspend fun getServers(@Query("IP") ip: String?): ServerList

    @GET("vpn/loads")
    suspend fun getLoads(@Query("IP") ip: String?): LoadsResponse

    @GET("vpn/servers/{serverId}")
    suspend fun getServerDomain(@Path(value = "serverId", encoded = true) serverId: String): ConnectingDomainResponse

    @GET("vpn/location")
    suspend fun getLocation(): UserLocation
}
