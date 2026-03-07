package com.gorod.moygorodok.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.HomeWidget
import com.gorod.moygorodok.data.model.MockHomeWidgets
import com.gorod.moygorodok.data.remote.model.HomeCellDto
import com.gorod.moygorodok.data.repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val homeRepository = HomeRepository.getInstance()

    private val _widgets = MutableLiveData<List<HomeWidget>>()
    val widgets: LiveData<List<HomeWidget>> = _widgets

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadWidgets()
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
