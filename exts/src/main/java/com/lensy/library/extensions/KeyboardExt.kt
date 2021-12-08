package com.lensy.library.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment


/**
 * Developed by
 * @author Aleksandr Artemov
 */
fun Fragment.hideKeyboard() {
    view?.let { hideKeyboard(it) }
}

fun Fragment.hideKeyboard(view: View) {
    activity?.hideKeyboard(view)
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.showKeyboard(view: View) {
    val focus = view.requestFocus()
    if (focus) {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE)?.let {
            (it as InputMethodManager).showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }
    }
}