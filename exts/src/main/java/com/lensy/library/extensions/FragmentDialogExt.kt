package com.lensy.library.extensions

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Developed by
 * @author Aleksandr Artemov
 */


fun DialogFragment.showDialog(fragmentManager: FragmentManager, tag: String) {
    fragmentManager
        .beginTransaction()
        .add(this, tag)
        .commitAllowingStateLoss()
}

fun DialogFragment.makeFullScreenDialogFragment() {
    dialog?.window?.let {
        val params = it.attributes
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.dimAmount = 0.7f
        it.attributes = params
        it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}

fun DialogFragment.onDialogBackPressed(block: () -> Unit) =
    object : Dialog(requireContext(), theme) {
        override fun onBackPressed() {
            block()
        }
    }