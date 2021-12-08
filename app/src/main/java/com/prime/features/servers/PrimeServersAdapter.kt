package com.prime.features.servers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lensy.library.extensions.visible
import com.lensy.library.extensions.visibleGone
import com.vpn.prime.R
import com.vpn.prime.databinding.PrimeServersItemServerCityBinding
import com.vpn.prime.databinding.PrimeServersItemServerCountryBinding
import com.vpn.prime.databinding.PrimeServersItemServerOptimalBinding

/**
 * Developed by
 * @author Aleksandr Artemov
 */

private enum class ViewType {
    Optimal,
    Country,
    City
}

private object SelectedPaylod

sealed class ServerUi(val viewType: Int) {

    abstract val id: String
    abstract val selected: Boolean

    class OptimalLocation(
        override val selected: Boolean,
        override val id: String = "optimal"
    ) : ServerUi(ViewType.Optimal.ordinal)

    data class Country(
        val flag: String,
        val flagImage: String,
        val name: String,
        val cities: Int,
        override val selected: Boolean,
        override val id: String = flag,
    ) : ServerUi(ViewType.Country.ordinal) {
        fun hasManyCities() = cities > 1
    }

    data class City(
        val flag: String,
        val flagImage: String,
        val name: String,
        val isPremium: Boolean,
        override val selected: Boolean,
        override val id: String = flag + name,
    ) : ServerUi(ViewType.City.ordinal)
}

class PrimeServersAdapter(
    private val clickListener: (exportApp: ServerUi) -> Unit
) :
    ListAdapter<ServerUi, PrimeServersAdapter.ServerUiViewHolder<ServerUi>>(ServersDiffCallback) {

    override fun onBindViewHolder(holder: ServerUiViewHolder<ServerUi>, position: Int, payloads: MutableList<Any>) {
        if (payloads.any { it == SelectedPaylod }) {
            holder.bindSelected(getItem(position))
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: ServerUiViewHolder<ServerUi>, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerUiViewHolder<ServerUi> =
        when (viewType) {
            ViewType.Optimal.ordinal -> OptimalServerUiViewHolder.from(parent)
            ViewType.Country.ordinal -> CountryServerUiViewHolder.from(parent)
            ViewType.City.ordinal -> CityServerUiViewHolder.from(parent)
            else -> throw IllegalArgumentException("Unexpected viewType $viewType")
        } as ServerUiViewHolder<ServerUi>

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    abstract class ServerUiViewHolder<Server : ServerUi> constructor(
        root: View
    ) : RecyclerView.ViewHolder(root) {
        abstract fun bind(server: Server, clickListener: (exportApp: ServerUi) -> Unit)

        abstract fun bindSelected(server: ServerUi)
    }

    class OptimalServerUiViewHolder private constructor(
        private val binding: PrimeServersItemServerOptimalBinding
    ) : ServerUiViewHolder<ServerUi.OptimalLocation>(binding.root) {
        override fun bind(server: ServerUi.OptimalLocation, clickListener: (exportApp: ServerUi) -> Unit) =
            with(binding) {
                serverRoot.setOnClickListener { clickListener(server) }

                bindSelected(server)
            }

        override fun bindSelected(server: ServerUi) {
            binding.btnSelected.visible = server.selected
        }

        companion object {
            fun from(
                parent: ViewGroup,
            ) = OptimalServerUiViewHolder(
                PrimeServersItemServerOptimalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    class CountryServerUiViewHolder private constructor(
        private val binding: PrimeServersItemServerCountryBinding
    ) : ServerUiViewHolder<ServerUi.Country>(binding.root) {

        override fun bind(server: ServerUi.Country, clickListener: (exportApp: ServerUi) -> Unit) = with(binding) {
            serverRoot.setOnClickListener { clickListener(server) }
            countryTitle.text = server.name
            citiesInfo.visibleGone = server.hasManyCities()
            citiesInfo.text = itemView.resources
                .getQuantityString(R.plurals.prime_serversCountryCities, server.cities)
                .format(server.cities)
            btnMore.visible = server.hasManyCities() && server.selected.not()

            Glide.with(countryFlag)
                .load(server.flagImage)
                .circleCrop()
                .placeholder(R.drawable.prime_servers_bg_flag)
                .into(countryFlag)
            bindSelected(server)
        }

        override fun bindSelected(server: ServerUi) {
            binding.btnSelected.visibleGone = server.selected
        }

        companion object {
            fun from(
                parent: ViewGroup,
            ) = CountryServerUiViewHolder(
                PrimeServersItemServerCountryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    class CityServerUiViewHolder private constructor(
        private val binding: PrimeServersItemServerCityBinding
    ) : ServerUiViewHolder<ServerUi.City>(binding.root) {

        override fun bind(server: ServerUi.City, clickListener: (exportApp: ServerUi) -> Unit) = with(binding) {
            serverRoot.setOnClickListener { clickListener(server) }
            countryTitle.text = server.name
            premiumLabel.visibleGone = server.isPremium
            Glide.with(countryFlag)
                .load(server.flagImage)
                .circleCrop()
                .placeholder(R.drawable.prime_servers_bg_flag)
                .into(countryFlag)
            bindSelected(server)
        }

        override fun bindSelected(server: ServerUi) {
            binding.btnSelected.visibleGone = server.selected
        }

        companion object {
            fun from(
                parent: ViewGroup,
            ) = CityServerUiViewHolder(
                PrimeServersItemServerCityBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    companion object ServersDiffCallback : DiffUtil.ItemCallback<ServerUi>() {
        override fun areItemsTheSame(oldItem: ServerUi, newItem: ServerUi): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ServerUi, newItem: ServerUi): Boolean =
            oldItem == newItem

        override fun getChangePayload(oldItem: ServerUi, newItem: ServerUi): Any? =
            when {
                oldItem.selected != newItem.selected -> SelectedPaylod
                else -> super.getChangePayload(oldItem, newItem)
            }
    }
}