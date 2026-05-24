package com.gorod.moygorodok.data.model

data class EmergencyContact(
    val id: Int,
    val scope: String,
    val category: EmergencyCategory,
    val categoryLabel: String,
    val name: String,
    val phone: String,
    val phoneNormalized: String,
    val description: String?,
    val is24h: Boolean,
    val workingHours: String?,
    val iconKey: String,
    val color: String,
    val priority: Int,
    val isFederal: Boolean
)

enum class EmergencyCategory(val key: String, val displayName: String, val order: Int) {
    POLICE("police", "Полиция", 0),
    AMBULANCE("ambulance", "Скорая помощь", 1),
    FIRE("fire", "Пожарная служба", 2),
    GAS("gas", "Газовая служба", 3),
    RESCUE("rescue", "Спасательная служба", 4),
    ELECTRIC("electric", "Электросети", 5),
    WATER("water", "Водоканал", 6),
    SOCIAL("social", "Социальные службы", 7);

    companion object {
        fun fromKey(key: String?): EmergencyCategory? =
            entries.firstOrNull { it.key == key }
    }
}
