package com.gorod.moygorodok.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.local.CityManager
import com.gorod.moygorodok.data.model.HomeWidget
import com.gorod.moygorodok.data.model.MockHomeWidgets
import com.gorod.moygorodok.data.repository.AnnouncementRepository
import com.gorod.moygorodok.data.repository.AuthRepository
import com.gorod.moygorodok.data.repository.HomeMapperContext
import com.gorod.moygorodok.data.repository.HomeRepository
import com.gorod.moygorodok.data.repository.HomeServerWidgetMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val homeRepository = HomeRepository.getInstance()
    private val announcementRepository = AnnouncementRepository.getInstance()
    private val cityManager = CityManager.getInstance(application)
    private val authRepository = AuthRepository.getInstance(application)

    private val _widgets = MutableLiveData<List<HomeWidget>>()
    val widgets: LiveData<List<HomeWidget>> = _widgets

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val cityName: LiveData<String?> = cityManager.selectedCityName.asLiveData()

    private val _showCitySelector = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val showCitySelector: SharedFlow<Unit> = _showCitySelector

    init {
        observeCitySelection()
        checkFirstLaunch()
    }

    private fun observeCitySelection() {
        viewModelScope.launch {
            cityManager.selectedCityId
                .combine(cityManager.selectedCityName) { id, name -> id to name }
                .collectLatest { (cityId, cityName) ->
                    loadWidgets(cityId, cityName)
                }
        }
    }

    private suspend fun loadWidgets(cityId: Int?, cityName: String?) {
        _isLoading.value = true

        val localWidgets = MockHomeWidgets.getLocalWidgets()
        val context = currentMapperContext(cityName)

        coroutineScope {
            val cellsDeferred = async { homeRepository.getHomeCells(cityId) }
            val announcementsDeferred = async {
                announcementRepository.fetchList(cityId = cityId, perPage = 4).getOrNull()
            }

            val cellsResult = cellsDeferred.await()
            val announcementsPage = announcementsDeferred.await()

            val announcementsWidget = announcementsPage?.takeIf { it.items.isNotEmpty() }?.let { page ->
                HomeWidget.AnnouncementsWidget(
                    title = "Новые объявления",
                    totalCount = page.total,
                    items = page.items.take(3)
                )
            }

            cellsResult
                .onSuccess { cells ->
                    val widgets = cells.mapNotNull { cell ->
                        when (cell.actionTarget) {
                            "announcements" -> announcementsWidget
                            else -> HomeServerWidgetMapper.map(cell, context)
                                ?: localWidgets[cell.actionTarget]
                        }
                    }
                    _widgets.value = widgets
                }
                .onFailure {
                    val fallback = mutableListOf<HomeWidget>().apply {
                        addAll(localWidgets.values)
                        announcementsWidget?.let { add(it) }
                    }
                    _widgets.value = fallback
                }
        }

        _isLoading.value = false
    }

    private fun currentMapperContext(cityName: String?): HomeMapperContext {
        val user = authRepository.getCurrentUser()
        return HomeMapperContext(
            cityName = cityName,
            isAuthenticated = authRepository.isLoggedIn(),
            hasBirthday = !user?.birthday.isNullOrBlank()
        )
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            val current = cityManager.selectedCityId.first()
            if (current == null) _showCitySelector.tryEmit(Unit)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val cityId = cityManager.selectedCityId.first()
            val name = cityManager.selectedCityName.first()
            loadWidgets(cityId, name)
        }
    }
}
