package com.prime.features.reviews

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import com.protonvpn.android.utils.PrimeLogger
import com.vpn.prime.BuildConfig
import com.vpn.prime.R
import java.util.Locale

/**
 * Developed by
 * @author Aleksandr Artemov
 */
object ContactUsHelper {

    private const val NO_RATED = -2

    private val EMAIL_RECIPIENTS = arrayOf("Not ")

    private const val TEMPLATE_FEEDBACK_TITLE = "%s Feedback"
    private const val TEMPLATE_SUPPORT_TITLE = "%s Support"

    fun sendAppFeedback(activity: Activity, feedback: String, starsRated: Int) =
        sendMail(
            activity,
            TEMPLATE_FEEDBACK_TITLE.format(getApplicationName(activity)),
            getAdjustedFeedback(feedback, starsRated)
        )

    //TODO R2 fix adding logs files?
    fun sendMessageToSupport(activity: Activity, message: String) =
        sendMail(
            activity,
            title = TEMPLATE_SUPPORT_TITLE.format(getApplicationName(activity)),
            message = getAdjustedFeedback(message, NO_RATED),
            attachments = null//getLogsFile(activity)
        )

    private fun getLogsFile(context: Context): List<Uri> =
        PrimeLogger.getLogFiles()
            .map { FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), it); }

    fun sendMail(activity: Activity, title: String, message: String, attachments: List<Uri>? = null) {
        val sendIntent = Intent(
            Intent.ACTION_SENDTO
        ).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, EMAIL_RECIPIENTS)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, message)
            attachments?.firstOrNull()?.let { putExtra(Intent.EXTRA_STREAM, it) }
        }

        try {
            activity.startActivity(Intent.createChooser(sendIntent, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAdjustedFeedback(feedback: String, starsRated: Int): String =
        try {
            var adjustedFeedback = if (feedback.isEmpty()) "" else "$feedback\n\n"

            if (starsRated != NO_RATED) {
                adjustedFeedback += "Rated: $starsRated\n"
            }

            adjustedFeedback += """
                ${getAndroidVersion()}
                ${getDeviceName()}
                Application version: ${BuildConfig.VERSION_NAME}
                
                """.trimIndent()

            adjustedFeedback
        } catch (e: Exception) {
            feedback
        }

    private fun getAndroidVersion(): String =
        try {
            "Android SDK: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})"
        } catch (e: Exception) {
            ""
        }

    private fun getDeviceName(): String =
        try {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL

            "Device: ${
                if (model.startsWith(manufacturer)) model.capitalize(Locale.getDefault())
                else "${manufacturer.capitalize(Locale.getDefault())} $model"
            }"
        } catch (e: Exception) {
            ""
        }

    private fun getApplicationName(context: Context): String =
        try {
            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
        } catch (e: Exception) {
            ""
        }
}