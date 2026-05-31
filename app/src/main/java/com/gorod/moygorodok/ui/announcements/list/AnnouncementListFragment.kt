package com.gorod.moygorodok.ui.announcements.list

import com.gorod.moygorodok.ui.announcements.common.AnnouncementAdapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.AnnouncementCategory
import com.gorod.moygorodok.data.model.AnnouncementSortOption
import com.gorod.moygorodok.data.repository.AuthRepository
import com.gorod.moygorodok.databinding.DialogAnnouncementPriceFilterBinding
import com.gorod.moygorodok.databinding.FragmentAnnouncementListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnnouncementListFragment : Fragment() {

    private var _binding: FragmentAnnouncementListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnnouncementListViewModel by viewModels()
    private lateinit var adapter: AnnouncementAdapter

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnouncementListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupSearch()
        setupChips()
        setupButtons()
        setupSwipeRefresh()
        setupFab()
        observeViewModel()
    }

    private fun setupRecycler() {
        adapter = AnnouncementAdapter { announcement ->
            val bundle = Bundle().apply { putInt("announcementId", announcement.id) }
            findNavController().navigate(R.id.action_announcementList_to_announcementDetail, bundle)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAnnouncements.layoutManager = layoutManager
        binding.recyclerAnnouncements.adapter = adapter

        binding.recyclerAnnouncements.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                viewModel.loadMoreIfNeeded(lastVisible)
            }
        })
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearch(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    viewModel.setSearch(newText.orEmpty())
                }
                return true
            }
        })
    }

    private fun setupChips() {
        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedId = checkedIds.firstOrNull()
            val category = when (selectedId) {
                R.id.chip_real_estate -> AnnouncementCategory.REAL_ESTATE
                R.id.chip_transport -> AnnouncementCategory.TRANSPORT
                R.id.chip_electronics -> AnnouncementCategory.ELECTRONICS
                R.id.chip_clothes -> AnnouncementCategory.CLOTHES
                R.id.chip_furniture -> AnnouncementCategory.FURNITURE
                R.id.chip_services -> AnnouncementCategory.SERVICES
                R.id.chip_pets -> AnnouncementCategory.PETS
                R.id.chip_other -> AnnouncementCategory.OTHER
                else -> null
            }
            viewModel.setCategory(category)
        }
    }

    private fun setupButtons() {
        binding.buttonSort.setOnClickListener { showSortDialog() }
        binding.buttonFilter.setOnClickListener { showPriceFilterDialog() }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
    }

    private fun setupFab() {
        binding.fabCreate.setOnClickListener {
            val isLoggedIn = AuthRepository.getInstance(requireContext()).isLoggedIn()
            if (!isLoggedIn) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.announcement_login_required_title)
                    .setMessage(R.string.announcement_login_required_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
                return@setOnClickListener
            }
            val bundle = Bundle().apply { putInt("editId", -1) }
            findNavController().navigate(R.id.navigation_create_announcement, bundle)
        }
    }

    private fun showSortDialog() {
        val options = AnnouncementSortOption.entries.toTypedArray()
        val current = viewModel.filter.value?.sort ?: AnnouncementSortOption.DATE_DESC
        val selectedIndex = options.indexOf(current)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.button_sort)
            .setSingleChoiceItems(
                options.map { it.label }.toTypedArray(),
                selectedIndex
            ) { dialog, which ->
                viewModel.setSort(options[which])
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showPriceFilterDialog() {
        val dialogBinding = DialogAnnouncementPriceFilterBinding.inflate(layoutInflater)
        val currentFilter = viewModel.filter.value
        currentFilter?.minPrice?.let { dialogBinding.editMinPrice.setText(it.toLong().toString()) }
        currentFilter?.maxPrice?.let { dialogBinding.editMaxPrice.setText(it.toLong().toString()) }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.announcement_price_filter_title)
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val min = dialogBinding.editMinPrice.text?.toString()?.toDoubleOrNull()
                val max = dialogBinding.editMaxPrice.text?.toString()?.toDoubleOrNull()
                viewModel.setPriceRange(min, max)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .setNeutralButton(R.string.button_filter) { _, _ ->
                viewModel.setPriceRange(null, null)
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.textEmpty.visibility =
                if (items.isEmpty() && viewModel.isLoading.value != true) View.VISIBLE else View.GONE
        }
        viewModel.total.observe(viewLifecycleOwner) { count ->
            binding.textResultsCount.text = getString(R.string.announcement_results_count, count)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.isRefreshing.observe(viewLifecycleOwner) { refreshing ->
            binding.swipeRefresh.isRefreshing = refreshing
        }
        viewModel.filter.observe(viewLifecycleOwner) { filter ->
            binding.buttonFilter.isSelected = filter.hasActiveFilters
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        // refresh при возврате с CreateFragment/Detail
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                // ничего не делаем, держим scope живым
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // тихий refresh — могли вернуться с Create/Detail
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        _binding = null
    }
}
