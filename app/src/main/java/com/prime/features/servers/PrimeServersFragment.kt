package com.prime.features.servers

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lensy.library.extensions.onBackPressed
import com.lensy.library.extensions.visible
import com.prime.features.navigation.NavigationHelper
import com.prime.features.servers.model.PrimeServersNavigation
import com.protonvpn.android.utils.ViewModelFactory
import com.vpn.prime.R
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Developed by
 * @author Aleksandr Artemov
 */
abstract class PrimeServersFragment<out ViewModel : PrimeServersViewModel, Binding : ViewBinding> : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    abstract val viewModel: ViewModel
    lateinit var binding: Binding

    abstract val serversList: RecyclerView
    abstract val btnGoPremium: View
    abstract val btnAccessAllLocations: View
    abstract val btnClose: View

    private val adapter: PrimeServersAdapter
        get() = serversList.adapter as PrimeServersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        observeLiveEvents()
        onBackPressed { viewModel.onCloseClicked() }

        btnAccessAllLocations.setOnClickListener { viewModel.onAccessAllClicked() }
        btnGoPremium.setOnClickListener { viewModel.onGoPremiumClicked() }
        btnClose.setOnClickListener { viewModel.onCloseClicked() }

        viewModel.userData.premiumLiveData.observe(viewLifecycleOwner, Observer { updatePremiumUI(it) })
        viewModel.navigationAction.observe(viewLifecycleOwner, Observer { it?.let { handleNavigation(it) } })
    }

    protected open fun handleNavigation(navigation: PrimeServersNavigation) {
        when (navigation) {
            PrimeServersNavigation.Close -> findNavController().popBackStack()
            PrimeServersNavigation.OpenIap -> openIapScreen()
            PrimeServersNavigation.CloseFlow -> findNavController().popBackStack(R.id.mainFragment, false)
            else -> throw IllegalStateException("Unhandled navigation action $navigation")
        }
        viewModel.onNavigationActionHandled()
    }

    private fun openIapScreen() = NavigationHelper.startIapScreen(this)

    private fun updatePremiumUI(isPremium: Boolean): Unit = with(binding) {
        btnGoPremium.visible = !isPremium
        btnAccessAllLocations.visible = !isPremium
    }

    private fun initList() = with(binding) {
        serversList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        serversList.adapter = PrimeServersAdapter { country ->
            viewModel.onServerSelected(country)
        }
        updateListData()
    }

    private fun observeLiveEvents() {
        viewModel.userData.updateEvent.observe(viewLifecycleOwner) { updateListData() }
        viewModel.serverManager.updateEvent.observe(viewLifecycleOwner) { updateListData() }
    }

    private fun updateListData() {
        adapter.submitList(viewModel.getServerList().toList())
    }
}