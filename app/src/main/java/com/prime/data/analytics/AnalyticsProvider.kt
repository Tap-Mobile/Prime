package com.prime.data.analytics

/**
 * Developed by
 * @author Aleksandr Artemov
 */
interface AnalyticsProvider {
    fun logEvent(event: String, params: Map<String, Any>?)
}