package com.gorod.moygorodok.ui.emergency

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.EmergencyContact
import com.gorod.moygorodok.databinding.ItemEmergencyContactBinding

class EmergencyAdapter(
    private val onCallClick: (EmergencyContact) -> Unit
) : ListAdapter<EmergencyContact, EmergencyAdapter.ContactViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemEmergencyContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ContactViewHolder(
        private val binding: ItemEmergencyContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: EmergencyContact) {
            binding.apply {
                textIcon.text = contact.icon
                textName.text = contact.shortName
                textPhone.text = contact.phone
                textDescription.text = contact.description

                // Set color
                try {
                    cardIcon.setCardBackgroundColor(Color.parseColor(contact.color))
                } catch (e: Exception) {
                    cardIcon.setCardBackgroundColor(Color.parseColor("#666666"))
                }

                buttonCall.setOnClickListener { onCallClick(contact) }
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
