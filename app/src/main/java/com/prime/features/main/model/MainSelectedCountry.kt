package com.prime.features.main.model

/**
 * Developed by
 * @author Aleksandr Artemov
 */
sealed class MainSelectedCountry {
    object OptimalLocation : MainSelectedCountry()
    data class Country(val flag: String, val flagImage: String, val name: String) : MainSelectedCountry()
}