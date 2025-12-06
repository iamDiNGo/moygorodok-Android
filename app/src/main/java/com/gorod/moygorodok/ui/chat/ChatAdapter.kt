package com.gorod.moygorodok.ui.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.ChatMessage
import com.gorod.moygorodok.databinding.ItemMessageIncomingBinding
import com.gorod.moygorodok.databinding.ItemMessageOutgoingBinding

class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_INCOMING = 0
        private const val TYPE_OUTGOING = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isOwn) TYPE_OUTGOING else TYPE_INCOMING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_OUTGOING -> OutgoingViewHolder(
                ItemMessageOutgoingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> IncomingViewHolder(
                ItemMessageIncomingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        val showAvatar = position == 0 || getItem(position - 1).senderId != message.senderId

        when (holder) {
            is IncomingViewHolder -> holder.bind(message, showAvatar)
            is OutgoingViewHolder -> holder.bind(message)
        }
    }

    class IncomingViewHolder(
        private val binding: ItemMessageIncomingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage, showAvatar: Boolean) {
            binding.apply {
                textMessage.text = message.text
                textTime.text = message.timestamp
                textSenderName.text = message.senderName

                if (showAvatar) {
                    cardAvatar.visibility = View.VISIBLE
                    textSenderName.visibility = View.VISIBLE
                    cardAvatar.setCardBackgroundColor(Color.parseColor(message.senderAvatar))
                    textAvatarInitial.text = message.senderName.first().toString()
                } else {
                    cardAvatar.visibility = View.INVISIBLE
                    textSenderName.visibility = View.GONE
                }
            }
        }
    }

    class OutgoingViewHolder(
        private val binding: ItemMessageOutgoingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.apply {
                textMessage.text = message.text
                textTime.text = message.timestamp
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
