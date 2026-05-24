package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.model.HomeWidget
import com.gorod.moygorodok.data.model.HoroscopeWidgetState
import com.gorod.moygorodok.data.model.NewsPreview
import com.gorod.moygorodok.data.model.WeatherCondition
import com.gorod.moygorodok.data.remote.model.CurrencyCellDataDto
import com.gorod.moygorodok.data.remote.model.CurrencyRateDto
import com.gorod.moygorodok.data.remote.model.HomeCellDto
import com.gorod.moygorodok.data.remote.model.HoroscopeDataDto
import com.gorod.moygorodok.data.remote.model.NewsListItemDto
import com.gorod.moygorodok.data.remote.model.WeatherCellDataDto
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

data class HomeMapperContext(
    val cityName: String?,
    val isAuthenticated: Boolean,
    val hasBirthday: Boolean
)

/**
 * Превращает серверные блоки `*_data` из /api/home в готовые виджеты главной.
 * Возвращает null, если данных для соответствующего таргета нет.
 */
object HomeServerWidgetMapper {

    fun map(cell: HomeCellDto, context: HomeMapperContext): HomeWidget? {
        return when (cell.actionTarget) {
            "weather" -> cell.weatherData?.let { mapWeather(it, context.cityName) }
            "currency" -> cell.currencyData?.let(::mapCurrency)
            "news" -> cell.newsData?.let { mapNews(it) }
            "company", "companies" -> cell.companiesData?.let { items ->
                HomeWidget.CompanyWidget(
                    totalCount = items.size,
                    verifiedCount = items.size,
                    categoriesCount = items.mapNotNull { it.category }.distinct().size
                )
            }
            "horoscope" -> HomeWidget.HoroscopeWidget(
                state = mapHoroscope(cell.horoscopeData, context)
            )
            else -> null
        }
    }

    private fun mapWeather(data: WeatherCellDataDto, fallbackLocation: String?): HomeWidget.WeatherWidget? {
        val current = data.current ?: return null
        val today = data.daily?.firstOrNull()
        val location = fallbackLocation ?: "—"
        val temp = current.temperature?.roundToInt() ?: return null
        val high = today?.tempMax?.roundToInt() ?: temp
        val low = today?.tempMin?.roundToInt() ?: temp
        return HomeWidget.WeatherWidget(
            location = location,
            currentTemp = temp,
            condition = mapWeatherIcon(current.icon),
            highTemp = high,
            lowTemp = low
        )
    }

    private fun mapWeatherIcon(icon: String?): WeatherCondition = when (icon) {
        "sunny" -> WeatherCondition.SUNNY
        "partly_cloudy" -> WeatherCondition.PARTLY_CLOUDY
        "cloudy" -> WeatherCondition.CLOUDY
        "fog" -> WeatherCondition.FOG
        "drizzle" -> WeatherCondition.LIGHT_RAIN
        "rain" -> WeatherCondition.RAIN
        "freezing_rain" -> WeatherCondition.RAIN
        "snow" -> WeatherCondition.SNOW
        "thunderstorm" -> WeatherCondition.THUNDERSTORM
        else -> WeatherCondition.CLOUDY
    }

    private fun mapCurrency(data: CurrencyCellDataDto): HomeWidget.CurrencyWidget? {
        val source = data.sources?.firstOrNull() ?: return null
        val rates = source.rates.orEmpty().associateBy { it.code }
        return HomeWidget.CurrencyWidget(
            usdRate = rates["USD"]?.toClientRate(),
            eurRate = rates["EUR"]?.toClientRate(),
            cnyRate = rates["CNY"]?.toClientRate(),
            jpyRate = rates["JPY"]?.toClientRate(),
            lastUpdate = formatRateDate(data.rateDate)
        )
    }

    private fun CurrencyRateDto.toClientRate(): Double? {
        val raw = officialRate ?: buyRate ?: sellRate ?: return null
        val divisor = (nominal ?: 1).takeIf { it > 0 } ?: 1
        return raw / divisor
    }

    private fun formatRateDate(rateDate: String?): String {
        if (rateDate.isNullOrBlank()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
            parser.parse(rateDate)?.let(formatter::format) ?: rateDate
        } catch (e: Exception) {
            rateDate
        }
    }

    private fun mapNews(items: List<NewsListItemDto>): HomeWidget.NewsWidget {
        val previews = items.mapNotNull { dto ->
            val title = dto.title?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            NewsPreview(id = dto.id, title = title)
        }
        return HomeWidget.NewsWidget(
            title = "Последние новости",
            newsCount = previews.size,
            latestNews = previews.take(3)
        )
    }

    private fun mapHoroscope(
        data: HoroscopeDataDto?,
        context: HomeMapperContext
    ): HoroscopeWidgetState {
        if (data != null) {
            val sign = data.zodiacSign
            val label = data.zodiacSignLabel
            val symbol = data.symbol
            val text = data.text
            if (!sign.isNullOrBlank() && !label.isNullOrBlank() && !text.isNullOrBlank()) {
                return HoroscopeWidgetState.Ready(
                    zodiacSign = sign,
                    zodiacSignLabel = label,
                    symbol = symbol.orEmpty(),
                    date = data.date,
                    text = text
                )
            }
        }
        return when {
            !context.isAuthenticated -> HoroscopeWidgetState.Anonymous
            !context.hasBirthday -> HoroscopeWidgetState.NoBirthday
            else -> HoroscopeWidgetState.Empty
        }
    }
}
