package ru.point.fasting.domain

import kotlinx.coroutines.flow.Flow
import ru.point.fasting.data.TimerRepository
import javax.inject.Inject

class GetScenarioUseCase @Inject constructor(
    private val timerRepository: TimerRepository
){

    operator fun invoke(): Flow<Scenario?> {
        return timerRepository.observeScenario()
    }

}