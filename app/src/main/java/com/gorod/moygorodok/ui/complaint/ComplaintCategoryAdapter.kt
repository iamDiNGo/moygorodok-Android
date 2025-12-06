package com.gorod.moygorodok.ui.complaint

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.ComplaintCategory
import com.gorod.moygorodok.databinding.ItemComplaintCategoryBinding

class ComplaintCategoryAdapter(
    private val onCategoryClick: (ComplaintCategory) -> Unit
) : ListAdapter<ComplaintCategory, ComplaintCategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    private var selectedCategory: ComplaintCategory? = null

    fun setSelectedCategory(category: ComplaintCategory?) {
        val oldSelected = selectedCategory
        selectedCategory = category

        currentList.forEachIndexed { index, item ->
            if (item == oldSelected || item == category) {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemComplaintCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding, onCategoryClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), selectedCategory)
    }

    class CategoryViewHolder(
        private val binding: ItemComplaintCategoryBinding,
        private val onCategoryClick: (ComplaintCategory) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: ComplaintCategory, selectedCategory: ComplaintCategory?) {
            val isSelected = category == selectedCategory

            binding.apply {
                textEmoji.text = category.emoji
                textName.text = category.displayName

                // Set card appearance based on selection
                if (isSelected) {
                    card.strokeWidth = 3
                    card.strokeColor = Color.parseColor(category.color)
                    card.setCardBackgroundColor(Color.parseColor(category.color + "20"))
                } else {
                    card.strokeWidth = 1
                    card.strokeColor = Color.parseColor("#E0E0E0")
                    card.setCardBackgroundColor(Color.WHITE)
                }

                root.setOnClickListener {
                    onCategoryClick(category)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ComplaintCategory>() {
        override fun areItemsTheSame(oldItem: ComplaintCategory, newItem: ComplaintCategory): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ComplaintCategory, newItem: ComplaintCategory): Boolean {
            return oldItem == newItem
        }
    }
}
