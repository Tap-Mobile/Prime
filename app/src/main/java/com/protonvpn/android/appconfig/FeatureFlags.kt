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
package com.protonvpn.android.appconfig

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.proton.core.network.data.protonApi.IntToBoolSerializer

@Serializable
data class FeatureFlags(
    //enable to true when API support getServer getServerDomain
    @Serializable(with = IntToBoolSerializer::class)
    @SerialName(value = "ServerRefresh") val maintenanceTrackerEnabled: Boolean = false,
    @Serializable(with = IntToBoolSerializer::class)
    @SerialName(value = "NetShield") val netShieldEnabled: Boolean = false,
    @Serializable(with = IntToBoolSerializer::class)
    @SerialName(value = "DisplayTVLogos") val displayTVLogos: Boolean = false,
    @Serializable(with = IntToBoolSerializer::class)
    @SerialName(value = "SmartReconnect_DISABLED") val smartReconnect: Boolean = true,
) : java.io.Serializable
