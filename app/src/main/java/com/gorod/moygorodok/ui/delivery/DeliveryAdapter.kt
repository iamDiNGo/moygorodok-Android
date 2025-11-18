package com.gorod.moygorodok.ui.delivery

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Delivery
import com.gorod.moygorodok.databinding.ItemDeliveryBinding

class DeliveryAdapter(
    private val onDeliveryClick: (Delivery) -> Unit
) : ListAdapter<Delivery, DeliveryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeliveryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onDeliveryClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemDeliveryBinding,
        private val onDeliveryClick: (Delivery) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(delivery: Delivery) {
            binding.apply {
                // Image placeholder
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 12f * root.context.resources.displayMetrics.density
                    setColor(Color.parseColor(delivery.imageColor))
                }
                viewImagePlaceholder.background = drawable
                textEmoji.text = delivery.category.emoji

                // Name and description
                textName.text = delivery.name
                textDescription.text = delivery.description

                // Rating
                textRating.text = delivery.rating.toString()
                textReviews.text = "(${delivery.reviewCount})"

                // Delivery info
                textDeliveryTime.text = delivery.deliveryTime
                textDeliveryPrice.text = if (delivery.deliveryPrice > 0) {
                    "Доставка ${delivery.deliveryPrice.toInt()} ₽"
                } else {
                    "Бесплатная доставка"
                }

                // Min order
                textMinOrder.text = "От ${delivery.minOrder.toInt()} ₽"

                // Closed badge
                if (!delivery.isOpen) {
                    badgeClosed.visibility = android.view.View.VISIBLE
                    root.alpha = 0.6f
                } else {
                    badgeClosed.visibility = android.view.View.GONE
                    root.alpha = 1.0f
                }

                // Promo badge
                if (delivery.hasPromo && delivery.promoText != null) {
                    textPromo.text = delivery.promoText
                    textPromo.visibility = android.view.View.VISIBLE
                } else {
                    textPromo.visibility = android.view.View.GONE
                }

                root.setOnClickListener { onDeliveryClick(delivery) }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Delivery>() {
        override fun areItemsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
            return oldItem == newItem
        }
    }
}
