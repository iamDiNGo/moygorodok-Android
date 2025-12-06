package com.gorod.moygorodok.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.NewsCategory
import com.gorod.moygorodok.databinding.FragmentNewsListBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class NewsListFragment : Fragment() {

    private var _binding: FragmentNewsListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsListViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChipGroup()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter { news ->
            val bundle = Bundle().apply {
                putString("newsId", news.id)
            }
            findNavController().navigate(R.id.action_newsList_to_newsDetail, bundle)
        }

        binding.recyclerNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }
    }

    private fun setupChipGroup() {
        // Добавляем чип "Все"
        binding.chipGroupCategories.check(R.id.chip_all)

        binding.chipGroupCategories.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedChipId = checkedIds.firstOrNull()

            val category = when (selectedChipId) {
                R.id.chip_city -> NewsCategory.CITY
                R.id.chip_events -> NewsCategory.EVENTS
                R.id.chip_transport -> NewsCategory.TRANSPORT
                R.id.chip_culture -> NewsCategory.CULTURE
                R.id.chip_sport -> NewsCategory.SPORT
                R.id.chip_society -> NewsCategory.SOCIETY
                else -> null
            }

            viewModel.setCategory(category)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshNews()
        }
    }

    private fun observeViewModel() {
        viewModel.newsList.observe(viewLifecycleOwner) { news ->
            newsAdapter.submitList(news)
            binding.textEmpty.visibility = if (news.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Повторить") {
                        viewModel.loadNews()
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
