package com.gorod.moygorodok.ui.complaint

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.databinding.ItemAttachedImageBinding

class AttachedImageAdapter(
    private val onRemoveClick: (Int) -> Unit
) : ListAdapter<String, AttachedImageAdapter.ImageViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemAttachedImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding, onRemoveClick)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class ImageViewHolder(
        private val binding: ItemAttachedImageBinding,
        private val onRemoveClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageColor: String, position: Int) {
            binding.apply {
                // Use color as placeholder for image
                cardImage.setCardBackgroundColor(Color.parseColor(imageColor))
                textPlaceholder.text = "📷"

                buttonRemove.setOnClickListener {
                    onRemoveClick(position)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
