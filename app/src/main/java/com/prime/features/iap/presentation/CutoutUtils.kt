package com.prime.features.iap.presentation

import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.Window
import androidx.core.view.WindowInsetsCompat
import com.prime.data.AppCrashlytics

/**
 * Developed by
 * @author Aleksandr Artemov
 */

object CutoutUtils {
    fun fixViewTopCutout(view: View, window: Window, topMargin: Float) {
        val displayCutout =
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    WindowInsetsCompat.toWindowInsetsCompat(window.decorView.rootWindowInsets).displayCutout
                else null
            } catch (e: Throwable) {
                AppCrashlytics.recordException(e)
                null
            }

        displayCutout?.boundingRects?.let {
            fixButtonWithCutouts(view, it, topMargin)
        }
    }

    private fun fixButtonWithCutouts(view: View, cutouts: List<Rect>, topMargin: Float) {
        val viewHitRect = Rect().apply {
            view.getHitRect(this)
        }
        var fixed = false

        for (cutout in cutouts) {
            if (Rect.intersects(cutout, viewHitRect)) {
                view.y = topMargin + cutout.bottom.toFloat()
                fixed = true
                break
            }
        }

        if (fixed) {
            view.post {
                fixButtonWithCutouts(view, cutouts, topMargin)
            }
        }
    }
}