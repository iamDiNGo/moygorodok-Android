package com.gorod.moygorodok.ui.company.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.R
import com.gorod.moygorodok.databinding.FragmentCompanyListBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class CompanyListFragment : Fragment() {

    private var _binding: FragmentCompanyListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CompanyListViewModel by viewModels()
    private lateinit var adapter: CompanyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        setupAdapter()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupAdapter() {
        adapter = CompanyAdapter { company ->
            val bundle = Bundle().apply { putInt("companyId", company.id) }
            findNavController().navigate(R.id.action_companyList_to_companyDetail, bundle)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCompanies.layoutManager = layoutManager
        binding.recyclerCompanies.adapter = adapter
        binding.recyclerCompanies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                viewModel.loadMoreIfNeeded(lastVisible)
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { adapter.submitList(it) }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            renderCategoryChips(categories)
        }

        viewModel.selectedCategoryId.observe(viewLifecycleOwner) { selectedId ->
            updateChipSelection(selectedId)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefresh.isRefreshing = loading
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { refreshing ->
            if (!refreshing) binding.swipeRefresh.isRefreshing = false
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun renderCategoryChips(categories: List<com.gorod.moygorodok.data.model.CompanyCategory>) {
        val group = binding.chipGroupCategories
        group.removeAllViews()

        val allChip = Chip(requireContext()).apply {
            text = "Все"
            isCheckable = true
            isChecked = viewModel.selectedCategoryId.value == null
            tag = null
            setOnClickListener { viewModel.setCategory(null) }
        }
        group.addView(allChip)

        val openNowChip = Chip(requireContext()).apply {
            text = "Открыто сейчас"
            isCheckable = true
            isChecked = viewModel.openNow.value == true
            setOnClickListener { viewModel.setOpenNow(isChecked) }
        }
        group.addView(openNowChip)

        categories.forEach { category ->
            val chip = Chip(requireContext()).apply {
                text = category.name
                isCheckable = true
                tag = category.id
                isChecked = viewModel.selectedCategoryId.value == category.id
                setOnClickListener { viewModel.setCategory(category.id) }
            }
            group.addView(chip)
        }
    }

    private fun updateChipSelection(selectedCategoryId: Int?) {
        val group = binding.chipGroupCategories
        for (i in 0 until group.childCount) {
            val chip = group.getChildAt(i) as? Chip ?: continue
            val tag = chip.tag
            // skip open-now chip (no tag, doesn't represent category)
            if (chip.text == "Открыто сейчас") continue
            chip.isChecked = when {
                selectedCategoryId == null && tag == null -> true
                tag is Int && tag == selectedCategoryId -> true
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
