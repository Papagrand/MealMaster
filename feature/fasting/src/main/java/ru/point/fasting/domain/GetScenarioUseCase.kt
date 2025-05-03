package ru.point.fasting.domain

import ru.point.fasting.data.TimerRepository
import javax.inject.Inject

class GetScenarioUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {

    suspend operator fun invoke(scenarioId: String): Scenario? {
        return timerRepository.getScenarioById(scenarioId)
    }
}