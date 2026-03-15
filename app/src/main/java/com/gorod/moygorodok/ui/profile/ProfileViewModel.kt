package com.gorod.moygorodok.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.ProfileState
import com.gorod.moygorodok.data.model.User
import com.gorod.moygorodok.data.repository.AuthRepository
import com.gorod.moygorodok.data.repository.UnauthorizedException
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository.getInstance(application)

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _profileState = MutableLiveData<ProfileState>(ProfileState.Idle)
    val profileState: LiveData<ProfileState> = _profileState

    private val _isEditing = MutableLiveData<Boolean>(false)
    val isEditing: LiveData<Boolean> = _isEditing

    private val _nameError = MutableLiveData<String?>()
    val nameError: LiveData<String?> = _nameError

    private val _loggedOut = MutableLiveData<Boolean>(false)
    val loggedOut: LiveData<Boolean> = _loggedOut

    private val _saveSuccess = MutableLiveData<Boolean>(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    init {
        loadProfile()
    }

    fun loadProfile() {
        _profileState.value = ProfileState.Loading

        viewModelScope.launch {
            try {
                val result = repository.getProfile()
                result.fold(
                    onSuccess = { user ->
                        _user.value = user
                        _profileState.value = ProfileState.Success(user)
                    },
                    onFailure = { exception ->
                        val cachedUser = repository.getCurrentUser()
                        if (cachedUser != null) {
                            _user.value = cachedUser
                            _profileState.value = ProfileState.Success(cachedUser)
                        } else {
                            _profileState.value = ProfileState.Error(
                                exception.message ?: "Ошибка загрузки профиля"
                            )
                        }
                    }
                )
            } catch (e: UnauthorizedException) {
                _loggedOut.value = true
            }
        }
    }

    fun toggleEditMode() {
        _isEditing.value = !(_isEditing.value ?: false)
    }

    fun cancelEdit() {
        _isEditing.value = false
        _nameError.value = null
    }

    fun saveProfile(name: String, email: String?, gender: String?) {
        if (name.isBlank()) {
            _nameError.value = "Введите имя"
            return
        }
        if (name.length > 255) {
            _nameError.value = "Имя слишком длинное"
            return
        }
        _nameError.value = null

        _profileState.value = ProfileState.Loading

        viewModelScope.launch {
            try {
                val result = repository.updateProfile(
                    name = name,
                    email = email?.takeIf { it.isNotBlank() },
                    gender = gender
                )
                result.fold(
                    onSuccess = { updatedUser ->
                        _user.value = updatedUser
                        _isEditing.value = false
                        _saveSuccess.value = true
                        _profileState.value = ProfileState.Success(updatedUser)
                    },
                    onFailure = { exception ->
                        _profileState.value = ProfileState.Error(
                            exception.message ?: "Ошибка сохранения"
                        )
                    }
                )
            } catch (e: UnauthorizedException) {
                _loggedOut.value = true
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _loggedOut.value = true
        }
    }

    fun logoutAll() {
        viewModelScope.launch {
            repository.logoutAll()
            _loggedOut.value = true
        }
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()
}
