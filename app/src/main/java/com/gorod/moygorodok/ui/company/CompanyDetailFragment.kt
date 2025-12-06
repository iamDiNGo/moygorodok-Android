package com.gorod.moygorodok.ui.company

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gorod.moygorodok.databinding.FragmentCompanyDetailBinding
import com.google.android.material.chip.Chip

class CompanyDetailFragment : Fragment() {

    private var _binding: FragmentCompanyDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CompanyDetailViewModel by viewModels()
    private val args: CompanyDetailFragmentArgs by navArgs()

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

        setupToolbar()
        observeViewModel()

        viewModel.loadCompany(args.companyId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.company.observe(viewLifecycleOwner) { company ->
            binding.apply {
                toolbar.title = company.name
                textName.text = company.name
                textCategory.text = "${company.category.emoji} ${company.category.displayName}"
                textDescription.text = company.description
                textAddress.text = company.address
                textPhone.text = company.phone
                textEmail.text = company.email
                textWebsite.text = company.website
                textHours.text = company.workingHours
                textRating.text = company.rating.toString()
                textReviews.text = "${company.reviewsCount} отзывов"

                // Verified badge
                textVerified.visibility = if (company.isVerified) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                // Logo placeholder
                try {
                    logoPlaceholder.setBackgroundColor(Color.parseColor(company.logoColor))
                } catch (e: Exception) {
                    logoPlaceholder.setBackgroundColor(Color.parseColor("#2196F3"))
                }
                textLogoEmoji.text = company.category.emoji

                // Services chips
                chipGroupServices.removeAllViews()
                company.services.forEach { service ->
                    val chip = Chip(requireContext()).apply {
                        text = service
                        isClickable = false
                    }
                    chipGroupServices.addView(chip)
                }

                // Photo placeholders
                if (company.photos.isNotEmpty()) {
                    try {
                        photo1.setBackgroundColor(Color.parseColor(company.photos[0]))
                    } catch (e: Exception) {}
                }
                if (company.photos.size > 1) {
                    try {
                        photo2.setBackgroundColor(Color.parseColor(company.photos[1]))
                    } catch (e: Exception) {}
                }
                if (company.photos.size > 2) {
                    try {
                        photo3.setBackgroundColor(Color.parseColor(company.photos[2]))
                    } catch (e: Exception) {}
                }

                // Click listeners
                btnCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${company.phone}")
                    }
                    startActivity(intent)
                }

                btnWebsite.setOnClickListener {
                    val url = if (company.website.startsWith("http")) {
                        company.website
                    } else {
                        "https://${company.website}"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }

                layoutAddress.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("geo:0,0?q=${Uri.encode(company.address)}")
                    }
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
