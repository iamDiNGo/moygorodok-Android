package com.gorod.moygorodok.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class LocationProvider private constructor(context: Context) {

    private val appContext = context.applicationContext
    private val client = LocationServices.getFusedLocationProviderClient(appContext)

    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrent(): Result<Location> {
        if (!hasPermission()) {
            return Result.failure(SecurityException("Нет разрешения на геолокацию"))
        }
        return try {
            val last = withTimeoutOrNull(2_000) { client.lastLocation.await() }
            if (last != null) return Result.success(last)

            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .build()
            val current = withTimeoutOrNull(5_000) {
                client.getCurrentLocation(request, null).await()
            }
            if (current != null) Result.success(current)
            else Result.failure(Exception("Не удалось определить координаты"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var instance: LocationProvider? = null

        fun getInstance(context: Context): LocationProvider {
            return instance ?: synchronized(this) {
                instance ?: LocationProvider(context.applicationContext).also { instance = it }
            }
        }
    }
}
