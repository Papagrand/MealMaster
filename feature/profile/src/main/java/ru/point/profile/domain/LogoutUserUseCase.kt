package ru.point.profile.domain

import ru.point.api.profile_data.domain.LogoutUserResult
import ru.point.api.profile_data.domain.ProfileDataRepository

class LogoutUserUseCase (
    private val profileDataRepository: ProfileDataRepository,
) {

    suspend operator fun invoke(
        userId: String,
        deviceId: String
    ): LogoutUserResult = profileDataRepository.logoutUser(userId, deviceId)

}