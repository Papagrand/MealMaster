package ru.point.fasting.domain

data class Scenario(
    val name: String,
    val fastingHours: Int,
    val eatingHours: Int,
    val description: String,
    val notice: String?
)

enum class TimerStatus { OFF, FASTING, EATING }

data class UserTimer(
    val userFastingId: String,
    val userId: String,
    val status: TimerStatus,
    val startTime: Long?,
    val endTime: Long?,
    val eatingWhileFasting: Boolean,
    val isActive: Boolean,
    val lastUpdate: Long,
    val scenario: Scenario
)
