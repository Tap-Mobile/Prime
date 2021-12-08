package com.prime.features.settings.support.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prime.features.settings.support.message.PrimeSupportMessageNavigation.AskAboutClose
import com.prime.features.settings.support.message.PrimeSupportMessageNavigation.Close
import com.prime.features.settings.support.message.PrimeSupportMessageNavigation.SendMail
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
sealed class PrimeSupportMessageNavigation {
    object Close : PrimeSupportMessageNavigation()
    object AskAboutClose : PrimeSupportMessageNavigation()
    data class SendMail(val message: String) : PrimeSupportMessageNavigation()
}

class PrimeSupportMessageViewModel @Inject constructor(

) : ViewModel() {
    private val _navigationAction = MutableLiveData<PrimeSupportMessageNavigation?>(null)
    val navigationAction: LiveData<PrimeSupportMessageNavigation?>
        get() = _navigationAction

    fun onNavigationActionHandled() {
        _navigationAction.value = null
    }

    fun onContinueClicked(title: String, message: String) {
        _navigationAction.value = SendMail("$title\n\n${message.trim()}")
    }

    fun onCloseClicked(message: String) {
        _navigationAction.value = if (message.trim().isEmpty()) Close else AskAboutClose
    }

    fun onCloseConfirmed() {
        _navigationAction.value = Close
    }
}