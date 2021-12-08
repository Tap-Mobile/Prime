package com.prime.features.servers.cities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.prime.features.servers.PrimeServersFragment
import com.vpn.prime.databinding.PrimeServersFragBinding

/**
 * Developed by
 * @author Aleksandr Artemov
 */
class PrimeCitiesFragment : PrimeServersFragment<PrimeCitiesViewModel, PrimeServersFragBinding>() {

    override val viewModel: PrimeCitiesViewModel by viewModels { viewModelFactory }

    override val serversList: RecyclerView get() = binding.serversList
    override val btnGoPremium: View get() = binding.btnGoPremium.root
    override val btnAccessAllLocations: View get() = binding.btnAccessAllLocations
    override val btnClose: View get() = binding.btnClose

    private val args: PrimeCitiesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PrimeServersFragBinding.inflate(inflater, container, false)
        .run {
            binding = this
            root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.flag
    }
}