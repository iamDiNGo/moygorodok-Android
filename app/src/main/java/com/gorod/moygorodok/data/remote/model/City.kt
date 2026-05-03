package com.gorod.moygorodok.data.remote.model

import com.google.gson.annotations.SerializedName

data class City(
    val id: Int,
    val name: String,
    val region: String? = null,
    val country: String? = null,
    @SerializedName("parent_id")
    val parentId: Int? = null,
    val type: String? = null,
    val level: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val population: Long? = null,
    @SerializedName("distance_km")
    val distanceKm: Double? = null,
    val children: List<City>? = null
)

data class CitiesResponse(
    val success: Boolean,
    val data: List<City>,
    val meta: CitiesMeta? = null
)

data class CitiesMeta(
    @SerializedName("current_page")
    val currentPage: Int? = null,
    @SerializedName("last_page")
    val lastPage: Int? = null,
    @SerializedName("per_page")
    val perPage: Int? = null,
    val total: Int? = null,
    @SerializedName("radius_km")
    val radiusKm: Double? = null
)

data class RegionsResponse(
    val success: Boolean,
    val data: List<City>
)

data class CityOverviewResponse(
    val success: Boolean,
    val data: CityOverviewData,
    val meta: CityOverviewMeta? = null
)

data class CityOverviewData(
    val regions: List<City>,
    @SerializedName("popular_cities")
    val popularCities: List<City>,
    @SerializedName("recent_cities")
    val recentCities: List<City>
)

data class CityOverviewMeta(
    @SerializedName("total_regions")
    val totalRegions: Int? = null,
    @SerializedName("total_cities")
    val totalCities: Int? = null,
    @SerializedName("total_districts")
    val totalDistricts: Int? = null
)
