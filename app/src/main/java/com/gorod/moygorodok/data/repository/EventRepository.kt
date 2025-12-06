package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.model.Event
import com.gorod.moygorodok.data.model.EventCategory
import com.gorod.moygorodok.data.model.MockEvents
import kotlinx.coroutines.delay

class EventRepository {

    suspend fun getAllEvents(): List<Event> {
        delay(500)
        return MockEvents.getAll()
    }

    suspend fun getEventById(id: String): Event? {
        delay(300)
        return MockEvents.getById(id)
    }

    suspend fun getEventsByCategory(category: EventCategory): List<Event> {
        delay(300)
        return MockEvents.getByCategory(category)
    }

    suspend fun getFeaturedEvents(): List<Event> {
        delay(300)
        return MockEvents.getFeatured()
    }

    suspend fun searchEvents(query: String): List<Event> {
        delay(300)
        val lowerQuery = query.lowercase()
        return MockEvents.getAll().filter {
            it.title.lowercase().contains(lowerQuery) ||
            it.shortDescription.lowercase().contains(lowerQuery) ||
            it.tags.any { tag -> tag.lowercase().contains(lowerQuery) }
        }
    }
}
