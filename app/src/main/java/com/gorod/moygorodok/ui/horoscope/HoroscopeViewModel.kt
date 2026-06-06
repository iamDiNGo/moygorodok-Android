package com.gorod.moygorodok.ui.horoscope

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.ZodiacSign
import com.gorod.moygorodok.data.remote.model.HoroscopeBundleDto
import com.gorod.moygorodok.data.remote.model.HoroscopeDataDto
import kotlinx.coroutines.launch
import com.gorod.moygorodok.data.repository.HoroscopeRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Периоды прогноза в бандле. */
enum class HoroscopePeriod(val label: String) {
    TODAY("Сегодня"),
    TOMORROW("Завтра"),
    WEEKLY("Неделя"),
    MONTHLY("Месяц")
}

/** Данные конкретного периода из загруженного бандла (null — прогноза на период нет). */
fun HoroscopeBundleDto.periodData(period: HoroscopePeriod): HoroscopeDataDto? = when (period) {
    HoroscopePeriod.TODAY -> today
    HoroscopePeriod.TOMORROW -> tomorrow
    HoroscopePeriod.WEEKLY -> weekly
    HoroscopePeriod.MONTHLY -> monthly
}

sealed class HoroscopeUiState {
    object Loading : HoroscopeUiState()
    data class Success(val bundle: HoroscopeBundleDto) : HoroscopeUiState()
    data class Error(val message: String) : HoroscopeUiState()
}

class HoroscopeViewModel : ViewModel() {

    private val repository = HoroscopeRepository.getInstance()

    private val _selectedSign = MutableLiveData<ZodiacSign>()
    val selectedSign: LiveData<ZodiacSign> = _selectedSign

    private val _selectedPeriod = MutableLiveData(HoroscopePeriod.TODAY)
    val selectedPeriod: LiveData<HoroscopePeriod> = _selectedPeriod

    private val _state = MutableLiveData<HoroscopeUiState>(HoroscopeUiState.Loading)
    val state: LiveData<HoroscopeUiState> = _state

    // Кэш загруженных бандлов по знаку. Гороскоп суточный — кэш валиден в пределах дня.
    private val cache = mutableMapOf<ZodiacSign, HoroscopeUiState>()
    private var cacheDate: String? = null

    fun selectSign(sign: ZodiacSign) {
        if (_selectedSign.value == sign && _state.value is HoroscopeUiState.Success) return
        _selectedSign.value = sign
        load(sign, forceRefresh = false)
    }

    /** Переключение периода — данные уже в загруженном бандле, сеть не нужна. */
    fun selectPeriod(period: HoroscopePeriod) {
        if (_selectedPeriod.value != period) {
            _selectedPeriod.value = period
        }
    }

    /** Повтор запроса текущего знака без учёта кэша (кнопка «Повторить», reselect вкладки). */
    fun retry() {
        _selectedSign.value?.let { load(it, forceRefresh = true) }
    }

    private fun load(sign: ZodiacSign, forceRefresh: Boolean) {
        invalidateCacheIfNewDay()

        // Загруженный бандл за сегодня переиспользуем без сети. Error не кэшируем.
        if (!forceRefresh) {
            val cached = cache[sign]
            if (cached is HoroscopeUiState.Success) {
                _state.value = cached
                return
            }
        }

        _state.value = HoroscopeUiState.Loading
        viewModelScope.launch {
            val newState = repository.getBundle(sign.slug).fold(
                onSuccess = { bundle ->
                    if (bundle != null) HoroscopeUiState.Success(bundle)
                    else HoroscopeUiState.Success(HoroscopeBundleDto(zodiacSign = sign.slug))
                },
                onFailure = { HoroscopeUiState.Error(it.message ?: "Ошибка загрузки") }
            )
            if (newState is HoroscopeUiState.Success) {
                cache[sign] = newState
            }
            if (_selectedSign.value == sign) {
                _state.value = newState
            }
        }
    }

    private fun invalidateCacheIfNewDay() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
        if (cacheDate != today) {
            cache.clear()
            cacheDate = today
        }
    }
}
