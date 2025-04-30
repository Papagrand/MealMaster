package ru.point.core_data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_timer")
data class UserTimerEntity(
    @PrimaryKey val userFastingId: String,
    val userId: String,
    val status: String,
    val startTimeMillis: Long?,
    val endTimeMillis: Long?,
    val eatingWhileFast: Boolean,
    val isActive: Boolean,
    val lastUpdateMillis: Long
)
