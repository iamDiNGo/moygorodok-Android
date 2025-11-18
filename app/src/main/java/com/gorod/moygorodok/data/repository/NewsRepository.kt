package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.model.MockNews
import com.gorod.moygorodok.data.model.News
import com.gorod.moygorodok.data.model.NewsCategory
import kotlinx.coroutines.delay

class NewsRepository {

    suspend fun getNewsList(): Result<List<News>> {
        // Имитация сетевой задержки
        delay(800)
        return Result.success(MockNews.getAll())
    }

    suspend fun getNewsById(id: String): Result<News> {
        delay(500)
        val news = MockNews.getById(id)
        return if (news != null) {
            Result.success(news)
        } else {
            Result.failure(Exception("Новость не найдена"))
        }
    }

    suspend fun getNewsByCategory(category: NewsCategory): Result<List<News>> {
        delay(600)
        return Result.success(MockNews.getByCategory(category))
    }

    suspend fun refreshNews(): Result<List<News>> {
        delay(1000)
        return Result.success(MockNews.getAll())
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
