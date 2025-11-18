package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.model.Delivery
import com.gorod.moygorodok.data.model.DeliveryCategory
import com.gorod.moygorodok.data.model.MockDeliveries
import kotlinx.coroutines.delay

class DeliveryRepository {

    suspend fun getAllDeliveries(): List<Delivery> {
        delay(500)
        return MockDeliveries.getAll()
    }

    suspend fun getDeliveryById(id: String): Delivery? {
        delay(300)
        return MockDeliveries.getById(id)
    }

    suspend fun getDeliveriesByCategory(category: DeliveryCategory): List<Delivery> {
        delay(300)
        return MockDeliveries.getByCategory(category)
    }

    suspend fun searchDeliveries(query: String): List<Delivery> {
        delay(300)
        val lowerQuery = query.lowercase()
        return MockDeliveries.getAll().filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.description.lowercase().contains(lowerQuery)
        }
    }
}
