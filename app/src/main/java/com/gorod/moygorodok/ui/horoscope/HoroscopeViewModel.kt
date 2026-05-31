package com.gorod.moygorodok.ui.horoscope

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.ZodiacSign
import com.gorod.moygorodok.data.remote.model.HoroscopeDataDto
import com.gorod.moygorodok.data.repository.HoroscopeRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class HoroscopeUiState {
    object Loading : HoroscopeUiState()
    data class Success(val data: HoroscopeDataDto) : HoroscopeUiState()
    object NotAvailable : HoroscopeUiState()
    data class Error(val message: String) : HoroscopeUiState()
}

class HoroscopeViewModel : ViewModel() {

    private val repository = HoroscopeRepository.getInstance()

    private val _selectedSign = MutableLiveData<ZodiacSign>()
    val selectedSign: LiveData<ZodiacSign> = _selectedSign

    private val _state = MutableLiveData<HoroscopeUiState>(HoroscopeUiState.Loading)
    val state: LiveData<HoroscopeUiState> = _state

    private val cache = mutableMapOf<ZodiacSign, HoroscopeUiState>()

    // Дата (yyyy-MM-dd), для которой валиден кэш. Гороскоп суточный —
    // при смене суток кэш сбрасывается, чтобы не показывать вчерашний прогноз.
    private var cacheDate: String? = null

    fun selectSign(sign: ZodiacSign) {
        if (_selectedSign.value == sign && _state.value is HoroscopeUiState.Success) return
        _selectedSign.value = sign
        load(sign, forceRefresh = false)
    }

    /** Повторная загрузка текущего знака без учёта кэша (кнопка «Повторить», reselect вкладки). */
    fun retry() {
        _selectedSign.value?.let { load(it, forceRefresh = true) }
    }

    private fun load(sign: ZodiacSign, forceRefresh: Boolean) {
        invalidateCacheIfNewDay()

        // Готовый ответ за сегодня (есть прогноз или его нет) переиспользуем без сети.
        // Error не кэшируем как валидный — его всегда перезапрашиваем.
        if (!forceRefresh) {
            val cached = cache[sign]
            if (cached is HoroscopeUiState.Success || cached is HoroscopeUiState.NotAvailable) {
                _state.value = cached
                return
            }
        }

        _state.value = HoroscopeUiState.Loading
        viewModelScope.launch {
            val newState = repository.getBySign(sign.slug).fold(
                onSuccess = { data ->
                    if (data != null) HoroscopeUiState.Success(data)
                    else HoroscopeUiState.NotAvailable
                },
                onFailure = { HoroscopeUiState.Error(it.message ?: "Ошибка загрузки") }
            )
            cache[sign] = newState
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
