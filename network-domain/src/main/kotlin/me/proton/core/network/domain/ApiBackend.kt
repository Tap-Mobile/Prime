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
package me.proton.core.network.domain

import me.proton.core.network.domain.session.Session

/**
 * Class responsible for making an actual API call using underlying HTTP library.
 *
 * @param Api API interface.
 */
interface ApiBackend<Api> {

    /**
     * BaseUrl for the API.
     */
    val baseUrl: String

    /**
     * Refresh the provided [Session] (accessToken, refreshToken, ...).
     *
     * @return [ApiResult] with updated [Session].
     */
    suspend fun refreshSession(session: Session): ApiResult<Session>

    /**
     * Lightweight call checking if API might be blocked.
     * @return [true] if API is not reachable and error might indicate blocking.
     */
    suspend fun isPotentiallyBlocked(): Boolean

    /**
     * Makes API call defined with [block] lambda.
     *
     * @param T Result type for API call.
     * @param call [ApiManager.Call] to be made.
     * @return [ApiResult] of the call.
     */
    suspend operator fun <T> invoke(call: ApiManager.Call<Api, T>): ApiResult<T>
}
