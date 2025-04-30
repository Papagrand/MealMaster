package ru.point.fasting.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ru.point.api.timer_fasting.data.TimerService
import ru.point.api.timer_fasting.data.UserFastingInfoResponse
import ru.point.api.timer_fasting.data.ScenarioResponse
import ru.point.core_data.dao.ScenarioDao
import ru.point.core_data.dao.UserTimerDao
import ru.point.core_data.entity.UserTimerEntity
import ru.point.core_data.entity.ScenarioEntity
import ru.point.fasting.domain.TimerStatus
import ru.point.fasting.domain.UserTimer
import ru.point.fasting.domain.Scenario
import java.time.Instant

class TimerRepositoryImpl(
    private val service: TimerService,
    private val timerDao: UserTimerDao,
    private val scenarioDao: ScenarioDao
) : TimerRepository {

    override fun observeTimer(userId: String): Flow<UserTimer?> =
        timerDao.getTimerFlow(userId)
            .combine(scenarioDao.getCurrentScenarioFlow()) { utEnt, scEnt -> utEnt to scEnt }
            .onStart {
                val ufResponse = service.getUserFasting(userId)
                if (ufResponse.isSuccessful) {
                    ufResponse.body()?.data?.let { dto ->
                        // Мапим и сохраняем UserTimerEntity
                        val timerEntity = mapToUserTimerEntity(dto)
                        timerDao.upsert(timerEntity)
                        service.getCurrentFastingScenario(dto.pickedScenarioId)
                            .takeIf { it.isSuccessful }
                            ?.body()?.data
                            ?.let { scenarioDto ->
                                val scenarioEntity = mapToScenarioEntity(scenarioDto)
                                scenarioDao.upsert(scenarioEntity)
                            }
                    }
                }
            }
            .map { (utEnt, scEnt) ->
                if (utEnt == null || scEnt == null) return@map null

                // Собираем доменную модель с уже закешированным сценарием
                val scenario = Scenario(
                    name = scEnt.scenarioName,
                    fastingHours = scEnt.fastingHours,
                    eatingHours = scEnt.eatingHours,
                    description = scEnt.description,
                    notice = scEnt.notice
                )
                UserTimer(
                    userFastingId = utEnt.userFastingId,
                    userId = utEnt.userId,
                    status = TimerStatus.valueOf(utEnt.status),
                    startTime = utEnt.startTimeMillis,
                    endTime = utEnt.endTimeMillis,
                    eatingWhileFasting = utEnt.eatingWhileFast,
                    isActive = utEnt.isActive,
                    lastUpdate = utEnt.lastUpdateMillis,
                    scenario = scenario
                )
            }

    override suspend fun startTimer(userId: String) {
        val utEnt = timerDao.getTimerFlow(userId).firstOrNull() ?: return
        val scEnt = scenarioDao.getCurrentScenarioFlow().firstOrNull() ?: return
        val now = System.currentTimeMillis()
        val fastingMillis = scEnt.fastingHours * 3_600_000L

        timerDao.upsert(
            utEnt.copy(
                status = TimerStatus.FASTING.name,
                startTimeMillis = now,
                endTimeMillis = now + fastingMillis,
                isActive = true,
                lastUpdateMillis = now
            )
        )
    }

    override suspend fun stopTimer(userId: String) {
        val utEnt = timerDao.getTimerFlow(userId).firstOrNull() ?: return
        val now = System.currentTimeMillis()

        timerDao.upsert(
            utEnt.copy(
                status = TimerStatus.OFF.name,
                isActive = false,
                lastUpdateMillis = now
            )
        )
    }

    override suspend fun adjustStart(userId: String, newStartMillis: Long) {
        val utEnt = timerDao.getTimerFlow(userId).firstOrNull() ?: return
        val scEnt = scenarioDao.getCurrentScenarioFlow().firstOrNull() ?: return
        val now = System.currentTimeMillis()

        val hours = if (utEnt.status == TimerStatus.FASTING.name)
            scEnt.fastingHours else scEnt.eatingHours
        val durationMillis = hours * 3_600_000L

        timerDao.upsert(
            utEnt.copy(
                startTimeMillis = newStartMillis,
                endTimeMillis = newStartMillis + durationMillis,
                lastUpdateMillis = now
            )
        )
    }

    // --- Вспомогательные мапперы ---
    private fun mapToUserTimerEntity(dto: UserFastingInfoResponse): UserTimerEntity =
        UserTimerEntity(
            userFastingId = dto.userFastingId,
            userId = dto.userId,
            status = dto.status,
            startTimeMillis = dto.startTime?.let(Instant::parse)?.toEpochMilli(),
            endTimeMillis = dto.endTime?.let(Instant::parse)?.toEpochMilli(),
            eatingWhileFast = dto.eatingWhileFasting,
            isActive = dto.isActive,
            lastUpdateMillis = dto.lastUpdate
                ?.let(Instant::parse)?.toEpochMilli()
                ?: System.currentTimeMillis()
        )

    private fun mapToScenarioEntity(dto: ScenarioResponse): ScenarioEntity =
        ScenarioEntity(
            scenarioName = dto.scenarioName,
            fastingHours = dto.scenarioFasting,
            eatingHours = dto.scenarioEating,
            description = dto.scenarioDescription,
            notice = dto.scenarioNotice,
            lastUpdate = System.currentTimeMillis()
        )
}

