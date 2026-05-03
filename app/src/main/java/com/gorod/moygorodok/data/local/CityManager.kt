package com.gorod.moygorodok.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gorod.moygorodok.data.remote.model.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.cityDataStore by preferencesDataStore(name = "city_prefs")

class CityManager private constructor(context: Context) {

    private val dataStore = context.applicationContext.cityDataStore
    private val gson = Gson()

    val selectedCityId: Flow<Int?> = dataStore.data.map { it[KEY_SELECTED_ID] }
    val selectedCityName: Flow<String?> = dataStore.data.map { it[KEY_SELECTED_NAME] }
    val selectedCityRegion: Flow<String?> = dataStore.data.map { it[KEY_SELECTED_REGION] }

    val recentCityIds: Flow<List<Int>> = dataStore.data.map { prefs ->
        decodeIdList(prefs[KEY_RECENT_IDS])
    }

    val recentCities: Flow<List<City>> = dataStore.data.map { prefs ->
        decodeCityList(prefs[KEY_RECENT_CITIES_JSON])
    }

    val cachedOverviewJson: Flow<String?> = dataStore.data.map { it[KEY_OVERVIEW_JSON] }
    val cachedOverviewAt: Flow<Long?> = dataStore.data.map { it[KEY_OVERVIEW_AT] }

    suspend fun getSelectedCityIdSync(): Int? = dataStore.data.first()[KEY_SELECTED_ID]

    suspend fun getSelectedCityNameSync(): String? = dataStore.data.first()[KEY_SELECTED_NAME]

    suspend fun setSelectedCity(city: City) {
        dataStore.edit { prefs ->
            prefs[KEY_SELECTED_ID] = city.id
            prefs[KEY_SELECTED_NAME] = city.name
            city.region?.let { prefs[KEY_SELECTED_REGION] = it } ?: prefs.remove(KEY_SELECTED_REGION)
            updateRecent(prefs, city)
        }
    }

    suspend fun clearSelectedCity() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_SELECTED_ID)
            prefs.remove(KEY_SELECTED_NAME)
            prefs.remove(KEY_SELECTED_REGION)
        }
    }

    suspend fun saveOverviewCache(json: String) {
        dataStore.edit { prefs ->
            prefs[KEY_OVERVIEW_JSON] = json
            prefs[KEY_OVERVIEW_AT] = System.currentTimeMillis()
        }
    }

    private fun updateRecent(prefs: androidx.datastore.preferences.core.MutablePreferences, city: City) {
        val current = decodeCityList(prefs[KEY_RECENT_CITIES_JSON]).toMutableList()
        current.removeAll { it.id == city.id }
        current.add(0, city)
        val trimmed = current.take(MAX_RECENT)

        prefs[KEY_RECENT_CITIES_JSON] = gson.toJson(trimmed)
        prefs[KEY_RECENT_IDS] = trimmed.joinToString(",") { it.id.toString() }
    }

    private fun decodeIdList(raw: String?): List<Int> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    private fun decodeCityList(raw: String?): List<City> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<City>>() {}.type
            gson.fromJson<List<City>>(raw, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        private const val MAX_RECENT = 5

        private val KEY_SELECTED_ID: Preferences.Key<Int> = intPreferencesKey("selected_city_id")
        private val KEY_SELECTED_NAME: Preferences.Key<String> = stringPreferencesKey("selected_city_name")
        private val KEY_SELECTED_REGION: Preferences.Key<String> = stringPreferencesKey("selected_city_region")
        private val KEY_RECENT_IDS: Preferences.Key<String> = stringPreferencesKey("recent_city_ids")
        private val KEY_RECENT_CITIES_JSON: Preferences.Key<String> = stringPreferencesKey("recent_cities_json")
        private val KEY_OVERVIEW_JSON: Preferences.Key<String> = stringPreferencesKey("cached_overview_json")
        private val KEY_OVERVIEW_AT: Preferences.Key<Long> = longPreferencesKey("cached_overview_at")

        @Volatile
        private var instance: CityManager? = null

        fun getInstance(context: Context): CityManager {
            return instance ?: synchronized(this) {
                instance ?: CityManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
