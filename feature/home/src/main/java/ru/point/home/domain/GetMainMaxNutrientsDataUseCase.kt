package ru.point.home.domain

import ru.point.api.profile_data.domain.ProfileDataRepository
import ru.point.api.profile_data.domain.models.MainMaxNutrientsDataModel
import ru.point.api.profile_data.domain.models.ProfileDataModel

class GetMainMaxNutrientsDataUseCase(
    private val profileDataRepository: ProfileDataRepository
) {

    suspend operator fun invoke(userProfileId: String): Result<ProfileDataModel<MainMaxNutrientsDataModel>>{
        return profileDataRepository.getMainMaxNutrientsData(userProfileId)
    }

}