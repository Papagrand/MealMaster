package ru.point.profile.domain

import ru.point.api.profile_data.domain.ProfileDataRepository
import ru.point.api.profile_data.domain.UpdateProfileResult

class UpdateWeightUseCase (
    private val profileDataRepository: ProfileDataRepository
) {
    suspend operator fun invoke(
        userProfileId: String,
        weight: Double
    ) : UpdateProfileResult {
        return profileDataRepository.updateWeight(userProfileId, weight)
    }
}