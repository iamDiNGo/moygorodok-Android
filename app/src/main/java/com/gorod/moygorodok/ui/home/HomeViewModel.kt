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
import com.gorod.moygorodok.data.repository.HomeRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val homeRepository = HomeRepository.getInstance()
    private val cityManager = CityManager.getInstance(application)

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
        loadWidgets()
        checkFirstLaunch()
    }

    fun loadWidgets() {
        viewModelScope.launch {
            _isLoading.value = true

            val allWidgets = MockHomeWidgets.getWidgets()
            val widgetsByTarget = mapWidgetsByTarget(allWidgets)

            homeRepository.getHomeCells()
                .onSuccess { cells ->
                    val sorted = cells.mapNotNull { cell ->
                        widgetsByTarget[cell.actionTarget]
                    }
                    _widgets.value = sorted
                }
                .onFailure {
                    _widgets.value = allWidgets
                }

            _isLoading.value = false
        }
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            val current = cityManager.selectedCityId.first()
            if (current == null) _showCitySelector.tryEmit(Unit)
        }
    }

    private fun mapWidgetsByTarget(widgets: List<HomeWidget>): Map<String, HomeWidget> {
        val map = mutableMapOf<String, HomeWidget>()
        for (widget in widgets) {
            val target = when (widget) {
                is HomeWidget.WeatherWidget -> "weather"
                is HomeWidget.NewsWidget -> "news"
                is HomeWidget.AdsWidget -> "announcements"
                is HomeWidget.DeliveryWidget -> "delivery"
                is HomeWidget.TasksWidget -> "tasks"
                is HomeWidget.AdminWidget -> "admin"
                is HomeWidget.EmergencyWidget -> "emergency"
                is HomeWidget.ComplaintWidget -> "complaint"
                is HomeWidget.NotificationsWidget -> "notifications"
                is HomeWidget.ChatWidget -> "chat"
                is HomeWidget.CinemaWidget -> "cinema"
                is HomeWidget.CurrencyWidget -> "currency"
                is HomeWidget.CompanyWidget -> "company"
                is HomeWidget.ProfileWidget -> "profile"
                is HomeWidget.QuickActionsWidget -> "quick_actions"
            }
            map[target] = widget
        }
        return map
    }

    fun refresh() {
        loadWidgets()
    }
}
