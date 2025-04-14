package ru.point.api.profile_data.domain

import ru.point.api.profile_data.data.UpdatedProfileData
import ru.point.api.profile_data.domain.models.ProfileDataModel
import ru.point.api.profile_data.domain.models.ProfileMainDataModel

interface ProfileDataRepository {
    suspend fun getProfileMainData(userProfileId: String): Result<ProfileDataModel<ProfileMainDataModel>>
    suspend fun updateProfileInformation(
        userProfileId: String,
        newProfileData: UpdatedProfileData
    ): UpdateProfileResult
}