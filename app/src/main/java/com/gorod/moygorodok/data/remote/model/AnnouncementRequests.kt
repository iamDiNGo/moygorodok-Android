package com.gorod.moygorodok.data.remote.model

import com.gorod.moygorodok.data.model.Announcement
import com.gorod.moygorodok.data.model.AnnouncementPhoto
import com.google.gson.annotations.SerializedName

data class AnnouncementListResponse(
    val success: Boolean,
    val data: List<Announcement> = emptyList(),
    val meta: PaginationMeta? = null,
    val message: String? = null
)

data class AnnouncementDetailResponse(
    val success: Boolean,
    val data: Announcement? = null,
    val message: String? = null
)

data class AnnouncementPhotoResponse(
    val success: Boolean,
    val data: AnnouncementPhoto? = null,
    val message: String? = null
)

data class FavoriteToggleResponse(
    val success: Boolean,
    val data: FavoriteTogglePayload? = null,
    val message: String? = null
)

data class FavoriteTogglePayload(
    @SerializedName("is_favorite") val isFavorite: Boolean
)

data class CreateAnnouncementRequest(
    @SerializedName("city_id") val cityId: Int,
    val category: String,
    val title: String,
    val description: String,
    val price: Double? = null,
    val address: String? = null
)

data class UpdateAnnouncementRequest(
    @SerializedName("city_id") val cityId: Int,
    val category: String,
    val title: String,
    val description: String,
    val price: Double? = null,
    val address: String? = null
)
