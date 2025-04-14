package ru.point.profile.domain

import ru.point.api.profile_data.domain.ProfileDataRepository
import ru.point.api.profile_data.domain.models.ProfileDataModel
import ru.point.api.profile_data.domain.models.ProfileMainDataModel

class GetProfileMainDataUseCase (
    private val profileDataRepository: ProfileDataRepository
) {

    suspend operator fun invoke(userProfileId: String): Result<ProfileDataModel<ProfileMainDataModel>>{
        return profileDataRepository.getProfileMainData(userProfileId)
    }

}