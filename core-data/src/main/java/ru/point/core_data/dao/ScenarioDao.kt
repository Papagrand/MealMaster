package ru.point.core_data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.point.core_data.entity.ScenarioEntity

@Dao
interface ScenarioDao {
    @Query("SELECT * FROM scenario LIMIT 1")
    fun getCurrentScenarioFlow(): Flow<ScenarioEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(scenario: ScenarioEntity)
}