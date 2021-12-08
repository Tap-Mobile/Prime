package com.prime.features.settings.support.model

import com.vpn.prime.R

/**
 * Developed by
 * @author Aleksandr Artemov
 */
enum class SupportReason {
    CONNECTION_PROBLEMS,
    SLOW_INTERNET,
    OTHER
}

val SupportReason.titleId: Int
    get() = when (this) {
        SupportReason.CONNECTION_PROBLEMS -> R.string.prime_supportReasonConnectionProblems
        SupportReason.SLOW_INTERNET -> R.string.prime_supportReasonSlowInternet
        SupportReason.OTHER -> R.string.prime_supportReasonOther
    }

val SupportReason.messageId: Int
    get() = when (this) {
        SupportReason.CONNECTION_PROBLEMS -> R.string.prime_supportMessageReasonConnection
        SupportReason.SLOW_INTERNET -> R.string.prime_supportMessageReasonSlowInternet
        SupportReason.OTHER -> R.string.prime_supportMessageReasonOther
    }