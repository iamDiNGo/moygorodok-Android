package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.model.DailyWeather
import com.gorod.moygorodok.data.model.HourlyWeather
import com.gorod.moygorodok.data.model.Weather
import com.gorod.moygorodok.data.model.WeatherCondition
import com.gorod.moygorodok.data.remote.ApiClient
import com.gorod.moygorodok.data.remote.model.WeatherCellDataDto
import com.gorod.moygorodok.data.remote.model.WeatherDailyDto
import com.gorod.moygorodok.data.remote.model.WeatherHourlyDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class WeatherRepository private constructor() {

    private val api = ApiClient.apiService

    suspend fun getWeatherForCity(cityId: Int, cityName: String?): Result<Weather> {
        return try {
            val response = api.getWeatherForCity(cityId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(mapWeather(body.data, cityName))
            } else {
                Result.failure(Exception(body?.message ?: "Ошибка загрузки погоды"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    private fun mapWeather(dto: WeatherCellDataDto, cityName: String?): Weather {
        val current = dto.current
        val today = dto.daily?.firstOrNull()
        val temp = current?.temperature?.roundToInt() ?: 0

        return Weather(
            location = cityName.orEmpty(),
            currentTemp = temp,
            feelsLike = current?.feelsLike?.roundToInt(),
            condition = mapIcon(current?.icon),
            description = current?.description,
            highTemp = today?.tempMax?.roundToInt(),
            lowTemp = today?.tempMin?.roundToInt(),
            humidity = current?.humidity?.roundToInt(),
            windSpeed = current?.windSpeed,
            windDirection = current?.windDirection?.let(::compassDirection),
            pressure = current?.pressure?.roundToInt(),
            sunrise = formatTime(today?.sunrise),
            sunset = formatTime(today?.sunset),
            hourlyForecast = dto.hourly.orEmpty().mapNotNull(::mapHourly),
            dailyForecast = dto.daily.orEmpty().mapNotNull(::mapDaily)
        )
    }

    private fun mapHourly(dto: WeatherHourlyDto): HourlyWeather? {
        val time = dto.time ?: return null
        val temp = dto.temperature?.roundToInt() ?: return null
        return HourlyWeather(
            hour = formatTime(time) ?: time,
            temp = temp,
            condition = mapIcon(dto.icon),
            precipProbability = dto.precipitation?.roundToInt() ?: 0
        )
    }

    private fun mapDaily(dto: WeatherDailyDto): DailyWeather? {
        val date = dto.date ?: return null
        val high = dto.tempMax?.roundToInt() ?: return null
        val low = dto.tempMin?.roundToInt() ?: return null
        return DailyWeather(
            dayOfWeek = dayOfWeekLabel(date),
            date = formatDayMonth(date),
            highTemp = high,
            lowTemp = low,
            condition = mapIcon(dto.icon),
            precipProbability = dto.precipitation?.roundToInt() ?: 0
        )
    }

    private fun mapIcon(icon: String?): WeatherCondition = when (icon) {
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

    private fun compassDirection(degrees: Double): String {
        val labels = listOf("С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "З", "СЗ")
        val normalized = ((degrees % 360) + 360) % 360
        val idx = ((normalized + 22.5) / 45).toInt() % 8
        return labels[idx]
    }

    private fun formatTime(iso: String?): String? {
        if (iso.isNullOrBlank()) return null
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ROOT)
            val formatter = SimpleDateFormat("HH:mm", Locale.ROOT)
            parser.parse(iso)?.let(formatter::format)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatDayMonth(date: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            val formatter = SimpleDateFormat("d MMM", Locale("ru", "RU"))
            parser.parse(date)?.let(formatter::format) ?: date
        } catch (e: Exception) {
            date
        }
    }

    private fun dayOfWeekLabel(date: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            val parsed = parser.parse(date) ?: return ""
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
            if (date == today) return "Сегодня"
            SimpleDateFormat("EE", Locale("ru", "RU")).format(parsed)
                .replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            ""
        }
    }

    companion object {
        @Volatile
        private var instance: WeatherRepository? = null

        fun getInstance(): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository().also { instance = it }
            }
        }
    }
}
