package com.gorod.moygorodok.data.model

data class News(
    val id: Int,
    val slug: String?,
    val title: String,
    val summary: String?,
    val content: String?,
    val imageUrl: String?,
    val sourceType: String?,
    val sourceUrl: String?,
    val cityId: Int?,
    val publishedAt: String?
)
