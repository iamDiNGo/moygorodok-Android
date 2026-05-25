package com.gorod.moygorodok.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.databinding.FragmentHomeBinding
import com.gorod.moygorodok.ui.city.CitySelectionBottomSheet
import kotlinx.coroutines.launch

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
        setupCityHeader()
        observeViewModel()
        observeCityResult()
        observeCitySelectorTrigger()
    }

    private fun setupCityHeader() {
        binding.cityHeader.setOnClickListener { showCitySelector() }
    }

    private fun showCitySelector() {
        val existing = parentFragmentManager.findFragmentByTag(CitySelectionBottomSheet.TAG)
        if (existing != null) return
        CitySelectionBottomSheet().show(parentFragmentManager, CitySelectionBottomSheet.TAG)
    }

    private fun observeCityResult() {
        parentFragmentManager.setFragmentResultListener(
            CitySelectionBottomSheet.RESULT_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            // CityManager обновлён, шапка перерисуется через LiveData
        }
    }

    private fun observeCitySelectorTrigger() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showCitySelector.collect { showCitySelector() }
            }
        }
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
                findNavController().navigate(R.id.navigation_announcement_list)
            },
            onDeliveryClick = {
                findNavController().navigate(R.id.navigation_delivery_list)
            },
            onTasksClick = {
                findNavController().navigate(R.id.navigation_task_list)
            },
            onAdminClick = {
                findNavController().navigate(R.id.navigation_delivery_admin)
            },
            onEmergencyClick = {
                findNavController().navigate(R.id.navigation_emergency)
            },
            onNotificationsClick = {
                findNavController().navigate(R.id.navigation_notification_list)
            },
            onChatClick = {
                findNavController().navigate(R.id.navigation_chat)
            },
            onCinemaClick = {
                findNavController().navigate(R.id.navigation_cinema_list)
            },
            onCurrencyClick = {
                findNavController().navigate(R.id.navigation_currency_list)
            },
            onCompanyClick = {
                findNavController().navigate(R.id.navigation_company_list)
            },
            onHoroscopeClick = { widget ->
                val sign = (widget.state as? com.gorod.moygorodok.data.model.HoroscopeWidgetState.Ready)?.zodiacSign
                val bundle = sign?.let { Bundle().apply { putString("sign", it) } }
                findNavController().navigate(R.id.navigation_horoscope, bundle)
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

        viewModel.cityName.observe(viewLifecycleOwner) { name ->
            binding.textCity.text = name ?: "Выберите город"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
