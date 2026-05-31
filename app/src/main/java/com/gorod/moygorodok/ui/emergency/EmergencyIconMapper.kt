package com.gorod.moygorodok.ui.emergency

import androidx.annotation.DrawableRes
import com.gorod.moygorodok.R

object EmergencyIconMapper {

    @DrawableRes
    fun drawableRes(key: String?): Int = when (key) {
        "shield" -> R.drawable.ic_emergency_shield
        "cross" -> R.drawable.ic_emergency_cross
        "flame" -> R.drawable.ic_emergency_flame
        "flame_circle" -> R.drawable.ic_emergency_flame_circle
        "lifebuoy" -> R.drawable.ic_emergency_lifebuoy
        "bolt" -> R.drawable.ic_emergency_bolt
        "drop" -> R.drawable.ic_emergency_drop
        "users" -> R.drawable.ic_emergency_users
        else -> R.drawable.ic_phone
    }
}
