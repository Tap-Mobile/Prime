package com.prime.features.settings.support.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jakewharton.rxbinding2.widget.RxTextView
import com.lensy.library.extensions.hideKeyboard
import com.lensy.library.extensions.onBackPressed
import com.prime.features.reviews.ContactUsHelper
import com.prime.features.settings.support.model.messageId
import com.prime.features.settings.support.model.titleId
import com.protonvpn.android.utils.ViewModelFactory
import com.vpn.prime.R
import com.vpn.prime.databinding.PrimeSupportMessageFragBinding
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeSupportMessageFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: PrimeSupportMessageViewModel by viewModels { viewModelFactory }
    private val args: PrimeSupportMessageFragmentArgs by navArgs()

    private lateinit var binding: PrimeSupportMessageFragBinding
    private var ctaActivatorDisposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PrimeSupportMessageFragBinding.inflate(inflater, container, false)
        .run {
            binding = this
            root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        onBackPressed { onCloseClicked() }
        message.hint = getString(R.string.prime_supportMessageHint, getString(args.reason.messageId))

        btnClose.setOnClickListener { onCloseClicked() }
        btnContinue.root.setOnClickListener {
            viewModel.onContinueClicked(getString(args.reason.titleId), message.text.toString())
            hideKeyboard()
        }

        ctaActivatorDisposable = RxTextView.textChanges(message)
            .map { it.toString() }
            .map { it.isNotEmpty() }
            .distinctUntilChanged()
            .subscribe(::updateCtaButton)

        viewModel.navigationAction.observe(viewLifecycleOwner, Observer { it?.let { handleNavigation(it) } })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ctaActivatorDisposable?.dispose()
    }

    private fun updateCtaButton(isEnabled: Boolean): Unit = with(binding) {
        btnContinue.btnStartWelcome.setBackgroundResource(
            if (isEnabled) R.drawable.prime_bg_bottom_continue
            else R.drawable.prime_bg_bottom_continue_disabled
        )
        btnContinue.root.isEnabled = isEnabled
    }

    private fun onCloseClicked() = viewModel.onCloseClicked(binding.message.text.toString())

    private fun handleNavigation(navigation: PrimeSupportMessageNavigation) {
        when (navigation) {
            PrimeSupportMessageNavigation.Close -> findNavController().popBackStack().also { hideKeyboard() }
            is PrimeSupportMessageNavigation.SendMail ->
                ContactUsHelper.sendMessageToSupport(
                    requireActivity(),
                    navigation.message
                )
            PrimeSupportMessageNavigation.AskAboutClose ->
                AlertDialog.Builder(requireContext(), R.style.AppAlertDialog)
                    .setTitle(R.string.prime_supportMessageCloseDialogTitle)
                    .setMessage(R.string.prime_supportMessageCloseDialogMessage)
                    .setPositiveButton(R.string.close) { dialog, _ ->
                        dialog.dismiss()
                        viewModel.onCloseConfirmed()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    .setCancelable(true)
                    .show()
        }
        viewModel.onNavigationActionHandled()
    }
}