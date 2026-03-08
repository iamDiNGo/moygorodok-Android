package com.gorod.moygorodok.data.remote.model

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    val success: Boolean,
    val data: List<HomeCellDto>,
    val meta: HomeMeta
)

data class HomeCellDto(
    val id: Int,
    val content: HomeCellContent,
    val style: HomeCellStyle,
    val width: Int,
    val height: Int,
    val image: String?,
    @SerializedName("action_type")
    val actionType: String,
    @SerializedName("action_target")
    val actionTarget: String,
    @SerializedName("action_params")
    val actionParams: Map<String, Any>?,
    @SerializedName("sort_order")
    val sortOrder: Int
)

data class HomeCellContent(
    val title: String,
    val subtitle: String? = null
)

data class HomeCellStyle(
    val background: String,
    @SerializedName("text_color")
    val textColor: String
)

data class HomeMeta(
    val total: Int,
    @SerializedName("city_id")
    val cityId: Int?
)
