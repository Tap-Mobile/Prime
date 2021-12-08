/*
 * Copyright (c) 2020 Proton Technologies AG
 * This file is part of Proton Technologies AG and ProtonCore.
 *
 * ProtonCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ProtonCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ProtonCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.proton.core.network.data.di

object Constants {

    /**
     * Certificate pins for Proton API (base64, SHA-256).
     */
    val DEFAULT_SPKI_PINS = arrayOf<String>()

    /**
     * SPKI pins for alternative Proton API leaf certificates (base64, SHA-256).
     */
    val ALTERNATIVE_API_SPKI_PINS = listOf<String>()

    /**
     * DNS over HTTPS services urls.
     */
    val DOH_PROVIDERS_URLS =
        arrayOf("https://dns11.quad9.net/dns-query/", "https://dns.google/dns-query/")
}
