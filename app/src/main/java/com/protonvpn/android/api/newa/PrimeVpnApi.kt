package com.protonvpn.android.api.newa

import com.protonvpn.android.appconfig.AppConfigResponse
import com.protonvpn.android.models.vpn.ConnectingDomainResponse
import com.protonvpn.android.models.vpn.LoadsResponse
import com.protonvpn.android.models.vpn.ServerList
import me.proton.core.network.data.protonApi.BaseRetrofitApi
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Developed by
 * @author Aleksandr Artemov
 */
interface PrimeVpnApi : BaseRetrofitApi {

    @GET("servers")
    suspend fun getServers(): ServerList

    @GET("config")
    suspend fun getAppConfig(): AppConfigResponse

    @GET("load")
    suspend fun getLoads(): LoadsResponse

    @POST("status")
    suspend fun getServerDomain(@Path(value = "serverId", encoded = true) serverId: String): ConnectingDomainResponse
}