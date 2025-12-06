package com.gorod.moygorodok.ui.company

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Company
import com.gorod.moygorodok.databinding.ItemCompanyBinding

class CompanyAdapter(
    private val onCompanyClick: (Company) -> Unit
) : ListAdapter<Company, CompanyAdapter.CompanyViewHolder>(CompanyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val binding = ItemCompanyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CompanyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CompanyViewHolder(
        private val binding: ItemCompanyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(company: Company) {
            binding.apply {
                textName.text = company.name
                textCategory.text = "${company.category.emoji} ${company.category.displayName}"
                textDescription.text = company.shortDescription
                textAddress.text = company.address
                textRating.text = company.rating.toString()
                textReviews.text = "(${company.reviewsCount})"
                textHours.text = company.workingHours

                // Verified badge
                textVerified.visibility = if (company.isVerified) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                // Logo placeholder color
                try {
                    logoPlaceholder.setBackgroundColor(Color.parseColor(company.logoColor))
                } catch (e: Exception) {
                    logoPlaceholder.setBackgroundColor(Color.parseColor("#2196F3"))
                }

                // Category emoji as logo
                textLogoEmoji.text = company.category.emoji

                root.setOnClickListener {
                    onCompanyClick(company)
                }
            }
        }
    }

    class CompanyDiffCallback : DiffUtil.ItemCallback<Company>() {
        override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem == newItem
        }
    }
}
