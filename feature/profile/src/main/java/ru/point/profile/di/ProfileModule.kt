package ru.point.profile.di

import dagger.Module
import dagger.Provides
import ru.point.api.profile_data.data.ProfileDataRepositoryImpl
import ru.point.api.profile_data.data.ProfileDataService
import ru.point.api.profile_data.domain.ProfileDataRepository
import ru.point.profile.domain.GetProfileMainDataUseCase
import ru.point.profile.ui.profile.ProfileViewModelFactory

@Module
object ProfileModule {

    @Provides
    fun provideProfileDataRepository(profileDataService: ProfileDataService): ProfileDataRepository{
        return ProfileDataRepositoryImpl(profileDataService)
    }

    @Provides
    fun provideGetProfileMainDataUseCase(profileDataRepository: ProfileDataRepository) =
        GetProfileMainDataUseCase(profileDataRepository)

    @Provides
    fun provideProfileViewModelFactory(
        getProfileMainDataUseCase: GetProfileMainDataUseCase
    ) = ProfileViewModelFactory(
        getProfileMainDataUseCaseProvider = { getProfileMainDataUseCase }
    )

}