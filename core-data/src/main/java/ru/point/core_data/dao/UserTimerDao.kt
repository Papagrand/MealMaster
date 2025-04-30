package ru.point.core_data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.point.core_data.entity.UserTimerEntity

@Dao
interface UserTimerDao {
    @Query("SELECT * FROM user_timer WHERE userId = :id")
    fun getTimerFlow(id: String): Flow<UserTimerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(timer: UserTimerEntity)
}
