package com.gorod.moygorodok.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.data.model.Weather
import com.gorod.moygorodok.databinding.FragmentWeatherBinding

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var hourlyAdapter: HourlyWeatherAdapter
    private lateinit var dailyAdapter: DailyWeatherAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupAdapters() {
        hourlyAdapter = HourlyWeatherAdapter()
        binding.recyclerHourly.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyAdapter
        }

        dailyAdapter = DailyWeatherAdapter()
        binding.recyclerDaily.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dailyAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            updateUI(weather)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            binding.progressBar.visibility = if (isLoading && viewModel.weather.value == null) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.textError.text = error
                binding.textError.visibility = View.VISIBLE
            } else {
                binding.textError.visibility = View.GONE
            }
        }
    }

    private fun updateUI(weather: Weather) {
        binding.apply {
            // Header
            textLocation.text = weather.location
            textCurrentTemp.text = "${weather.currentTemp}°"
            textCondition.text = weather.condition.displayName
            textHighLow.text = "Макс: ${weather.highTemp}°  Мин: ${weather.lowTemp}°"

            // Hourly forecast
            hourlyAdapter.submitList(weather.hourlyForecast)

            // Daily forecast
            dailyAdapter.submitList(weather.dailyForecast)

            // Weather details
            textFeelsLike.text = "${weather.feelsLike}°"
            textHumidity.text = "${weather.humidity}%"
            textWind.text = "${weather.windSpeed} м/с ${weather.windDirection}"
            textPressure.text = "${weather.pressure} мм"
            textVisibility.text = "${weather.visibility} км"
            textUvIndex.text = weather.uvIndex.toString()
            textSunrise.text = weather.sunrise
            textSunset.text = weather.sunset
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
