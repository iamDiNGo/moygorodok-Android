package com.gorod.moygorodok.ui.tasks

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
import com.gorod.moygorodok.data.model.TaskCategory
import com.gorod.moygorodok.databinding.FragmentTaskListBinding
import com.google.android.material.chip.Chip

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupCategoryChips()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter { task ->
            val action = TaskListFragmentDirections.actionTaskListToTaskDetail(task.id)
            findNavController().navigate(action)
        }

        binding.recyclerTasks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText ?: "")
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
        TaskCategory.values().forEach { category ->
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

    private fun updateChipSelection(selectedCategory: TaskCategory?) {
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
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            binding.emptyView.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerTasks.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
