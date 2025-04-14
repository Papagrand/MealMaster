package ru.point.auth.ui.on_boarding.di

import dagger.Module
import dagger.Provides
import ru.point.api.profile_creating.data.ProfileRepositoryImpl
import ru.point.api.profile_creating.data.ProfileCreateService
import ru.point.auth.ui.on_boarding.domain.CheckMassRateUseCase
import ru.point.auth.ui.on_boarding.domain.CreateProfileUseCase
import ru.point.api.profile_creating.domain.ProfileRepository
import ru.point.auth.ui.on_boarding.ui.OnboardingViewModelFactory

@Module
object OnboardingModule {

    @Provides
    fun provideProfileRepository(
        profileCreateService: ProfileCreateService
    ): ProfileRepository {
        return ProfileRepositoryImpl(profileCreateService)
    }

    @Provides
    fun provideCreateProfileUseCase(
        profileRepository: ProfileRepository
    ): CreateProfileUseCase {
        return CreateProfileUseCase(profileRepository)
    }

    @Provides
    fun provideCheckMassRateUseCase(): CheckMassRateUseCase {
        return CheckMassRateUseCase()
    }

    @Provides
    fun provideOnboardingViewModelFactory(
        createProfileUseCase: CreateProfileUseCase,
        checkMassRateUseCase: CheckMassRateUseCase
    ): OnboardingViewModelFactory {
        return OnboardingViewModelFactory(
            createProfileUseCaseProvider = { createProfileUseCase },
            checkMassRateUseCaseProvider = { checkMassRateUseCase }
        )
    }
}