package com.gorod.moygorodok.ui.events

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Event
import com.gorod.moygorodok.data.model.EventPriceType
import com.gorod.moygorodok.databinding.ItemEventBinding
import java.text.NumberFormat
import java.util.Locale

class EventAdapter(
    private val onEventClick: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onEventClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemEventBinding,
        private val onEventClick: (Event) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.apply {
                // Set colored placeholder background
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 16f * root.context.resources.displayMetrics.density
                    setColor(Color.parseColor(event.imageColor))
                }
                viewImagePlaceholder.background = drawable

                // Category emoji
                textCategoryEmoji.text = event.category.emoji

                // Title and description
                textTitle.text = event.title
                textDescription.text = event.shortDescription

                // Date and time
                textDate.text = event.date
                textTime.text = if (event.endTime != null) {
                    "${event.time} — ${event.endTime}"
                } else {
                    event.time
                }

                // Location
                textLocation.text = event.location.name

                // Price
                textPrice.text = formatPrice(event.price.type, event.price.minPrice, event.price.maxPrice)

                // Age restriction
                if (event.ageRestriction != null) {
                    textAge.text = event.ageRestriction
                    textAge.visibility = android.view.View.VISIBLE
                } else {
                    textAge.visibility = android.view.View.GONE
                }

                // Featured badge
                badgeFeatured.visibility = if (event.isFeatured) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                root.setOnClickListener { onEventClick(event) }
            }
        }

        private fun formatPrice(type: EventPriceType, min: Double?, max: Double?): String {
            return when (type) {
                EventPriceType.FREE -> "Бесплатно"
                EventPriceType.DONATION -> "Свободный вход"
                EventPriceType.PAID -> {
                    val format = NumberFormat.getNumberInstance(Locale("ru", "RU"))
                    when {
                        min == max && min != null -> "${format.format(min)} ₽"
                        min != null && max != null -> "от ${format.format(min)} ₽"
                        min != null -> "от ${format.format(min)} ₽"
                        else -> "Платно"
                    }
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}
