package com.gorod.moygorodok.ui.delivery_admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.EditableCategory
import com.gorod.moygorodok.databinding.ItemAdminCategoryBinding

class AdminCategoryAdapter(
    private val onCategoryClick: (EditableCategory) -> Unit,
    private val onVisibilityToggle: (EditableCategory) -> Unit
) : ListAdapter<EditableCategory, AdminCategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemAdminCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemAdminCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: EditableCategory) {
            binding.apply {
                textCategoryName.text = category.name
                switchVisible.isChecked = category.isVisible
                textSortOrder.text = "#${category.sortOrder + 1}"

                switchVisible.setOnCheckedChangeListener { _, _ ->
                    onVisibilityToggle(category)
                }

                root.setOnClickListener { onCategoryClick(category) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EditableCategory>() {
        override fun areItemsTheSame(oldItem: EditableCategory, newItem: EditableCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EditableCategory, newItem: EditableCategory): Boolean {
            return oldItem == newItem
        }
    }
}
