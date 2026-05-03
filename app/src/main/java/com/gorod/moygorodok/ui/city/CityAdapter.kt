package com.gorod.moygorodok.ui.city

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.remote.model.City
import com.gorod.moygorodok.databinding.ItemCityHeaderBinding
import com.gorod.moygorodok.databinding.ItemCityRowBinding

class CityAdapter(
    private val onCityClick: (City) -> Unit
) : ListAdapter<CityListItem, RecyclerView.ViewHolder>(Diff()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ROW = 1
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is CityListItem.Header -> TYPE_HEADER
        is CityListItem.CityRow -> TYPE_ROW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderHolder(ItemCityHeaderBinding.inflate(inflater, parent, false))
            TYPE_ROW -> RowHolder(ItemCityRowBinding.inflate(inflater, parent, false), onCityClick)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CityListItem.Header -> (holder as HeaderHolder).bind(item)
            is CityListItem.CityRow -> (holder as RowHolder).bind(item)
        }
    }

    class HeaderHolder(private val binding: ItemCityHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CityListItem.Header) {
            binding.textHeader.text = item.title
        }
    }

    class RowHolder(
        private val binding: ItemCityRowBinding,
        private val onClick: (City) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CityListItem.CityRow) {
            val city = item.city
            binding.textName.text = city.name
            val subtitle = buildString {
                city.region?.let { append(it) }
                if (item.showDistance && city.distanceKm != null) {
                    if (isNotEmpty()) append(" · ")
                    append(String.format("%.1f км", city.distanceKm))
                }
            }
            if (subtitle.isBlank()) {
                binding.textSubtitle.visibility = View.GONE
            } else {
                binding.textSubtitle.visibility = View.VISIBLE
                binding.textSubtitle.text = subtitle
            }
            binding.root.setOnClickListener { onClick(city) }
        }
    }

    private class Diff : DiffUtil.ItemCallback<CityListItem>() {
        override fun areItemsTheSame(oldItem: CityListItem, newItem: CityListItem): Boolean {
            return when {
                oldItem is CityListItem.Header && newItem is CityListItem.Header ->
                    oldItem.title == newItem.title
                oldItem is CityListItem.CityRow && newItem is CityListItem.CityRow ->
                    oldItem.city.id == newItem.city.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: CityListItem, newItem: CityListItem): Boolean {
            return oldItem == newItem
        }
    }
}
