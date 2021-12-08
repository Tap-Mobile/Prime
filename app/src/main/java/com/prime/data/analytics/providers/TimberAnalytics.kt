package com.prime.data.analytics.providers

import com.prime.data.analytics.AnalyticsProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Developed by
 * @author Aleksandr Artemov
 */
@Singleton
class TimberAnalytics @Inject constructor(
) : AnalyticsProvider {

    override fun logEvent(event: String, params: Map<String, Any>?) {
        Timber.i(
            "Analytics event \"$event\" ${
                params?.entries?.let { entries ->
                    if (entries.isNotEmpty())
                        "params: ${entries.joinToString(",", "{", "}") { "${it.key}:${it.value}" }}"
                    else
                        ""
                } ?: ""
            }"
        )
    }
}