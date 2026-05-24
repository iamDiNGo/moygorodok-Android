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
            weather?.let { updateUI(it) }
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
            textLocation.text = weather.location
            textCurrentTemp.text = "${weather.currentTemp}°"
            textCondition.text = weather.description?.takeIf { it.isNotBlank() }
                ?: weather.condition.displayName
            textHighLow.text = formatHighLow(weather.highTemp, weather.lowTemp)

            hourlyAdapter.submitList(weather.hourlyForecast)
            dailyAdapter.submitList(weather.dailyForecast)

            textFeelsLike.text = weather.feelsLike?.let { "$it°" } ?: "—"
            textHumidity.text = weather.humidity?.let { "$it%" } ?: "—"
            textWind.text = listOfNotNull(
                weather.windSpeed?.let { "%.1f м/с".format(it) },
                weather.windDirection
            ).joinToString(" ").ifBlank { "—" }
            textPressure.text = weather.pressure?.let { "$it гПа" } ?: "—"
            textSunrise.text = weather.sunrise ?: "—"
            textSunset.text = weather.sunset ?: "—"
        }
    }

    private fun formatHighLow(high: Int?, low: Int?): String {
        if (high == null && low == null) return ""
        val hi = high?.let { "Макс: $it°" } ?: ""
        val lo = low?.let { "Мин: $it°" } ?: ""
        return listOf(hi, lo).filter { it.isNotEmpty() }.joinToString("  ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
