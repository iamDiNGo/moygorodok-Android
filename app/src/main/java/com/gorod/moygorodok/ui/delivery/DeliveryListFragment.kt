package com.gorod.moygorodok.ui.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.DeliveryCategory
import com.gorod.moygorodok.databinding.FragmentDeliveryListBinding
import com.google.android.material.chip.Chip

class DeliveryListFragment : Fragment() {

    private var _binding: FragmentDeliveryListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeliveryListViewModel by viewModels()
    private lateinit var deliveryAdapter: DeliveryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupSearch()
        setupCategoryChips()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupAdapter() {
        deliveryAdapter = DeliveryAdapter { delivery ->
            val bundle = bundleOf("deliveryId" to delivery.id)
            findNavController().navigate(R.id.action_deliveryList_to_deliveryDetail, bundle)
        }

        binding.recyclerDeliveries.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deliveryAdapter
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchDeliveries(newText ?: "")
                return true
            }
        })
    }

    private fun setupCategoryChips() {
        // Add "All" chip
        val allChip = Chip(requireContext()).apply {
            text = "Все"
            isCheckable = true
            isChecked = true
            setOnClickListener {
                viewModel.setCategory(null)
                updateChipSelection(null)
            }
        }
        binding.chipGroupCategories.addView(allChip)

        // Add category chips
        DeliveryCategory.values().forEach { category ->
            val chip = Chip(requireContext()).apply {
                text = "${category.emoji} ${category.displayName}"
                isCheckable = true
                tag = category
                setOnClickListener {
                    viewModel.setCategory(category)
                    updateChipSelection(category)
                }
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun updateChipSelection(selectedCategory: DeliveryCategory?) {
        for (i in 0 until binding.chipGroupCategories.childCount) {
            val chip = binding.chipGroupCategories.getChildAt(i) as? Chip
            chip?.isChecked = if (selectedCategory == null) {
                i == 0
            } else {
                chip?.tag == selectedCategory
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        viewModel.deliveries.observe(viewLifecycleOwner) { deliveries ->
            deliveryAdapter.submitList(deliveries)
            binding.textEmpty.visibility = if (deliveries.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            updateChipSelection(category)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
