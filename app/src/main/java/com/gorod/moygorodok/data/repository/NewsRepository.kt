package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.model.News
import com.gorod.moygorodok.data.remote.ApiClient
import com.gorod.moygorodok.data.remote.model.NewsDetailDto
import com.gorod.moygorodok.data.remote.model.NewsListItemDto

class NewsRepository private constructor() {

    private val api = ApiClient.apiService

    suspend fun getNewsList(cityId: Int?, page: Int = 1): Result<NewsPage> {
        return try {
            val response = api.getNewsList(cityId = cityId, page = page)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                val items = body.data.mapNotNull { it.toDomain() }
                val meta = body.meta
                Result.success(
                    NewsPage(
                        items = items,
                        currentPage = meta?.currentPage ?: page,
                        lastPage = meta?.lastPage ?: page
                    )
                )
            } else {
                Result.failure(Exception("Ошибка загрузки новостей"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun getNewsById(id: Int): Result<News> {
        return try {
            val response = api.getNewsById(id)
            val body = response.body()
            when {
                response.isSuccessful && body?.success == true && body.data != null -> {
                    body.data.toDomain()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Некорректный ответ"))
                }
                response.code() == 404 -> Result.failure(Exception("Новость не найдена"))
                else -> Result.failure(Exception(body?.message ?: "Ошибка загрузки"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    private fun NewsListItemDto.toDomain(): News? {
        val newsId = id ?: return null
        val newsTitle = title ?: return null
        return News(
            id = newsId,
            slug = slug,
            title = newsTitle,
            summary = summary,
            content = null,
            imageUrl = imageUrl,
            sourceType = sourceType,
            sourceUrl = sourceUrl,
            cityId = cityId,
            publishedAt = publishedAt
        )
    }

    private fun NewsDetailDto.toDomain(): News? {
        val newsId = id ?: return null
        val newsTitle = title ?: return null
        return News(
            id = newsId,
            slug = slug,
            title = newsTitle,
            summary = summary,
            content = content,
            imageUrl = imageUrl,
            sourceType = sourceType,
            sourceUrl = sourceUrl,
            cityId = cityId,
            publishedAt = publishedAt
        )
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null

        fun getInstance(): NewsRepository {
            return instance ?: synchronized(this) {
                instance ?: NewsRepository().also { instance = it }
            }
        }
    }
}

data class NewsPage(
    val items: List<News>,
    val currentPage: Int,
    val lastPage: Int
) {
    val hasMore: Boolean get() = currentPage < lastPage
}
