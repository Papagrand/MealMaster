package ru.point.fasting.di

import dagger.Module
import dagger.Provides
import ru.point.api.timer_fasting.data.TimerService
import ru.point.core_data.dao.ScenarioDao
import ru.point.core_data.dao.UserTimerDao
import ru.point.fasting.data.TimerRepository
import ru.point.fasting.data.TimerRepositoryImpl
import ru.point.fasting.domain.AdjustStartUseCase
import ru.point.fasting.domain.ChangeStartIntervalTimeUseCase
import ru.point.fasting.domain.GetScenarioUseCase
import ru.point.fasting.domain.UpdateFastingBackendInfoUseCase
import ru.point.fasting.domain.GetTimerUseCase
import ru.point.fasting.domain.StartTimerUseCase
import ru.point.fasting.domain.StopTimerUseCase
import ru.point.fasting.domain.TogglePhaseUseCase
import ru.point.fasting.domain.UpdateScenarioUseCase
import ru.point.fasting.ui.AlarmScheduler
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
    fun provideGetScenarioUseCase(repo: TimerRepository): GetScenarioUseCase =
        GetScenarioUseCase(repo)

    @Provides
    fun providerUpdateScenarioUseCase(repo: TimerRepository): UpdateScenarioUseCase =
        UpdateScenarioUseCase(repo)

    @Provides
    fun provideUpdateFastingBackendInfoUseCase(repo: TimerRepository): UpdateFastingBackendInfoUseCase =
        UpdateFastingBackendInfoUseCase(repo)

    @Provides
    fun provideStartTimerUseCase(repo: TimerRepository, alarmScheduler: AlarmScheduler): StartTimerUseCase =
        StartTimerUseCase(repo, alarmScheduler)

    @Provides
    fun provideStopTimerUseCase(repo: TimerRepository, alarmScheduler: AlarmScheduler): StopTimerUseCase =
        StopTimerUseCase(repo, alarmScheduler)

    @Provides
    fun provideAdjustStartUseCase(repo: TimerRepository, alarmScheduler: AlarmScheduler): AdjustStartUseCase =
        AdjustStartUseCase(repo, alarmScheduler)

    @Provides
    fun provideTogglePhaseUseCase(repo: TimerRepository, alarmScheduler: AlarmScheduler): TogglePhaseUseCase =
        TogglePhaseUseCase(repo, alarmScheduler)

    @Provides
    fun provideChangeStartIntervalTimeUseCase(repo: TimerRepository, alarmScheduler: AlarmScheduler): ChangeStartIntervalTimeUseCase =
        ChangeStartIntervalTimeUseCase(repo, alarmScheduler)


    @Provides
    fun provideFastingViewModelFactory(
        getTimerUseCaseProvider: Provider<GetTimerUseCase>,
        getScenarioUseCaseProvider: Provider<GetScenarioUseCase>,
        updateScenarioUseCaseProvider: Provider<UpdateScenarioUseCase>,
        updateFastingBackendInfoUseCaseProvider: Provider<UpdateFastingBackendInfoUseCase>,
        startTimerUseCaseProvider: Provider<StartTimerUseCase>,
        stopTimerUseCaseProvider: Provider<StopTimerUseCase>,
        adjustStartUseCaseProvider: Provider<AdjustStartUseCase>,
        togglePhaseUseCaseProvider: Provider<TogglePhaseUseCase>,
        changeStartIntervalTimeUseCaseProvider: Provider<ChangeStartIntervalTimeUseCase>

    ): FastingViewModelFactory =
        FastingViewModelFactory(
            getTimerUseCaseProvider,
            getScenarioUseCaseProvider,
            updateScenarioUseCaseProvider,
            updateFastingBackendInfoUseCaseProvider,
            startTimerUseCaseProvider,
            stopTimerUseCaseProvider,
            adjustStartUseCaseProvider,
            togglePhaseUseCaseProvider,
            changeStartIntervalTimeUseCaseProvider
        )
}