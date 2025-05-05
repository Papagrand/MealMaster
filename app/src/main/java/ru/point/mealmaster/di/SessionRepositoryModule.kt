package ru.point.mealmaster.di

import android.app.Application
import dagger.Module
import dagger.Provides
import ru.point.core_data.SessionRepository

@Module
class SessionRepositoryModule {

    @Provides
    fun provideSessionRepository(app: Application) = SessionRepository(app)
}