package com.gorod.moygorodok.ui.avatar

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.User
import com.gorod.moygorodok.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AvatarViewModel : ViewModel() {

    private val repository = AuthRepository.getInstance()

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        _user.value = repository.getCurrentUser()
    }

    fun setSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun saveAvatar() {
        val uri = _selectedImageUri.value
        if (uri == null) {
            _errorMessage.value = "Выберите изображение"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            // В реальном приложении здесь было бы загрузка на сервер
            // Для демо просто сохраняем URI как строку
            val result = repository.updateAvatar(uri.toString())
            result.fold(
                onSuccess = { updatedUser ->
                    _user.value = updatedUser
                    _saveSuccess.value = true
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Ошибка сохранения"
                    _isLoading.value = false
                }
            )
        }
    }

    fun removeAvatar() {
        _selectedImageUri.value = null

        _isLoading.value = true

        viewModelScope.launch {
            val result = repository.updateAvatar("")
            result.fold(
                onSuccess = { updatedUser ->
                    _user.value = updatedUser
                    _saveSuccess.value = true
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Ошибка удаления"
                    _isLoading.value = false
                }
            )
        }
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
