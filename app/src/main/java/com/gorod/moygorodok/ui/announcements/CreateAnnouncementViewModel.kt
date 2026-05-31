package com.gorod.moygorodok.ui.announcements

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.local.CityManager
import com.gorod.moygorodok.data.model.Announcement
import com.gorod.moygorodok.data.model.AnnouncementCategory
import com.gorod.moygorodok.data.model.AnnouncementPhoto
import com.gorod.moygorodok.data.remote.model.CreateAnnouncementRequest
import com.gorod.moygorodok.data.remote.model.UpdateAnnouncementRequest
import com.gorod.moygorodok.data.repository.AnnouncementRepository
import kotlinx.coroutines.launch

class CreateAnnouncementViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AnnouncementRepository.getInstance()
    private val cityManager = CityManager.getInstance(application)

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSubmitting = MutableLiveData(false)
    val isSubmitting: LiveData<Boolean> = _isSubmitting

    private val _existingPhotos = MutableLiveData<List<AnnouncementPhoto>>(emptyList())
    val existingPhotos: LiveData<List<AnnouncementPhoto>> = _existingPhotos

    private val _newPhotos = MutableLiveData<List<Uri>>(emptyList())
    val newPhotos: LiveData<List<Uri>> = _newPhotos

    private val _deletedExistingIds = mutableSetOf<Int>()

    var category: AnnouncementCategory? = null
    var isNegotiable: Boolean = false
    var editingAnnouncement: Announcement? = null
        private set

    private val _result = MutableLiveData<SubmitResult?>()
    val result: LiveData<SubmitResult?> = _result

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    val isEditMode: Boolean get() = editingAnnouncement != null

    fun loadForEditing(id: Int) {
        if (editingAnnouncement?.id == id) return
        _isLoading.value = true
        viewModelScope.launch {
            repository.fetchDetail(id).fold(
                onSuccess = { announcement ->
                    editingAnnouncement = announcement
                    category = announcement.category
                    isNegotiable = announcement.price == null
                    _existingPhotos.value = announcement.photos.orEmpty()
                },
                onFailure = { _errorMessage.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun addNewPhoto(uri: Uri) {
        if (totalVisiblePhotos() >= MAX_PHOTOS) return
        _newPhotos.value = (_newPhotos.value.orEmpty()) + uri
    }

    fun addNewPhotos(uris: List<Uri>) {
        val available = MAX_PHOTOS - totalVisiblePhotos()
        if (available <= 0) return
        _newPhotos.value = (_newPhotos.value.orEmpty()) + uris.take(available)
    }

    fun removeNewPhoto(index: Int) {
        val current = _newPhotos.value.orEmpty().toMutableList()
        if (index !in current.indices) return
        current.removeAt(index)
        _newPhotos.value = current
    }

    fun removeExistingPhoto(photoId: Int) {
        _deletedExistingIds.add(photoId)
        _existingPhotos.value = _existingPhotos.value.orEmpty().filter { it.id != photoId }
    }

    fun totalVisiblePhotos(): Int =
        (_existingPhotos.value?.size ?: 0) + (_newPhotos.value?.size ?: 0)

    fun submit(title: String, description: String, priceText: String, address: String?) {
        val cat = category ?: run {
            _errorMessage.value = getApplication<Application>().getString(
                com.gorod.moygorodok.R.string.announcement_validation_category
            )
            return
        }
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            _errorMessage.value = getApplication<Application>().getString(
                com.gorod.moygorodok.R.string.announcement_validation_title
            )
            return
        }
        val trimmedDescription = description.trim()
        if (trimmedDescription.isEmpty()) {
            _errorMessage.value = getApplication<Application>().getString(
                com.gorod.moygorodok.R.string.announcement_validation_description
            )
            return
        }
        val price: Double? = if (isNegotiable) null else priceText.toDoubleOrNull()
        val trimmedAddress = address?.trim()?.takeIf { it.isNotBlank() }

        viewModelScope.launch {
            val cityId = cityManager.getSelectedCityIdSync() ?: run {
                _errorMessage.value = getApplication<Application>().getString(
                    com.gorod.moygorodok.R.string.announcement_validation_city
                )
                return@launch
            }
            _isSubmitting.value = true

            val existing = editingAnnouncement
            if (existing == null) {
                doCreate(cityId, cat, trimmedTitle, trimmedDescription, price, trimmedAddress)
            } else {
                doUpdate(existing.id, cityId, cat, trimmedTitle, trimmedDescription, price, trimmedAddress)
            }

            _isSubmitting.value = false
        }
    }

    private suspend fun doCreate(
        cityId: Int,
        category: AnnouncementCategory,
        title: String,
        description: String,
        price: Double?,
        address: String?
    ) {
        val request = CreateAnnouncementRequest(
            cityId = cityId,
            category = category.apiValue,
            title = title,
            description = description,
            price = price,
            address = address
        )
        repository.create(request).fold(
            onSuccess = { announcement ->
                val (uploaded, failed) = uploadAllNewPhotos(announcement.id)
                _result.value = SubmitResult(
                    announcement = announcement,
                    isCreated = true,
                    uploadedPhotos = uploaded,
                    failedPhotos = failed,
                    totalNewPhotos = _newPhotos.value.orEmpty().size
                )
            },
            onFailure = { _errorMessage.value = it.message }
        )
    }

    private suspend fun doUpdate(
        id: Int,
        cityId: Int,
        category: AnnouncementCategory,
        title: String,
        description: String,
        price: Double?,
        address: String?
    ) {
        val request = UpdateAnnouncementRequest(
            cityId = cityId,
            category = category.apiValue,
            title = title,
            description = description,
            price = price,
            address = address
        )
        repository.update(id, request).fold(
            onSuccess = { announcement ->
                // Удаляем помеченные старые фото
                var failed = 0
                for (photoId in _deletedExistingIds.toList()) {
                    repository.deletePhoto(id, photoId).onFailure { failed++ }
                }
                val (uploaded, uploadFailed) = uploadAllNewPhotos(id)
                _result.value = SubmitResult(
                    announcement = announcement,
                    isCreated = false,
                    uploadedPhotos = uploaded,
                    failedPhotos = failed + uploadFailed,
                    totalNewPhotos = _newPhotos.value.orEmpty().size
                )
            },
            onFailure = { _errorMessage.value = it.message }
        )
    }

    private suspend fun uploadAllNewPhotos(announcementId: Int): Pair<Int, Int> {
        var uploaded = 0
        var failed = 0
        val context = getApplication<Application>()
        for (uri in _newPhotos.value.orEmpty()) {
            repository.uploadPhoto(context, announcementId, uri).fold(
                onSuccess = { uploaded++ },
                onFailure = { failed++ }
            )
        }
        return uploaded to failed
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun consumeResult() {
        _result.value = null
    }

    data class SubmitResult(
        val announcement: Announcement,
        val isCreated: Boolean,
        val uploadedPhotos: Int,
        val failedPhotos: Int,
        val totalNewPhotos: Int
    )

    companion object {
        const val MAX_PHOTOS = 8
    }
}
