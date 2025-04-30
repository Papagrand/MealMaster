package ru.point.core_data

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.point.core_data.dao.UserTimerDao
import ru.point.core_data.dao.ScenarioDao

@Module
object DatabaseModule {

    @Provides
    fun provideAppDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "meal_master_db")
            .build()

    @Provides
    fun provideUserTimerDao(db: AppDatabase): UserTimerDao =
        db.userTimerDao()

    @Provides
    fun provideScenarioDao(db: AppDatabase): ScenarioDao =
        db.scenarioDao()
}