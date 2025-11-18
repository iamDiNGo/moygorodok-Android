package com.gorod.moygorodok.ui.ads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.AdCategory
import com.gorod.moygorodok.data.model.SortOption
import com.gorod.moygorodok.databinding.FragmentAdListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class AdListFragment : Fragment() {

    private var _binding: FragmentAdListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdListViewModel by viewModels()
    private lateinit var adAdapter: AdAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupChipGroup()
        setupButtons()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adAdapter = AdAdapter(
            onAdClick = { ad ->
                val bundle = Bundle().apply {
                    putString("adId", ad.id)
                }
                findNavController().navigate(R.id.action_adList_to_adDetail, bundle)
            },
            onFavoriteClick = { ad ->
                // Обработка добавления в избранное
                Snackbar.make(
                    binding.root,
                    if (ad.isFavorite) "Удалено из избранного" else "Добавлено в избранное",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        )

        binding.recyclerAds.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adAdapter
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun setupChipGroup() {
        binding.chipGroupCategories.check(R.id.chip_all_ads)

        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedChipId = checkedIds.firstOrNull()

            val category = when (selectedChipId) {
                R.id.chip_transport -> AdCategory.TRANSPORT
                R.id.chip_realty -> AdCategory.REALTY
                R.id.chip_electronics -> AdCategory.ELECTRONICS
                R.id.chip_clothing -> AdCategory.CLOTHING
                R.id.chip_home -> AdCategory.HOME
                R.id.chip_services -> AdCategory.SERVICES
                R.id.chip_animals -> AdCategory.ANIMALS
                R.id.chip_hobbies -> AdCategory.HOBBIES
                R.id.chip_children -> AdCategory.CHILDREN
                R.id.chip_other -> AdCategory.OTHER
                else -> null
            }

            viewModel.setCategory(category)
        }
    }

    private fun setupButtons() {
        binding.buttonSort.setOnClickListener {
            showSortDialog()
        }

        binding.buttonFilter.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showSortDialog() {
        val sortOptions = SortOption.values()
        val currentSort = viewModel.currentFilter.value?.sortOption ?: SortOption.DATE_DESC
        val selectedIndex = sortOptions.indexOf(currentSort)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Сортировка")
            .setSingleChoiceItems(
                sortOptions.map { it.displayName }.toTypedArray(),
                selectedIndex
            ) { dialog, which ->
                viewModel.setSortOption(sortOptions[which])
                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showFilterDialog() {
        val currentFilter = viewModel.currentFilter.value

        val dialogView = layoutInflater.inflate(R.layout.dialog_ad_filter, null)
        val minPriceEdit = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edit_min_price)
        val maxPriceEdit = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edit_max_price)

        currentFilter?.minPrice?.let { minPriceEdit.setText(it.toInt().toString()) }
        currentFilter?.maxPrice?.let { maxPriceEdit.setText(it.toInt().toString()) }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Фильтр по цене")
            .setView(dialogView)
            .setPositiveButton("Применить") { _, _ ->
                val minPrice = minPriceEdit.text.toString().toDoubleOrNull()
                val maxPrice = maxPriceEdit.text.toString().toDoubleOrNull()
                viewModel.setPriceRange(minPrice, maxPrice)
            }
            .setNegativeButton("Отмена", null)
            .setNeutralButton("Сбросить") { _, _ ->
                viewModel.setPriceRange(null, null)
            }
            .show()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshAds()
        }
    }

    private fun observeViewModel() {
        viewModel.adsList.observe(viewLifecycleOwner) { ads ->
            adAdapter.submitList(ads)
            binding.textEmpty.visibility = if (ads.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.resultsCount.observe(viewLifecycleOwner) { count ->
            binding.textResultsCount.text = "Найдено: $count"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }

        viewModel.currentFilter.observe(viewLifecycleOwner) { filter ->
            // Обновляем индикатор активных фильтров
            binding.buttonFilter.isSelected = viewModel.hasActiveFilters()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Повторить") {
                        viewModel.loadAds()
                    }
                    .show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
