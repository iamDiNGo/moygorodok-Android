package com.gorod.moygorodok.ui.delivery

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.Delivery
import com.gorod.moygorodok.databinding.FragmentDeliveryDetailBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class DeliveryDetailFragment : Fragment() {

    private var _binding: FragmentDeliveryDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeliveryDetailViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupAdapter()

        val deliveryId = arguments?.getString("deliveryId") ?: return
        viewModel.loadDelivery(deliveryId)

        observeViewModel()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_delivery_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_favorite -> {
                        viewModel.toggleFavorite()
                        true
                    }
                    R.id.action_share -> {
                        shareDelivery()
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

    private fun setupAdapter() {
        productAdapter = ProductAdapter { product ->
            Snackbar.make(
                binding.root,
                "${product.name} добавлен в корзину",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        binding.recyclerProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.delivery.observe(viewLifecycleOwner) { delivery ->
            delivery?.let { updateUI(it) }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) {
            requireActivity().invalidateOptionsMenu()
        }

        viewModel.selectedCategoryIndex.observe(viewLifecycleOwner) { index ->
            val delivery = viewModel.delivery.value ?: return@observe
            if (index in delivery.menuCategories.indices) {
                productAdapter.submitList(delivery.menuCategories[index].products)
                updateCategoryChips(index)
            }
        }
    }

    private fun updateUI(delivery: Delivery) {
        binding.apply {
            // Header
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor(delivery.imageColor))
            }
            viewImagePlaceholder.background = drawable
            textHeaderEmoji.text = delivery.category.emoji

            // Name and description
            textName.text = delivery.name
            textDescription.text = delivery.description

            // Rating
            textRating.text = delivery.rating.toString()
            textReviews.text = "(${delivery.reviewCount} отзывов)"

            // Delivery info
            textDeliveryTime.text = delivery.deliveryTime
            textDeliveryPrice.text = if (delivery.deliveryPrice > 0) {
                "Доставка ${delivery.deliveryPrice.toInt()} ₽"
            } else {
                "Бесплатная доставка"
            }
            textMinOrder.text = "Минимальный заказ ${delivery.minOrder.toInt()} ₽"

            // Working hours and status
            textWorkingHours.text = delivery.workingHours
            if (!delivery.isOpen) {
                textStatus.text = "Закрыто"
                textStatus.setTextColor(Color.parseColor("#FF3B30"))
            } else {
                textStatus.text = "Открыто"
                textStatus.setTextColor(Color.parseColor("#34C759"))
            }

            // Call button
            buttonCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${delivery.phone}")
                }
                startActivity(intent)
            }

            // Setup menu category chips
            setupMenuCategories(delivery)
        }
    }

    private fun setupMenuCategories(delivery: Delivery) {
        binding.chipGroupMenuCategories.removeAllViews()

        delivery.menuCategories.forEachIndexed { index, category ->
            val chip = Chip(requireContext()).apply {
                text = category.name
                isCheckable = true
                isChecked = index == 0
                tag = index
                setOnClickListener {
                    viewModel.selectCategory(index)
                }
            }
            binding.chipGroupMenuCategories.addView(chip)
        }

        // Show first category products
        if (delivery.menuCategories.isNotEmpty()) {
            productAdapter.submitList(delivery.menuCategories[0].products)
        }
    }

    private fun updateCategoryChips(selectedIndex: Int) {
        for (i in 0 until binding.chipGroupMenuCategories.childCount) {
            val chip = binding.chipGroupMenuCategories.getChildAt(i) as? Chip
            chip?.isChecked = chip?.tag == selectedIndex
        }
    }

    private fun shareDelivery() {
        val delivery = viewModel.delivery.value ?: return
        val shareText = """
            ${delivery.name}
            ${delivery.description}

            Время доставки: ${delivery.deliveryTime}
            ${if (delivery.deliveryPrice > 0) "Доставка: ${delivery.deliveryPrice.toInt()} ₽" else "Бесплатная доставка"}

            Тел: ${delivery.phone}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Поделиться"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
