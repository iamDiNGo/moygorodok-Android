package com.gorod.moygorodok.ui.city

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.local.CityManager
import com.gorod.moygorodok.data.local.TokenManager
import com.gorod.moygorodok.data.remote.model.City
import com.gorod.moygorodok.data.repository.AuthRepository
import com.gorod.moygorodok.data.repository.CityRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class CitySelectionViewModel(application: Application) : AndroidViewModel(application) {

    private val cityRepository = CityRepository.getInstance(application)
    private val cityManager = CityManager.getInstance(application)
    private val tokenManager = TokenManager.getInstance(application)
    private val authRepository = AuthRepository.getInstance(application)

    private val _state = MutableStateFlow<CitySelectionState>(CitySelectionState.Loading)
    val state: StateFlow<CitySelectionState> = _state.asStateFlow()

    private val _selected = MutableSharedFlow<City>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val selected: SharedFlow<City> = _selected

    private val queryFlow = MutableStateFlow("")
    private var overviewCache: CitySelectionState.Overview? = null

    init {
        loadOverview()
        observeSearch()
    }

    private fun loadOverview() {
        viewModelScope.launch {
            _state.value = CitySelectionState.Loading
            val recent = cityManager.recentCities.first()
            cityRepository.getOverview()
                .onSuccess { data ->
                    val overview = CitySelectionState.Overview(
                        recent = recent,
                        popular = data.popularCities
                    )
                    overviewCache = overview
                    if (queryFlow.value.isBlank()) _state.value = overview
                }
                .onFailure { e ->
                    if (recent.isNotEmpty()) {
                        val overview = CitySelectionState.Overview(recent, emptyList())
                        overviewCache = overview
                        _state.value = overview
                    } else {
                        _state.value = CitySelectionState.Error(
                            e.message ?: "Не удалось загрузить города"
                        )
                    }
                }
        }
    }

    private fun observeSearch() {
        viewModelScope.launch {
            queryFlow
                .debounce(250)
                .collectLatest { q ->
                    val trimmed = q.trim()
                    if (trimmed.isEmpty()) {
                        overviewCache?.let { _state.value = it }
                        return@collectLatest
                    }
                    if (trimmed.length < 2) return@collectLatest
                    runSearch(trimmed)
                }
        }
    }

    fun onQueryChanged(query: String) {
        queryFlow.value = query
    }

    private suspend fun runSearch(query: String) {
        _state.value = CitySelectionState.Loading
        cityRepository.search(query)
            .onSuccess { cities ->
                _state.value = if (cities.isEmpty()) CitySelectionState.Empty
                else CitySelectionState.Search(query, cities)
            }
            .onFailure { e ->
                _state.value = CitySelectionState.Error(e.message ?: "Ошибка поиска")
            }
    }

    fun loadNearby(lat: Double, lng: Double) {
        viewModelScope.launch {
            _state.value = CitySelectionState.Loading
            cityRepository.nearby(lat, lng)
                .onSuccess { cities ->
                    _state.value = if (cities.isEmpty()) CitySelectionState.Empty
                    else CitySelectionState.Nearby(cities)
                }
                .onFailure { e ->
                    _state.value = CitySelectionState.Error(e.message ?: "Не удалось определить город")
                }
        }
    }

    fun selectCity(city: City) {
        viewModelScope.launch {
            cityManager.setSelectedCity(city)
            _selected.tryEmit(city)

            if (tokenManager.isLoggedIn()) {
                authRepository.updateProfile(cityId = city.id)
                    .onFailure {
                        Toast.makeText(
                            getApplication(),
                            "Не удалось сохранить город в профиле",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }
}
