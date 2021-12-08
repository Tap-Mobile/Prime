package com.prime.features.servers.model

/**
 * Developed by
 * @author Aleksandr Artemov
 */
sealed class PrimeServersNavigation {
    object Close : PrimeServersNavigation()
    object OpenIap : PrimeServersNavigation()
    object CloseFlow : PrimeServersNavigation()


    sealed class Countries : PrimeServersNavigation() {
        data class OpenCountry(val flag: String) : Countries()
    }
}