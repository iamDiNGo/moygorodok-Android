package com.gorod.moygorodok.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Weather
import com.gorod.moygorodok.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather> = _weather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadWeather()
    }

    fun loadWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getCurrentWeather()
                _weather.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки погоды"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.refreshWeather()
                _weather.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка обновления погоды"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
