package com.gorod.moygorodok.ui.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.databinding.FragmentCompanyListBinding
import com.google.android.material.chip.Chip

class CompanyListFragment : Fragment() {

    private var _binding: FragmentCompanyListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CompanyViewModel by viewModels()
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

        setupToolbar()
        setupAdapter()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAdapter() {
        adapter = CompanyAdapter { company ->
            val action = CompanyListFragmentDirections.actionCompanyListToCompanyDetail(company.id)
            findNavController().navigate(action)
        }

        binding.recyclerCompanies.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CompanyListFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        viewModel.companies.observe(viewLifecycleOwner) { companies ->
            adapter.submitList(companies)
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            setupCategoryChips(categories)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun setupCategoryChips(categories: List<com.gorod.moygorodok.data.model.CompanyCategory>) {
        binding.chipGroupCategories.removeAllViews()

        // Add "All" chip
        val allChip = Chip(requireContext()).apply {
            text = "Все"
            isCheckable = true
            isChecked = true
            setOnClickListener {
                viewModel.filterByCategory(null)
                updateChipSelection(null)
            }
        }
        binding.chipGroupCategories.addView(allChip)

        // Add category chips
        categories.forEach { category ->
            val chip = Chip(requireContext()).apply {
                text = "${category.emoji} ${category.displayName}"
                isCheckable = true
                tag = category
                setOnClickListener {
                    viewModel.filterByCategory(category)
                    updateChipSelection(category)
                }
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun updateChipSelection(selectedCategory: com.gorod.moygorodok.data.model.CompanyCategory?) {
        for (i in 0 until binding.chipGroupCategories.childCount) {
            val chip = binding.chipGroupCategories.getChildAt(i) as? Chip
            chip?.isChecked = if (selectedCategory == null) {
                i == 0
            } else {
                chip?.tag == selectedCategory
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
