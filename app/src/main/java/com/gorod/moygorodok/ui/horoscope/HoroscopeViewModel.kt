package com.gorod.moygorodok.ui.horoscope

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.ZodiacSign
import com.gorod.moygorodok.data.remote.model.HoroscopeDataDto
import com.gorod.moygorodok.data.repository.HoroscopeNotFoundException
import com.gorod.moygorodok.data.repository.HoroscopeRepository
import kotlinx.coroutines.launch

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

    fun selectSign(sign: ZodiacSign) {
        if (_selectedSign.value == sign && _state.value is HoroscopeUiState.Success) return
        _selectedSign.value = sign

        cache[sign]?.let { cached ->
            _state.value = cached
            if (cached is HoroscopeUiState.Success) return
        }

        _state.value = HoroscopeUiState.Loading
        viewModelScope.launch {
            val newState = try {
                repository.getBySign(sign.slug).fold(
                    onSuccess = { HoroscopeUiState.Success(it) },
                    onFailure = { HoroscopeUiState.Error(it.message ?: "Ошибка загрузки") }
                )
            } catch (e: HoroscopeNotFoundException) {
                HoroscopeUiState.NotAvailable
            }
            cache[sign] = newState
            if (_selectedSign.value == sign) {
                _state.value = newState
            }
        }
    }
}
