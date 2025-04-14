package ru.point.core.enums

enum class UserGoal (val goalNumber: Int) {
    LOSS(1),
    GAIN(2),
    MAINTENANCE(3);

    companion object {
        fun fromString(value: String): UserGoal? {
            return when (value) {
                "LOSS" -> LOSS
                "GAIN" -> GAIN
                "MAINTENANCE" -> MAINTENANCE
                else -> return null
            }
        }
    }


}