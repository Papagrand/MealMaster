package ru.point.fasting.domain

import ru.point.api.timer_fasting.domain.UpdateFastingBackendInfoResult
import ru.point.fasting.data.TimerRepository
import javax.inject.Inject

class UpdateScenarioUseCase @Inject constructor(
    private val repo: TimerRepository
){
    suspend operator fun invoke(userId: String, scenarioId: String): UpdateFastingBackendInfoResult {
        val result = repo.updateUserScenario(userId, scenarioId)
        if (result is UpdateFastingBackendInfoResult.Success) {
            repo.clearLocalData()
        }
        return result
    }
}