package com.gorod.moygorodok.ui.emergency

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.EmergencyCategory
import com.gorod.moygorodok.data.model.EmergencyContact
import com.gorod.moygorodok.databinding.ItemEmergencyContactBinding
import com.gorod.moygorodok.databinding.ItemEmergencySectionHeaderBinding

class EmergencyAdapter(
    private val onCallClick: (EmergencyContact) -> Unit
) : ListAdapter<EmergencyAdapter.Row, RecyclerView.ViewHolder>(DiffCallback()) {

    sealed class Row {
        data class Header(
            val category: EmergencyCategory,
            val color: String,
            val iconKey: String
        ) : Row()

        data class Contact(val contact: EmergencyContact) : Row()
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Row.Header -> VIEW_TYPE_HEADER
        is Row.Contact -> VIEW_TYPE_CONTACT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemEmergencySectionHeaderBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_CONTACT -> ContactViewHolder(
                ItemEmergencyContactBinding.inflate(inflater, parent, false)
            )
            else -> error("Unknown viewType=$viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is Row.Header -> (holder as HeaderViewHolder).bind(row)
            is Row.Contact -> (holder as ContactViewHolder).bind(row.contact)
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemEmergencySectionHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(row: Row.Header) {
            binding.apply {
                textCategory.text = row.category.displayName
                imageIcon.setImageResource(EmergencyIconMapper.drawableRes(row.iconKey))
                cardIcon.setCardBackgroundColor(parseColorSafe(row.color))
            }
        }
    }

    inner class ContactViewHolder(
        private val binding: ItemEmergencyContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: EmergencyContact) {
            binding.apply {
                textName.text = contact.name
                textPhone.text = contact.phone
                imageIcon.setImageResource(EmergencyIconMapper.drawableRes(contact.iconKey))
                cardIcon.setCardBackgroundColor(parseColorSafe(contact.color))

                badge24h.visibility = if (contact.is24h) android.view.View.VISIBLE else android.view.View.GONE

                val subtitle = when {
                    !contact.description.isNullOrBlank() -> contact.description
                    !contact.is24h && !contact.workingHours.isNullOrBlank() -> contact.workingHours
                    else -> null
                }
                if (subtitle != null) {
                    textSubtitle.text = subtitle
                    textSubtitle.visibility = android.view.View.VISIBLE
                } else {
                    textSubtitle.visibility = android.view.View.GONE
                }

                buttonCall.setOnClickListener { onCallClick(contact) }
                cardRoot.setOnClickListener { onCallClick(contact) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Row>() {
        override fun areItemsTheSame(oldItem: Row, newItem: Row): Boolean = when {
            oldItem is Row.Header && newItem is Row.Header -> oldItem.category == newItem.category
            oldItem is Row.Contact && newItem is Row.Contact -> oldItem.contact.id == newItem.contact.id
            else -> false
        }

        override fun areContentsTheSame(oldItem: Row, newItem: Row): Boolean = oldItem == newItem
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_CONTACT = 1

        private fun parseColorSafe(hex: String): Int = try {
            Color.parseColor(hex)
        } catch (e: IllegalArgumentException) {
            Color.parseColor("#666666")
        }
    }
}
