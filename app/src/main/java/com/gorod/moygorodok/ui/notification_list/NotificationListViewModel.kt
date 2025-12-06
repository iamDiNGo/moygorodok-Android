package com.gorod.moygorodok.ui.notification_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.AppNotification
import com.gorod.moygorodok.data.model.MockNotifications

class NotificationListViewModel : ViewModel() {

    private val _notifications = MutableLiveData<List<AppNotification>>()
    val notifications: LiveData<List<AppNotification>> = _notifications

    private val _unreadCount = MutableLiveData<Int>()
    val unreadCount: LiveData<Int> = _unreadCount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        _isLoading.value = true
        val allNotifications = MockNotifications.getNotifications()
        _notifications.value = allNotifications
        _unreadCount.value = allNotifications.count { !it.isRead }
        _isLoading.value = false
    }

    fun markAsRead(notificationId: String) {
        val currentList = _notifications.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isRead = true)
            _notifications.value = currentList
            _unreadCount.value = currentList.count { !it.isRead }
        }
    }

    fun markAllAsRead() {
        val currentList = _notifications.value?.map { it.copy(isRead = true) } ?: return
        _notifications.value = currentList
        _unreadCount.value = 0
    }

    fun deleteNotification(notificationId: String) {
        val currentList = _notifications.value?.toMutableList() ?: return
        currentList.removeAll { it.id == notificationId }
        _notifications.value = currentList
        _unreadCount.value = currentList.count { !it.isRead }
    }

    fun refresh() {
        loadNotifications()
    }
}
