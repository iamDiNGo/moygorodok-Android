package com.gorod.moygorodok.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var widgetAdapter: HomeWidgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupAdapter() {
        widgetAdapter = HomeWidgetAdapter(
            onWeatherClick = {
                findNavController().navigate(R.id.navigation_weather)
            },
            onNewsClick = {
                findNavController().navigate(R.id.navigation_news_list)
            },
            onAdsClick = {
                findNavController().navigate(R.id.navigation_ad_list)
            },
            onDeliveryClick = {
                findNavController().navigate(R.id.navigation_delivery_list)
            },
            onTasksClick = {
                findNavController().navigate(R.id.navigation_task_list)
            }
        )

        binding.recyclerWidgets.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = widgetAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        viewModel.widgets.observe(viewLifecycleOwner) { widgets ->
            widgetAdapter.submitList(widgets)
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