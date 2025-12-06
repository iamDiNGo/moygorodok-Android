package com.gorod.moygorodok.ui.events

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.Event
import com.gorod.moygorodok.data.model.EventPriceType
import com.gorod.moygorodok.databinding.FragmentEventDetailBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()

        val eventId = arguments?.getString("eventId") ?: return
        viewModel.loadEvent(eventId)

        observeViewModel()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_event_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_favorite -> {
                        viewModel.toggleFavorite()
                        true
                    }
                    R.id.action_share -> {
                        shareEvent()
                        true
                    }
                    else -> false
                }
            }

            override fun onPrepareMenu(menu: Menu) {
                val favoriteItem = menu.findItem(R.id.action_favorite)
                val isFavorite = viewModel.isFavorite.value ?: false
                favoriteItem?.setIcon(
                    if (isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeViewModel() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            event?.let { updateUI(it) }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) {
            requireActivity().invalidateOptionsMenu()
        }
    }

    private fun updateUI(event: Event) {
        binding.apply {
            // Header image placeholder
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor(event.imageColor))
            }
            viewImagePlaceholder.background = drawable
            textHeaderEmoji.text = event.category.emoji

            // Category badge
            textCategory.text = "${event.category.emoji} ${event.category.displayName}"

            // Title
            textTitle.text = event.title

            // Date and time
            textDateTime.text = if (event.endTime != null) {
                "${event.date}, ${event.time} — ${event.endTime}"
            } else {
                "${event.date}, ${event.time}"
            }

            // Location
            textLocationName.text = event.location.name
            textLocationAddress.text = event.location.address

            // Price
            textPrice.text = formatPrice(event.price.type, event.price.minPrice, event.price.maxPrice)

            // Price badge color
            val priceColor = when (event.price.type) {
                EventPriceType.FREE -> "#34C759"
                EventPriceType.DONATION -> "#007AFF"
                EventPriceType.PAID -> "#FF9500"
            }
            val priceBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 8f * resources.displayMetrics.density
                setColor(Color.parseColor(priceColor))
            }
            textPrice.background = priceBg

            // Age restriction
            if (event.ageRestriction != null) {
                textAge.text = event.ageRestriction
                textAge.visibility = View.VISIBLE
            } else {
                textAge.visibility = View.GONE
            }

            // Description
            textDescription.text = event.description

            // Tags
            chipGroupTags.removeAllViews()
            event.tags.forEach { tag ->
                val chip = Chip(requireContext()).apply {
                    text = tag
                    isClickable = false
                }
                chipGroupTags.addView(chip)
            }

            // Organizer
            textOrganizerName.text = event.organizerName

            // Phone button
            if (event.organizerPhone != null) {
                buttonCall.visibility = View.VISIBLE
                buttonCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${event.organizerPhone}")
                    }
                    startActivity(intent)
                }
            } else {
                buttonCall.visibility = View.GONE
            }

            // Website button
            if (event.website != null) {
                buttonWebsite.visibility = View.VISIBLE
                buttonWebsite.setOnClickListener {
                    val url = if (event.website.startsWith("http")) {
                        event.website
                    } else {
                        "https://${event.website}"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
            } else {
                buttonWebsite.visibility = View.GONE
            }

            // Buy tickets button
            if (event.price.type == EventPriceType.PAID) {
                buttonBuyTickets.visibility = View.VISIBLE
                buttonBuyTickets.setOnClickListener {
                    Snackbar.make(binding.root, "Покупка билетов в разработке", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                buttonBuyTickets.visibility = View.GONE
            }

            // Location map click
            cardLocation.setOnClickListener {
                Snackbar.make(binding.root, "Открытие карты в разработке", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatPrice(type: EventPriceType, min: Double?, max: Double?): String {
        return when (type) {
            EventPriceType.FREE -> "Бесплатно"
            EventPriceType.DONATION -> "Свободный вход"
            EventPriceType.PAID -> {
                val format = NumberFormat.getNumberInstance(Locale("ru", "RU"))
                when {
                    min == max && min != null -> "${format.format(min)} ₽"
                    min != null && max != null -> "${format.format(min)} — ${format.format(max)} ₽"
                    min != null -> "от ${format.format(min)} ₽"
                    else -> "Платно"
                }
            }
        }
    }

    private fun shareEvent() {
        val event = viewModel.event.value ?: return
        val shareText = """
            ${event.title}

            ${event.date}, ${event.time}
            ${event.location.name}
            ${event.location.address}

            ${event.shortDescription}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Поделиться событием"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
