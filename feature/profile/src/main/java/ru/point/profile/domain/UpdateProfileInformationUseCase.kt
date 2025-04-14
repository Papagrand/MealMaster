package ru.point.profile.domain

import ru.point.api.profile_data.data.UpdatedProfileData
import ru.point.api.profile_data.domain.ProfileDataRepository
import ru.point.api.profile_data.domain.UpdateProfileResult

class UpdateProfileInformationUseCase (
    private val profileDataRepository: ProfileDataRepository
) {

    suspend operator fun invoke(
        userProfileId: String,
        newProfileData: UpdatedProfileData
    ) : UpdateProfileResult {
        return profileDataRepository.updateProfileInformation(userProfileId, newProfileData)
    }

}