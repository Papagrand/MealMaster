package ru.point.core_data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scenario")
data class ScenarioEntity(
    @PrimaryKey val scenarioName: String,
    val fastingHours: Int,
    val eatingHours: Int,
    val description: String,
    val notice: String?,
    val lastUpdate: Long
)
