package com.gorod.moygorodok.ui.delivery

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Product
import com.gorod.moygorodok.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onProductClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemProductBinding,
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                // Image placeholder
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 8f * root.context.resources.displayMetrics.density
                    setColor(Color.parseColor(product.imageColor))
                }
                viewImagePlaceholder.background = drawable

                // Name and description
                textName.text = product.name
                textDescription.text = product.description

                // Weight
                if (product.weight != null) {
                    textWeight.text = product.weight
                    textWeight.visibility = android.view.View.VISIBLE
                } else {
                    textWeight.visibility = android.view.View.GONE
                }

                // Price
                val format = NumberFormat.getNumberInstance(Locale("ru", "RU"))
                textPrice.text = "${format.format(product.price)} ₽"

                // Old price (discount)
                if (product.oldPrice != null) {
                    textOldPrice.text = "${format.format(product.oldPrice)} ₽"
                    textOldPrice.paintFlags = textOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    textOldPrice.visibility = android.view.View.VISIBLE
                } else {
                    textOldPrice.visibility = android.view.View.GONE
                }

                // Popular badge
                badgePopular.visibility = if (product.isPopular) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                // Availability
                if (!product.isAvailable) {
                    root.alpha = 0.5f
                    buttonAdd.isEnabled = false
                } else {
                    root.alpha = 1.0f
                    buttonAdd.isEnabled = true
                }

                buttonAdd.setOnClickListener { onProductClick(product) }
                root.setOnClickListener { onProductClick(product) }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
