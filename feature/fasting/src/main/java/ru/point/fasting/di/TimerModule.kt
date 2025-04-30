package ru.point.fasting.di

import dagger.Module
import dagger.Provides
import ru.point.api.timer_fasting.data.TimerService
import ru.point.core_data.dao.ScenarioDao
import ru.point.core_data.dao.UserTimerDao
import ru.point.fasting.data.TimerRepository
import ru.point.fasting.data.TimerRepositoryImpl
import ru.point.fasting.domain.AdjustStartUseCase
import ru.point.fasting.domain.GetTimerUseCase
import ru.point.fasting.domain.StartTimerUseCase
import ru.point.fasting.domain.StopTimerUseCase
import ru.point.fasting.ui.FastingViewModelFactory
import javax.inject.Provider

@Module
object TimerModule {

    @Provides
    fun provideTimerRepository(
        service: TimerService,
        timerDao: UserTimerDao,
        scenarioDao: ScenarioDao
    ): TimerRepository =
        TimerRepositoryImpl(service, timerDao, scenarioDao)

    @Provides
    fun provideGetTimerUseCase(repo: TimerRepository): GetTimerUseCase =
        GetTimerUseCase(repo)

    @Provides
    fun provideStartTimerUseCase(repo: TimerRepository): StartTimerUseCase =
        StartTimerUseCase(repo)

    @Provides
    fun provideStopTimerUseCase(repo: TimerRepository): StopTimerUseCase =
        StopTimerUseCase(repo)

    @Provides
    fun provideAdjustStartUseCase(repo: TimerRepository): AdjustStartUseCase =
        AdjustStartUseCase(repo)

    @Provides
    fun provideFastingViewModelFactory(
        getTimerUseCaseProvider: Provider<GetTimerUseCase>,
        startTimerUseCaseProvider: Provider<StartTimerUseCase>,
        stopTimerUseCaseProvider: Provider<StopTimerUseCase>,
        adjustStartUseCaseProvider: Provider<AdjustStartUseCase>
    ): FastingViewModelFactory =
        FastingViewModelFactory(
            getTimerUseCaseProvider,
            startTimerUseCaseProvider,
            stopTimerUseCaseProvider,
            adjustStartUseCaseProvider
        )
}