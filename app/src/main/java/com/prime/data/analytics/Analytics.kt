package com.prime.data.analytics

/**
 * Developed by
 * @author Elad Finish
 */

object Analytics {
    object Iap {
        object Key {
            const val PAYMENT_START = "payment_start"
            const val PAYMENT_COMPLETE = "payment_complete"
            const val PAYMENT_FAILED = "payment_failed"
        }

        object Param {
            const val PRODUCT_ID = "product_id"
        }
    }

    object Navigation {
        object Key {
            object Screen {
                private const val SCREEN = "screen"

                const val IAP = "${SCREEN}_iap"
            }

            object Dialog {
                private const val DIALOG = "dialog"
            }
        }

        object Param {
            const val SCREEN = "screen"
        }
    }

    object RateUs {
        object Key {
            const val RATED_AND_OPENED_STORE = "rated_5_and_opened_store"
        }
    }

    object Servers {
        object Key {
            const val COUNTRY_SELECTED = "country_selected"
        }

        object Param {
            const val NAME = "name"
        }

        object Value {
            const val OPTIMAL = "optimal"
        }
    }

    object Vpn {
        object Key {
            const val CONNECTED = "connected"
            const val DISCONNECTED = "disconnected"
        }
    }

    object Ads {
        object Key {
            const val OPENED = "ad_opened"
            const val CLICKED = "ad_clicked"
        }

        object Param {
            const val AD_TYPE = "type"
        }
    }

}


