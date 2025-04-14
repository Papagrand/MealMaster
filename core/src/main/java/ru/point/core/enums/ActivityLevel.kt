package ru.point.core.enums

enum class ActivityLevel(val level: Int) {
    VERY_LOW(1),
    LOW(2),
    MEDIUM(3),
    HIGH(4),
    VERY_HIGH(5);

    companion object {
        fun fromString(value: String): ActivityLevel? {
            return when (value) {
                "VERY_LOW" -> VERY_LOW
                "LOW" -> LOW
                "MEDIUM" -> MEDIUM
                "HIGH" -> HIGH
                "VERY_HIGH" -> VERY_HIGH
                else -> return null
            }
        }
    }
}