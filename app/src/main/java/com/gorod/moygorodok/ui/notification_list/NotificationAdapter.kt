package com.gorod.moygorodok.ui.notification_list

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.AppNotification
import com.gorod.moygorodok.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val onNotificationClick: (AppNotification) -> Unit,
    private val onDeleteClick: (AppNotification) -> Unit
) : ListAdapter<AppNotification, NotificationAdapter.NotificationViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding, onNotificationClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding,
        private val onNotificationClick: (AppNotification) -> Unit,
        private val onDeleteClick: (AppNotification) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: AppNotification) {
            binding.apply {
                // Icon and color
                textIcon.text = notification.type.emoji
                cardIcon.setCardBackgroundColor(Color.parseColor(notification.type.color + "20"))

                // Content
                textTitle.text = notification.title
                textMessage.text = notification.message
                textTime.text = notification.timestamp
                textType.text = notification.type.displayName

                // Read state styling
                if (notification.isRead) {
                    textTitle.setTypeface(null, Typeface.NORMAL)
                    textTitle.setTextColor(Color.parseColor("#757575"))
                    textMessage.setTextColor(Color.parseColor("#9E9E9E"))
                    indicatorUnread.visibility = View.GONE
                    card.setCardBackgroundColor(Color.parseColor("#FAFAFA"))
                } else {
                    textTitle.setTypeface(null, Typeface.BOLD)
                    textTitle.setTextColor(Color.parseColor("#212121"))
                    textMessage.setTextColor(Color.parseColor("#616161"))
                    indicatorUnread.visibility = View.VISIBLE
                    card.setCardBackgroundColor(Color.WHITE)
                }

                // Click listeners
                root.setOnClickListener {
                    onNotificationClick(notification)
                }

                buttonDelete.setOnClickListener {
                    onDeleteClick(notification)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AppNotification>() {
        override fun areItemsTheSame(oldItem: AppNotification, newItem: AppNotification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AppNotification, newItem: AppNotification): Boolean {
            return oldItem == newItem
        }
    }
}
