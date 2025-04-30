package ru.point.core_data

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.point.core_data.dao.ScenarioDao
import ru.point.core_data.dao.UserTimerDao
import ru.point.core_data.entity.ScenarioEntity
import ru.point.core_data.entity.UserTimerEntity

@Database(
    entities = [UserTimerEntity::class, ScenarioEntity::class ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userTimerDao(): UserTimerDao
    abstract fun scenarioDao(): ScenarioDao
}