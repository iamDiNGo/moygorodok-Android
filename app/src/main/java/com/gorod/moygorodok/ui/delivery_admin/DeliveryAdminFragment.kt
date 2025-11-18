package com.gorod.moygorodok.ui.delivery_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.databinding.FragmentDeliveryAdminBinding

class DeliveryAdminFragment : Fragment() {

    private var _binding: FragmentDeliveryAdminBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeliveryAdminViewModel by viewModels()
    private lateinit var categoryAdapter: AdminCategoryAdapter
    private lateinit var promotionAdapter: AdminPromotionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAdapters()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAdapters() {
        categoryAdapter = AdminCategoryAdapter(
            onCategoryClick = { category ->
                // Navigate to category products
                Toast.makeText(context, "Редактирование категории: ${category.name}", Toast.LENGTH_SHORT).show()
            },
            onVisibilityToggle = { category ->
                viewModel.toggleCategoryVisibility(category.id)
            }
        )

        promotionAdapter = AdminPromotionAdapter(
            onPromotionClick = { promotion ->
                Toast.makeText(context, "Редактирование акции: ${promotion.title}", Toast.LENGTH_SHORT).show()
            },
            onActiveToggle = { promotion ->
                viewModel.togglePromotionActive(promotion.id)
            }
        )

        binding.recyclerCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }

        binding.recyclerPromotions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = promotionAdapter
        }
    }

    private fun setupClickListeners() {
        // Toggle delivery status
        binding.switchDeliveryOpen.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleDeliveryOpen()
        }

        // Add category button
        binding.buttonAddCategory.setOnClickListener {
            Toast.makeText(context, "Добавить категорию", Toast.LENGTH_SHORT).show()
        }

        // Add promotion button
        binding.buttonAddPromotion.setOnClickListener {
            Toast.makeText(context, "Создать акцию", Toast.LENGTH_SHORT).show()
        }

        // Delivery zone settings
        binding.cardDeliveryZone.setOnClickListener {
            Toast.makeText(context, "Настройка зоны доставки на карте", Toast.LENGTH_SHORT).show()
        }

        // Save settings button
        binding.buttonSaveSettings.setOnClickListener {
            val discount = binding.editDeliveryDiscount.text.toString().toIntOrNull() ?: 0
            val freeFrom = binding.editFreeDeliveryFrom.text.toString().toDoubleOrNull()

            viewModel.setDeliveryDiscount(discount)
            viewModel.setFreeDeliveryFrom(freeFrom)

            Toast.makeText(context, "Настройки сохранены", Toast.LENGTH_SHORT).show()
        }

        // Products management
        binding.cardManageProducts.setOnClickListener {
            Toast.makeText(context, "Управление товарами", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.deliveryAdmin.observe(viewLifecycleOwner) { admin ->
            binding.apply {
                textDeliveryName.text = admin.delivery.name
                textDeliveryCategory.text = admin.delivery.category.displayName

                // Stats
                textTodayOrders.text = "12"
                textTodayRevenue.text = "15 600 ₽"
                textRating.text = admin.delivery.rating.toString()
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
            binding.textCategoriesCount.text = "${categories.size} категорий"
        }

        viewModel.promotions.observe(viewLifecycleOwner) { promotions ->
            promotionAdapter.submitList(promotions)
            val activeCount = promotions.count { it.isActive }
            binding.textPromotionsCount.text = "$activeCount активных"
        }

        viewModel.isOpen.observe(viewLifecycleOwner) { isOpen ->
            binding.switchDeliveryOpen.isChecked = isOpen
            binding.textDeliveryStatus.text = if (isOpen) "Открыто" else "Закрыто"
            binding.textDeliveryStatus.setTextColor(
                resources.getColor(
                    if (isOpen) android.R.color.holo_green_dark else android.R.color.holo_red_dark,
                    null
                )
            )
        }

        viewModel.deliveryDiscount.observe(viewLifecycleOwner) { discount ->
            if (binding.editDeliveryDiscount.text.toString() != discount.toString()) {
                binding.editDeliveryDiscount.setText(discount.toString())
            }
        }

        viewModel.freeDeliveryFrom.observe(viewLifecycleOwner) { amount ->
            val text = amount?.toInt()?.toString() ?: ""
            if (binding.editFreeDeliveryFrom.text.toString() != text) {
                binding.editFreeDeliveryFrom.setText(text)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
