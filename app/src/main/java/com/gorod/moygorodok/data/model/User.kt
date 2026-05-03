package com.gorod.moygorodok.data.model

import com.gorod.moygorodok.data.remote.model.City
import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val email: String? = null,
    val phone: String,
    val avatar: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    val gender: String? = null,
    val role: String = "user",
    val status: String = "active",
    @SerializedName("city_id")
    val cityId: Int? = null,
    val city: City? = null,
    @SerializedName("last_login_at")
    val lastLoginAt: String? = null,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("is_admin")
    val isAdmin: Boolean = false,
    @SerializedName("is_blocked")
    val isBlocked: Boolean = false,
    @SerializedName("blocked_at")
    val blockedAt: String? = null,
    @SerializedName("blocked_reason")
    val blockedReason: String? = null
)
