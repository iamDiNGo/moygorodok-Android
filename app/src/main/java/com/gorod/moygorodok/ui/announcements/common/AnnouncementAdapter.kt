package com.gorod.moygorodok.ui.announcements.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.Announcement
import com.gorod.moygorodok.databinding.ItemAnnouncementBinding

class AnnouncementAdapter(
    private val onClick: (Announcement) -> Unit
) : ListAdapter<Announcement, AnnouncementAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnnouncementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemAnnouncementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Announcement) {
            val context = binding.root.context
            binding.textTitle.text = item.title
            binding.textPrice.text = item.priceFormatted
            binding.textCategory.text = item.categoryLabel
            binding.textDate.text = item.timeAgo()
            binding.textViews.text = context.getString(R.string.announcement_views_count_short, item.viewsCount)
            binding.iconFavorite.visibility = if (item.isFavorite) android.view.View.VISIBLE else android.view.View.GONE

            val placeholder = AnnouncementCategoryIcons.iconRes(item.category)
            val thumbnail = item.thumbnailUrl
            if (!thumbnail.isNullOrBlank()) {
                binding.imageThumbnail.load(thumbnail) {
                    placeholder(placeholder)
                    error(placeholder)
                    crossfade(true)
                }
            } else {
                binding.imageThumbnail.setImageResource(placeholder)
            }

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Announcement>() {
        override fun areItemsTheSame(oldItem: Announcement, newItem: Announcement) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Announcement, newItem: Announcement) =
            oldItem == newItem
    }
}
