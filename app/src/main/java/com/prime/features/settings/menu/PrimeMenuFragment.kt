package com.prime.features.settings.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lensy.library.extensions.preventSecondTouch
import com.lensy.library.extensions.visibleGone
import com.prime.features.main.PrimeMainViewModel
import com.prime.features.navigation.NavigationHelper
import com.prime.features.reviews.RateUsManager
import com.prime.features.reviews.RateUsPlacement
import com.prime.utils.ShareAppHelper
import com.vpn.prime.databinding.PrimeMenuFragBinding
import com.protonvpn.android.utils.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeMenuFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var rateUsManager: RateUsManager

    private val viewModel: PrimeMainViewModel by viewModels { viewModelFactory }
    private lateinit var binding: PrimeMenuFragBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PrimeMenuFragBinding.inflate(inflater, container, false)
        .run {
            binding = this
            root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        btnGoPremium.setOnClickListener {
            NavigationHelper.startIapScreen(this@PrimeMenuFragment)
        }
        btnContactSupport.setOnClickListener {
            findNavController().navigate(PrimeMenuFragmentDirections.openSupportInfo())
        }
        btnShare.setOnClickListener {
            it.preventSecondTouch(500)
            ShareAppHelper.shareApp(requireContext())
        }
        btnRateUs.setOnClickListener {
            it.preventSecondTouch()
            rateUsManager.showRateUs(requireActivity(), RateUsPlacement.MENU)
        }

        viewModel.let { vm ->
            vm.userData.premiumLiveData.observe(viewLifecycleOwner, Observer { updatePremiumUI(it) })
        }
    }

    private fun updatePremiumUI(isPremium: Boolean): Unit = with(binding) {
        btnGoPremium.visibleGone = !isPremium
    }
}