package com.gorod.moygorodok.data.remote.model

import com.google.gson.annotations.SerializedName

data class EmergencyListResponse(
    val success: Boolean,
    val data: List<EmergencyContactDto> = emptyList(),
    val meta: EmergencyMetaDto? = null,
    val message: String? = null
)

data class EmergencyContactDto(
    val id: Int,
    val scope: String,
    val category: String,
    @SerializedName("category_label")
    val categoryLabel: String,
    val name: String,
    val phone: String,
    @SerializedName("phone_normalized")
    val phoneNormalized: String,
    val description: String?,
    @SerializedName("is_24h")
    val is24h: Boolean,
    @SerializedName("working_hours")
    val workingHours: String?,
    @SerializedName("icon_key")
    val iconKey: String,
    val color: String,
    val priority: Int,
    @SerializedName("is_federal")
    val isFederal: Boolean
)

data class EmergencyMetaDto(
    val city: EmergencyMetaCityDto?
)

data class EmergencyMetaCityDto(
    val id: Int,
    val name: String
)
