package ru.point.mealmaster.di

import android.app.Application
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: Application) {
    @Provides
    fun provideApplication(): Application = app
}