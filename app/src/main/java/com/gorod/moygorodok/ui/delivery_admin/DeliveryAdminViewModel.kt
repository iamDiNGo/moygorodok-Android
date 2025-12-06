package com.gorod.moygorodok.ui.delivery_admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.DeliveryAdmin
import com.gorod.moygorodok.data.model.EditableCategory
import com.gorod.moygorodok.data.model.EditableProduct
import com.gorod.moygorodok.data.model.MockDeliveryAdmin
import com.gorod.moygorodok.data.model.Promotion

class DeliveryAdminViewModel : ViewModel() {

    private val _deliveryAdmin = MutableLiveData<DeliveryAdmin>()
    val deliveryAdmin: LiveData<DeliveryAdmin> = _deliveryAdmin

    private val _categories = MutableLiveData<List<EditableCategory>>()
    val categories: LiveData<List<EditableCategory>> = _categories

    private val _products = MutableLiveData<List<EditableProduct>>()
    val products: LiveData<List<EditableProduct>> = _products

    private val _promotions = MutableLiveData<List<Promotion>>()
    val promotions: LiveData<List<Promotion>> = _promotions

    private val _isOpen = MutableLiveData<Boolean>()
    val isOpen: LiveData<Boolean> = _isOpen

    private val _deliveryDiscount = MutableLiveData<Int>()
    val deliveryDiscount: LiveData<Int> = _deliveryDiscount

    private val _freeDeliveryFrom = MutableLiveData<Double?>()
    val freeDeliveryFrom: LiveData<Double?> = _freeDeliveryFrom

    init {
        loadData()
    }

    fun loadData() {
        val admin = MockDeliveryAdmin.getDeliveryAdmin()
        _deliveryAdmin.value = admin
        _categories.value = MockDeliveryAdmin.getEditableCategories()
        _products.value = MockDeliveryAdmin.getEditableProducts()
        _promotions.value = MockDeliveryAdmin.getPromotions()
        _isOpen.value = admin.isOpen
        _deliveryDiscount.value = admin.deliveryDiscount
        _freeDeliveryFrom.value = admin.freeDeliveryFrom
    }

    fun toggleDeliveryOpen() {
        _isOpen.value = !(_isOpen.value ?: true)
    }

    fun setDeliveryDiscount(discount: Int) {
        _deliveryDiscount.value = discount
    }

    fun setFreeDeliveryFrom(amount: Double?) {
        _freeDeliveryFrom.value = amount
    }

    fun toggleCategoryVisibility(categoryId: String) {
        _categories.value = _categories.value?.map {
            if (it.id == categoryId) it.copy(isVisible = !it.isVisible) else it
        }
    }

    fun toggleProductAvailability(productId: String) {
        _products.value = _products.value?.map {
            if (it.id == productId) it.copy(isAvailable = !it.isAvailable) else it
        }
    }

    fun toggleProductPopular(productId: String) {
        _products.value = _products.value?.map {
            if (it.id == productId) it.copy(isPopular = !it.isPopular) else it
        }
    }

    fun togglePromotionActive(promotionId: String) {
        _promotions.value = _promotions.value?.map {
            if (it.id == promotionId) it.copy(isActive = !it.isActive) else it
        }
    }

    fun getProductsByCategory(categoryId: String): List<EditableProduct> {
        return _products.value?.filter { it.categoryId == categoryId } ?: emptyList()
    }
}
