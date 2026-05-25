package com.gorod.moygorodok.ui.company

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.gorod.moygorodok.data.model.Company
import com.gorod.moygorodok.databinding.ItemCompanyBinding

class CompanyAdapter(
    private val onCompanyClick: (Company) -> Unit
) : ListAdapter<Company, CompanyAdapter.CompanyViewHolder>(CompanyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val binding = ItemCompanyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompanyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CompanyViewHolder(
        private val binding: ItemCompanyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(company: Company) = with(binding) {
            textName.text = company.name
            textCategory.text = company.category?.name ?: company.kindLabel
            textAddress.text = company.address?.takeIf { it.isNotBlank() } ?: "—"

            textRating.text = company.rating?.let { String.format("%.1f", it) } ?: "—"
            textReviews.text = if (company.reviewsCount > 0) "(${company.reviewsCount})" else ""

            textPriceRange.apply {
                if (company.priceRange != null) {
                    text = company.priceRange.symbol
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }

            textOpenNow.apply {
                when (company.isOpenNow) {
                    true -> {
                        text = "Открыто"
                        setTextColor(0xFF388E3C.toInt())
                        visibility = View.VISIBLE
                    }
                    false -> {
                        text = "Закрыто"
                        setTextColor(0xFFD32F2F.toInt())
                        visibility = View.VISIBLE
                    }
                    null -> visibility = View.GONE
                }
            }

            imageVerified.visibility = if (company.isVerified) View.VISIBLE else View.GONE

            val accentColor = CompanyCategoryIcons.resolveColor(company.category?.colorHex)
            logoContainer.setBackgroundColor(accentColor)
            imageCategoryIcon.setImageResource(
                CompanyCategoryIcons.resolveIcon(company.category?.key)
            )

            val artworkUrl = company.logoUrl ?: company.coverPhotoUrl
            if (artworkUrl != null) {
                imageLogo.visibility = View.VISIBLE
                imageCategoryIcon.visibility = View.GONE
                imageLogo.load(artworkUrl)
            } else {
                imageLogo.visibility = View.GONE
                imageCategoryIcon.visibility = View.VISIBLE
            }

            root.setOnClickListener { onCompanyClick(company) }
        }
    }

    class CompanyDiffCallback : DiffUtil.ItemCallback<Company>() {
        override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean =
            oldItem == newItem
    }
}
