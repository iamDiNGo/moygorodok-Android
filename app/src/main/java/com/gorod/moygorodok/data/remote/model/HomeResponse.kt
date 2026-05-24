package com.gorod.moygorodok.data.remote.model

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    val success: Boolean,
    val data: List<HomeCellDto>,
    val meta: HomeMeta
)

data class HomeCellDto(
    val id: Int,
    val content: HomeCellContent,
    val style: HomeCellStyle,
    val width: Int,
    val height: Int,
    val image: String?,
    @SerializedName("action_type")
    val actionType: String,
    @SerializedName("action_target")
    val actionTarget: String,
    @SerializedName("action_params")
    val actionParams: Map<String, Any>?,
    @SerializedName("sort_order")
    val sortOrder: Int,
    @SerializedName("weather_data")
    val weatherData: WeatherCellDataDto? = null,
    @SerializedName("currency_data")
    val currencyData: CurrencyCellDataDto? = null,
    @SerializedName("news_data")
    val newsData: List<NewsListItemDto>? = null,
    @SerializedName("events_data")
    val eventsData: List<EventListItemDto>? = null,
    @SerializedName("companies_data")
    val companiesData: List<CompanyListItemDto>? = null,
    @SerializedName("horoscope_data")
    val horoscopeData: HoroscopeDataDto? = null
)

data class HomeCellContent(
    val title: String,
    val subtitle: String? = null
)

data class HomeCellStyle(
    val background: String,
    @SerializedName("text_color")
    val textColor: String
)

data class HomeMeta(
    val total: Int,
    @SerializedName("city_id")
    val cityId: Int?
)

// -- Weather ---------------------------------------------------------------

data class WeatherCellDataDto(
    val timezone: String? = null,
    val provider: String? = null,
    val current: WeatherCurrentDto? = null,
    val hourly: List<WeatherHourlyDto>? = null,
    val daily: List<WeatherDailyDto>? = null
)

data class WeatherHourlyDto(
    val time: String? = null,
    val temperature: Double? = null,
    val precipitation: Double? = null,
    @SerializedName("wind_speed")
    val windSpeed: Double? = null,
    @SerializedName("weather_code")
    val weatherCode: Int? = null,
    val icon: String? = null
)

data class WeatherCurrentDto(
    @SerializedName("observed_at")
    val observedAt: String? = null,
    val temperature: Double? = null,
    @SerializedName("feels_like")
    val feelsLike: Double? = null,
    val humidity: Double? = null,
    @SerializedName("wind_speed")
    val windSpeed: Double? = null,
    @SerializedName("wind_direction")
    val windDirection: Double? = null,
    val pressure: Double? = null,
    @SerializedName("weather_code")
    val weatherCode: Int? = null,
    val icon: String? = null,
    val description: String? = null
)

data class WeatherDailyDto(
    val date: String? = null,
    @SerializedName("temp_min")
    val tempMin: Double? = null,
    @SerializedName("temp_max")
    val tempMax: Double? = null,
    val precipitation: Double? = null,
    @SerializedName("wind_speed_max")
    val windSpeedMax: Double? = null,
    @SerializedName("weather_code")
    val weatherCode: Int? = null,
    val icon: String? = null,
    val description: String? = null,
    val sunrise: String? = null,
    val sunset: String? = null
)

// -- Currency --------------------------------------------------------------

data class CurrencyCellDataDto(
    @SerializedName("city_id")
    val cityId: Int? = null,
    @SerializedName("profile_scope")
    val profileScope: String? = null,
    @SerializedName("rate_date")
    val rateDate: String? = null,
    val sources: List<CurrencySourceDto>? = null
)

data class CurrencySourceDto(
    val code: String? = null,
    val name: String? = null,
    @SerializedName("rate_kind")
    val rateKind: String? = null,
    @SerializedName("base_currency")
    val baseCurrency: String? = null,
    val rates: List<CurrencyRateDto>? = null
)

data class CurrencyRateDto(
    val code: String? = null,
    val name: String? = null,
    val symbol: String? = null,
    val flag: String? = null,
    val nominal: Int? = null,
    @SerializedName("official_rate")
    val officialRate: Double? = null,
    @SerializedName("buy_rate")
    val buyRate: Double? = null,
    @SerializedName("sell_rate")
    val sellRate: Double? = null,
    @SerializedName("previous_official_rate")
    val previousOfficialRate: Double? = null,
    @SerializedName("previous_buy_rate")
    val previousBuyRate: Double? = null,
    @SerializedName("previous_sell_rate")
    val previousSellRate: Double? = null,
    @SerializedName("previous_rate_date")
    val previousRateDate: String? = null
)

// -- News ------------------------------------------------------------------

data class NewsListItemDto(
    val id: Int? = null,
    val slug: String? = null,
    val title: String? = null,
    val summary: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("source_type")
    val sourceType: String? = null,
    @SerializedName("source_url")
    val sourceUrl: String? = null,
    @SerializedName("city_id")
    val cityId: Int? = null,
    @SerializedName("published_at")
    val publishedAt: String? = null
)

data class NewsDetailDto(
    val id: Int? = null,
    val slug: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val content: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("source_type")
    val sourceType: String? = null,
    @SerializedName("source_url")
    val sourceUrl: String? = null,
    @SerializedName("city_id")
    val cityId: Int? = null,
    @SerializedName("published_at")
    val publishedAt: String? = null
)

data class NewsListResponse(
    val success: Boolean,
    val data: List<NewsListItemDto>,
    val meta: PaginationMeta? = null
)

data class PaginationMeta(
    @SerializedName("current_page")
    val currentPage: Int? = null,
    @SerializedName("last_page")
    val lastPage: Int? = null,
    @SerializedName("per_page")
    val perPage: Int? = null,
    val total: Int? = null
)

// -- Events ----------------------------------------------------------------

data class EventListItemDto(
    val id: Int? = null,
    val slug: String? = null,
    val title: String? = null,
    val summary: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val venue: String? = null,
    @SerializedName("ticket_url")
    val ticketUrl: String? = null,
    @SerializedName("starts_at")
    val startsAt: String? = null,
    @SerializedName("ends_at")
    val endsAt: String? = null,
    @SerializedName("city_id")
    val cityId: Int? = null
)

// -- Companies -------------------------------------------------------------

data class CompanyListItemDto(
    val id: Int? = null,
    val name: String? = null,
    val slug: String? = null,
    val kind: String? = null,
    @SerializedName("kind_label")
    val kindLabel: String? = null,
    val category: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val rating: Double? = null,
    @SerializedName("reviews_count")
    val reviewsCount: Int? = null,
    @SerializedName("cover_photo_url")
    val coverPhotoUrl: String? = null,
    @SerializedName("city_id")
    val cityId: Int? = null
)

// -- Horoscope -------------------------------------------------------------

data class HoroscopeDataDto(
    @SerializedName("zodiac_sign")
    val zodiacSign: String? = null,
    @SerializedName("zodiac_sign_label")
    val zodiacSignLabel: String? = null,
    val symbol: String? = null,
    val date: String? = null,
    val type: String? = null,
    val text: String? = null
)
