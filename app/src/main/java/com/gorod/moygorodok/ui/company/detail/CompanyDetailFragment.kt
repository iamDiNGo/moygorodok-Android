package com.gorod.moygorodok.ui.company.detail

import com.gorod.moygorodok.ui.company.common.CompanyCategoryIcons

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.gorod.moygorodok.data.model.CompanyDetail
import com.gorod.moygorodok.data.model.CompanyReview
import com.gorod.moygorodok.data.model.WorkingDay
import com.gorod.moygorodok.databinding.FragmentCompanyDetailBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class CompanyDetailFragment : Fragment() {

    private var _binding: FragmentCompanyDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CompanyDetailViewModel by viewModels()
    private val args: CompanyDetailFragmentArgs by navArgs()

    private val reviewDateFormatter = SimpleDateFormat("d MMMM yyyy", Locale("ru", "RU"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.recyclerPhotos.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        observeViewModel()
        viewModel.loadCompany(args.companyId)
    }

    private fun observeViewModel() {
        viewModel.company.observe(viewLifecycleOwner) { company ->
            company?.let { renderCompany(it) }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun renderCompany(company: CompanyDetail) = with(binding) {
        toolbar.title = company.name
        textName.text = company.name
        textCategory.text = company.category?.name ?: company.kindLabel

        textRating.text = company.rating?.let { String.format("%.1f", it) } ?: "—"
        textReviews.text = "${company.reviewsCount} отзывов"

        if (company.priceRange != null) {
            textPriceRange.text = company.priceRange.symbol
            textPriceRange.visibility = View.VISIBLE
        } else {
            textPriceRange.visibility = View.GONE
        }

        imageVerified.isVisible = company.isVerified

        // Logo
        val accentColor = CompanyCategoryIcons.resolveColor(company.category?.colorHex)
        logoContainer.setBackgroundColor(accentColor)
        imageCategoryIcon.setImageResource(
            CompanyCategoryIcons.resolveIcon(company.category?.key)
        )
        if (!company.logoUrl.isNullOrBlank()) {
            imageLogo.visibility = View.VISIBLE
            imageCategoryIcon.visibility = View.GONE
            imageLogo.load(company.logoUrl)
        } else if (!company.coverPhotoUrl.isNullOrBlank()) {
            imageLogo.visibility = View.VISIBLE
            imageCategoryIcon.visibility = View.GONE
            imageLogo.load(company.coverPhotoUrl)
        } else {
            imageLogo.visibility = View.GONE
            imageCategoryIcon.visibility = View.VISIBLE
        }

        // Open now badge — use working_hours
        renderOpenNow(company.workingHours?.isOpenNow, company.workingHours?.todayLabel)

        // Description
        if (!company.description.isNullOrBlank()) {
            cardDescription.visibility = View.VISIBLE
            textDescription.text = company.description
        } else {
            cardDescription.visibility = View.GONE
        }

        // Services
        renderServices(company.services)

        // Working hours
        renderWorkingHours(company.workingHours?.schedule ?: emptyList(), company.workingHours?.todayDayOfWeek)

        // Contacts
        renderContacts(company)

        // Photos
        if (company.photos.isNotEmpty()) {
            labelPhotos.visibility = View.VISIBLE
            recyclerPhotos.visibility = View.VISIBLE
            recyclerPhotos.adapter = CompanyPhotoAdapter(company.photos)
        } else {
            labelPhotos.visibility = View.GONE
            recyclerPhotos.visibility = View.GONE
        }

        // Recent reviews
        renderReviews(company.recentReviews)
    }

    private fun renderOpenNow(isOpenNow: Boolean?, todayLabel: String?) = with(binding) {
        when (isOpenNow) {
            true -> {
                textOpenNow.text = todayLabel?.let { "Открыто • $it" } ?: "Открыто"
                textOpenNow.setTextColor(0xFF388E3C.toInt())
                textOpenNow.visibility = View.VISIBLE
            }
            false -> {
                textOpenNow.text = todayLabel?.let { "Закрыто • $it" } ?: "Закрыто"
                textOpenNow.setTextColor(0xFFD32F2F.toInt())
                textOpenNow.visibility = View.VISIBLE
            }
            null -> textOpenNow.visibility = View.GONE
        }
    }

    private fun renderServices(services: List<String>) = with(binding) {
        chipGroupServices.removeAllViews()
        if (services.isEmpty()) {
            labelServices.visibility = View.GONE
            chipGroupServices.visibility = View.GONE
            return@with
        }
        labelServices.visibility = View.VISIBLE
        chipGroupServices.visibility = View.VISIBLE
        services.forEach { service ->
            val chip = Chip(requireContext()).apply {
                text = service
                isClickable = false
            }
            chipGroupServices.addView(chip)
        }
    }

    private fun renderWorkingHours(schedule: List<WorkingDay>, todayDayOfWeek: Int?) = with(binding) {
        hoursContainer.removeAllViews()
        if (schedule.isEmpty()) {
            cardHours.visibility = View.GONE
            return@with
        }
        cardHours.visibility = View.VISIBLE
        val dayNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        schedule.sortedBy { it.dayOfWeek }.forEach { day ->
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 6, 0, 6)
            }
            val isToday = day.dayOfWeek == todayDayOfWeek
            val dayLabel = TextView(requireContext()).apply {
                text = dayNames.getOrNull(day.dayOfWeek - 1) ?: "?"
                setTextColor(if (isToday) 0xFF1976D2.toInt() else 0xFF424242.toInt())
                textSize = 14f
                if (isToday) setTypeface(typeface, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val hoursLabel = TextView(requireContext()).apply {
                text = formatDayIntervals(day)
                setTextColor(if (isToday) 0xFF1976D2.toInt() else 0xFF424242.toInt())
                textSize = 14f
                if (isToday) setTypeface(typeface, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
            }
            row.addView(dayLabel)
            row.addView(hoursLabel)
            hoursContainer.addView(row)
        }
    }

    private fun formatDayIntervals(day: WorkingDay): String {
        if (day.isClosed) return "Выходной"
        if (day.intervals.isEmpty()) return "—"
        return day.intervals.joinToString(", ") { "${it.opensAt}–${it.closesAt}" }
    }

    private fun renderContacts(company: CompanyDetail) = with(binding) {
        if (!company.address.isNullOrBlank()) {
            layoutAddress.visibility = View.VISIBLE
            textAddress.text = company.address
            layoutAddress.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(company.address)}"))
                )
            }
        } else {
            layoutAddress.visibility = View.GONE
        }

        if (!company.phone.isNullOrBlank()) {
            layoutPhone.visibility = View.VISIBLE
            textPhone.text = company.phone
            btnCall.isEnabled = true
            btnCall.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${company.phone}")))
            }
        } else {
            layoutPhone.visibility = View.GONE
            btnCall.isEnabled = false
        }

        if (!company.email.isNullOrBlank()) {
            layoutEmail.visibility = View.VISIBLE
            textEmail.text = company.email
        } else {
            layoutEmail.visibility = View.GONE
        }

        if (!company.website.isNullOrBlank()) {
            layoutWebsite.visibility = View.VISIBLE
            textWebsite.text = company.website
            btnWebsite.isEnabled = true
            btnWebsite.setOnClickListener { openWebsite(company.website) }
            textWebsite.setOnClickListener { openWebsite(company.website) }
        } else {
            layoutWebsite.visibility = View.GONE
            btnWebsite.isEnabled = false
        }
    }

    private fun openWebsite(url: String) {
        val normalized = if (url.startsWith("http")) url else "https://$url"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(normalized)))
    }

    private fun renderReviews(reviews: List<CompanyReview>) = with(binding) {
        reviewsContainer.removeAllViews()
        if (reviews.isEmpty()) {
            labelReviews.visibility = View.GONE
            return@with
        }
        labelReviews.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(requireContext())
        reviews.forEach { review ->
            val card = inflater.inflate(
                com.gorod.moygorodok.R.layout.item_company_review,
                reviewsContainer,
                false
            )
            val author = card.findViewById<TextView>(com.gorod.moygorodok.R.id.text_review_author)
            val rating = card.findViewById<TextView>(com.gorod.moygorodok.R.id.text_review_rating)
            val date = card.findViewById<TextView>(com.gorod.moygorodok.R.id.text_review_date)
            val text = card.findViewById<TextView>(com.gorod.moygorodok.R.id.text_review_text)

            author.text = review.author?.name ?: "Аноним"
            rating.text = "★".repeat(review.rating.coerceIn(0, 5))
            date.text = review.publishedAt?.let { reviewDateFormatter.format(it) } ?: "На модерации"
            if (review.text.isNullOrBlank()) {
                text.visibility = View.GONE
            } else {
                text.visibility = View.VISIBLE
                text.text = review.text
            }
            reviewsContainer.addView(card)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
