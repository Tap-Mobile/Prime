package com.prime.utils

import android.content.Context
import android.content.Intent
import com.vpn.prime.R

/**
 * Developed by
 * @author Aleksandr Artemov
 */
object ShareAppHelper {
    fun shareApp(context: Context) = context.startActivity(
        Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    context.getString(R.string.prime_shareText) + "\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                )
                type = "text/plain"
            },
            context.getString(R.string.prime_shareTitle)
        )
    )
}