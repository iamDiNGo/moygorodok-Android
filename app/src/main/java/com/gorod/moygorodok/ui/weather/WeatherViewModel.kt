package com.gorod.moygorodok.ui.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.local.CityManager
import com.gorod.moygorodok.data.model.Weather
import com.gorod.moygorodok.data.repository.WeatherRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository.getInstance()
    private val cityManager = CityManager.getInstance(application)

    private val _weather = MutableLiveData<Weather?>()
    val weather: LiveData<Weather?> = _weather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        observeCity()
    }

    private fun observeCity() {
        viewModelScope.launch {
            cityManager.selectedCityId
                .combine(cityManager.selectedCityName) { id, name -> id to name }
                .collectLatest { (cityId, cityName) ->
                    loadWeather(cityId, cityName)
                }
        }
    }

    private suspend fun loadWeather(cityId: Int?, cityName: String?) {
        if (cityId == null) {
            _weather.value = null
            _error.value = "Выберите город"
            return
        }
        _isLoading.value = true
        _error.value = null
        repository.getWeatherForCity(cityId, cityName)
            .onSuccess { _weather.value = it }
            .onFailure {
                _error.value = it.message ?: "Ошибка загрузки погоды"
            }
        _isLoading.value = false
    }

    fun refresh() {
        viewModelScope.launch {
            val id = cityManager.selectedCityId.first()
            val name = cityManager.selectedCityName.first()
            loadWeather(id, name)
        }
    }
}
