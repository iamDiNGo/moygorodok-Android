package com.gorod.moygorodok.ui.city

import com.gorod.moygorodok.data.remote.model.City

sealed class CitySelectionState {
    object Loading : CitySelectionState()

    data class Overview(
        val recent: List<City>,
        val popular: List<City>
    ) : CitySelectionState()

    data class Search(val query: String, val results: List<City>) : CitySelectionState()

    data class Nearby(val results: List<City>) : CitySelectionState()

    object Empty : CitySelectionState()

    data class Error(val message: String) : CitySelectionState()
}

sealed class CityListItem {
    data class Header(val title: String) : CityListItem()
    data class CityRow(val city: City, val showDistance: Boolean = false) : CityListItem()
}
