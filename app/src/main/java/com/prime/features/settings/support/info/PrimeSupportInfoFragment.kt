package com.prime.features.settings.support.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.prime.features.settings.support.model.SupportReason
import com.prime.features.settings.support.model.SupportReason.CONNECTION_PROBLEMS
import com.prime.features.settings.support.model.SupportReason.OTHER
import com.prime.features.settings.support.model.SupportReason.SLOW_INTERNET
import com.vpn.prime.databinding.PrimeSupportInfoFragBinding
import dagger.android.support.DaggerFragment

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeSupportInfoFragment : DaggerFragment() {

    private lateinit var binding: PrimeSupportInfoFragBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PrimeSupportInfoFragBinding.inflate(inflater, container, false)
        .run {
            binding = this
            root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        btnReasonConnectionProblems.setOnClickListener { openMessageScreen(CONNECTION_PROBLEMS) }
        btnReasonSlowInternet.setOnClickListener { openMessageScreen(SLOW_INTERNET) }
        btnReasonOther.setOnClickListener { openMessageScreen(OTHER) }
    }

    private fun openMessageScreen(reason: SupportReason) =
        findNavController().navigate(PrimeSupportInfoFragmentDirections.openSupportMessage(reason))
}