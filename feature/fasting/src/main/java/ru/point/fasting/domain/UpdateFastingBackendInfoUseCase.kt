package ru.point.fasting.domain

import ru.point.api.timer_fasting.domain.UpdateFastingBackendInfoResult
import ru.point.fasting.data.TimerRepository
import javax.inject.Inject

class UpdateFastingBackendInfoUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke(
        userFastingId: String,
        userId: String,
        status: String,
        startTimeMillis: Long?,
        endTimeMillis: Long?,
        eatingWhileFast: Boolean,
        isActive: Boolean,
        lastUpdateMillis: Long
    ): UpdateFastingBackendInfoResult =
        timerRepository.updateFastingBackendInfo(
            userFastingId,
            userId,
            status,
            startTimeMillis,
            endTimeMillis,
            eatingWhileFast,
            isActive,
            lastUpdateMillis
        )
}
