package com.gorod.moygorodok.ui.delivery_admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Promotion
import com.gorod.moygorodok.databinding.ItemAdminPromotionBinding

class AdminPromotionAdapter(
    private val onPromotionClick: (Promotion) -> Unit,
    private val onActiveToggle: (Promotion) -> Unit
) : ListAdapter<Promotion, AdminPromotionAdapter.PromotionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val binding = ItemAdminPromotionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PromotionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PromotionViewHolder(
        private val binding: ItemAdminPromotionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(promotion: Promotion) {
            binding.apply {
                textPromotionTitle.text = promotion.title
                textPromotionDates.text = "${promotion.startDate} - ${promotion.endDate}"
                switchActive.isChecked = promotion.isActive

                // Discount info
                val discountText = when {
                    promotion.discountPercent != null -> "-${promotion.discountPercent}%"
                    promotion.discountAmount != null -> "-${promotion.discountAmount.toInt()} ₽"
                    else -> ""
                }
                textDiscount.text = discountText

                // Color indicator
                try {
                    viewColorIndicator.setBackgroundColor(Color.parseColor(promotion.imageColor))
                } catch (e: Exception) {
                    viewColorIndicator.setBackgroundColor(Color.parseColor("#CCCCCC"))
                }

                switchActive.setOnCheckedChangeListener { _, _ ->
                    onActiveToggle(promotion)
                }

                root.setOnClickListener { onPromotionClick(promotion) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Promotion>() {
        override fun areItemsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
            return oldItem == newItem
        }
    }
}
