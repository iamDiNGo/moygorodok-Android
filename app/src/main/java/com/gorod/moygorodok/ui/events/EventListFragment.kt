package com.gorod.moygorodok.ui.events

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
import com.gorod.moygorodok.data.model.EventCategory
import com.gorod.moygorodok.databinding.FragmentEventListBinding
import com.google.android.material.chip.Chip

class EventListFragment : Fragment() {

    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventListViewModel by viewModels()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
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
        eventAdapter = EventAdapter { event ->
            val bundle = bundleOf("eventId" to event.id)
            findNavController().navigate(R.id.action_eventList_to_eventDetail, bundle)
        }

        binding.recyclerEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchEvents(newText ?: "")
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
        EventCategory.values().forEach { category ->
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

    private fun updateChipSelection(selectedCategory: EventCategory?) {
        for (i in 0 until binding.chipGroupCategories.childCount) {
            val chip = binding.chipGroupCategories.getChildAt(i) as? Chip
            chip?.isChecked = if (selectedCategory == null) {
                i == 0 // "All" chip
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
        viewModel.events.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
            binding.textEmpty.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
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
