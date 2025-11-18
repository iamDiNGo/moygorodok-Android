package com.gorod.moygorodok.ui.ads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Ad
import com.gorod.moygorodok.data.model.PriceType
import com.gorod.moygorodok.databinding.ItemAdBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class AdAdapter(
    private val onAdClick: (Ad) -> Unit,
    private val onFavoriteClick: (Ad) -> Unit
) : ListAdapter<Ad, AdAdapter.AdViewHolder>(AdDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val binding = ItemAdBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AdViewHolder(
        private val binding: ItemAdBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        private val priceFormat = NumberFormat.getNumberInstance(Locale("ru"))

        fun bind(ad: Ad) {
            binding.textTitle.text = ad.title
            binding.textLocation.text = ad.location
            binding.textDate.text = dateFormat.format(ad.createdAt)
            binding.textViews.text = "${ad.viewCount}"

            // Форматирование цены
            binding.textPrice.text = when (ad.priceType) {
                PriceType.FREE -> "Бесплатно"
                PriceType.EXCHANGE -> "Обмен"
                PriceType.NEGOTIABLE -> "${priceFormat.format(ad.price)} ₽ (торг)"
                PriceType.FIXED -> "${priceFormat.format(ad.price)} ₽"
            }

            // Иконка избранного
            binding.buttonFavorite.isSelected = ad.isFavorite

            binding.root.setOnClickListener {
                onAdClick(ad)
            }

            binding.buttonFavorite.setOnClickListener {
                onFavoriteClick(ad)
            }
        }
    }

    private class AdDiffCallback : DiffUtil.ItemCallback<Ad>() {
        override fun areItemsTheSame(oldItem: Ad, newItem: Ad): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Ad, newItem: Ad): Boolean {
            return oldItem == newItem
        }
    }
}
