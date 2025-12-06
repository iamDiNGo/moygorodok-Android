package com.gorod.moygorodok.ui.emergency

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.EmergencyContact
import com.gorod.moygorodok.databinding.ItemEmergencyMainBinding

class MainEmergencyAdapter(
    private val onCallClick: (EmergencyContact) -> Unit
) : ListAdapter<EmergencyContact, MainEmergencyAdapter.MainContactViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainContactViewHolder {
        val binding = ItemEmergencyMainBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MainContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MainContactViewHolder(
        private val binding: ItemEmergencyMainBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: EmergencyContact) {
            binding.apply {
                textIcon.text = contact.icon
                textName.text = contact.shortName
                textPhone.text = contact.phone

                // Set card color
                try {
                    card.setCardBackgroundColor(Color.parseColor(contact.color))
                } catch (e: Exception) {
                    card.setCardBackgroundColor(Color.parseColor("#666666"))
                }

                root.setOnClickListener { onCallClick(contact) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EmergencyContact>() {
        override fun areItemsTheSame(oldItem: EmergencyContact, newItem: EmergencyContact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EmergencyContact, newItem: EmergencyContact): Boolean {
            return oldItem == newItem
        }
    }
}
