package ru.point.profile.di

import dagger.Module
import dagger.Provides
import ru.point.api.profile_data.data.ProfileDataRepositoryImpl
import ru.point.api.profile_data.data.ProfileDataService
import ru.point.api.profile_data.domain.ProfileDataRepository
import ru.point.profile.domain.LogoutUserUseCase
import ru.point.profile.domain.UpdateProfileInformationUseCase
import ru.point.profile.domain.UpdateProfileValidationUseCase
import ru.point.profile.ui.update_profile_information.UpdateProfileInformationViewModelFactory

@Module
object UpdateProfileModule {

    @Provides
    fun provideProfileDataRepository(profileDataService: ProfileDataService): ProfileDataRepository{
        return ProfileDataRepositoryImpl(profileDataService)
    }



    @Provides
    fun provideUpdateProfileValidationUseCase() =
        UpdateProfileValidationUseCase()



    @Provides
    fun provideUpdateProfileInformationUseCase(
        profileDataRepository: ProfileDataRepository
    ) : UpdateProfileInformationUseCase{
        return UpdateProfileInformationUseCase(profileDataRepository)
    }

    @Provides
    fun provideUpdateProfileInformationViewModelFactory(
        updateProfileValidationUseCase: UpdateProfileValidationUseCase,
        updateProfileInformationUseCase: UpdateProfileInformationUseCase
    ) = UpdateProfileInformationViewModelFactory(
        updateProfileValidationUseCaseProvider = { updateProfileValidationUseCase },
        updateProfileInformationUseCaseProvider = { updateProfileInformationUseCase }
    )

}