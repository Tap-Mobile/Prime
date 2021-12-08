package com.prime.features.servers.countries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.prime.features.servers.PrimeServersFragment
import com.prime.features.servers.model.PrimeServersNavigation
import com.vpn.prime.databinding.PrimeServersFragBinding

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeCountriesFragment : PrimeServersFragment<PrimeCountriesViewModel, PrimeServersFragBinding>() {

    override val viewModel: PrimeCountriesViewModel by viewModels { viewModelFactory }

    override val serversList: RecyclerView get() = binding.serversList
    override val btnGoPremium: View get() = binding.btnGoPremium.root
    override val btnAccessAllLocations: View get() = binding.btnAccessAllLocations
    override val btnClose: View get() = binding.btnClose

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PrimeServersFragBinding.inflate(inflater, container, false)
        .run {
            binding = this
            root
        }

    override fun handleNavigation(navigation: PrimeServersNavigation) {
        if (navigation is PrimeServersNavigation.Countries)
            when (navigation) {
                is PrimeServersNavigation.Countries.OpenCountry ->
                    findNavController().navigate(PrimeCountriesFragmentDirections.openCities(navigation.flag))
            }
        else
            super.handleNavigation(navigation)


        viewModel.onNavigationActionHandled()
    }
}