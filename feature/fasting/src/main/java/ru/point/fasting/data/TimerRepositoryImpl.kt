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
        timerDao
            .getTimerFlow(userId)
            .combine(scenarioDao.getCurrentScenarioFlow()) { utEnt, scEnt -> utEnt to scEnt }
            .map { (utEnt, scEnt) ->
                if (utEnt == null || scEnt == null) return@map null

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

    override fun observeScenario(): Flow<Scenario?> =
        scenarioDao
            .getCurrentScenarioFlow()
            .map { scenarioEntity ->
                scenarioEntity?.let {
                    Scenario(
                        name = it.scenarioName,
                        fastingHours = it.fastingHours,
                        eatingHours = it.eatingHours,
                        description = it.description,
                        notice = it.notice
                    )
                }
            }


    override suspend fun fetchAndCache(userId: String) {
        val localTimer = timerDao.getTimerFlow(userId).firstOrNull()
        if (localTimer != null) return

        val respTimer = service.getUserFasting(userId)
        if (respTimer.isSuccessful) {
            respTimer.body()?.data?.let { dto ->
                timerDao.upsert(mapToUserTimerEntity(dto))

                service.getCurrentFastingScenario(dto.pickedScenarioId)
                    .takeIf { it.isSuccessful }
                    ?.body()?.data
                    ?.let { scDto ->
                        scenarioDao.upsert(mapToScenarioEntity(scDto))
                    }
            }
        }
    }


    override suspend fun getCurrentTimer(userId: String): UserTimer? {
        val utEnt = timerDao.getTimerFlow(userId).firstOrNull() ?: return null
        val scEnt = scenarioDao.getCurrentScenarioFlow().firstOrNull() ?: return null
        return UserTimer(
            userFastingId = utEnt.userFastingId,
            userId = utEnt.userId,
            status = TimerStatus.valueOf(utEnt.status),
            startTime = utEnt.startTimeMillis,
            endTime = utEnt.endTimeMillis,
            eatingWhileFasting = utEnt.eatingWhileFast,
            isActive = utEnt.isActive,
            lastUpdate = utEnt.lastUpdateMillis,
            scenario = Scenario(
                name = scEnt.scenarioName,
                fastingHours = scEnt.fastingHours,
                eatingHours = scEnt.eatingHours,
                description = scEnt.description,
                notice = scEnt.notice
            )
        )
    }

    override suspend fun updateTimer(
        userId: String,
        status: TimerStatus,
        startMillis: Long,
        endMillis: Long
    ) {
        timerDao.getTimerFlow(userId).firstOrNull()?.let { ut ->
            timerDao.upsert(
                ut.copy(
                    status = status.name,
                    startTimeMillis = startMillis,
                    endTimeMillis = endMillis,
                    isActive = status != TimerStatus.OFF,
                    lastUpdateMillis = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun updateStatusOff(userId: String) {
        timerDao.getTimerFlow(userId).firstOrNull()?.let { ut ->
            timerDao.upsert(
                ut.copy(
                    status = TimerStatus.OFF.name,
                    startTimeMillis = null,
                    endTimeMillis = null,
                    isActive = false,
                    lastUpdateMillis = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun togglePhase(userId: String) {
        val timer = timerDao.getTimerFlow(userId).firstOrNull() ?: return
        val scenario = scenarioDao.getCurrentScenarioFlow().firstOrNull() ?: return
        val newStatus = when (timer.status) {
            TimerStatus.FASTING.name -> TimerStatus.EATING.name
            TimerStatus.EATING.name -> TimerStatus.FASTING.name
            else -> return
        }

        val durationHours = when (newStatus) {
            TimerStatus.FASTING.name -> scenario.fastingHours
            TimerStatus.EATING.name -> scenario.eatingHours
            else -> null
        }

        val now = System.currentTimeMillis()
        val newStart = now
        val newEnd = durationHours?.let { now + it * 60 * 60 * 1000 }

        timerDao.upsert(
            timer.copy(
                status = newStatus,
                startTimeMillis = newStart,
                endTimeMillis = newEnd,
                lastUpdateMillis = now
            )
        )
    }

    override suspend fun adjustTimerWindow(
        userId: String,
        newPhase: TimerStatus,
        newStartMillis: Long,
        newEndMillis: Long
    ) {
        timerDao.getTimerFlow(userId).firstOrNull()?.let { ut ->
            timerDao.upsert(
                ut.copy(
                    status = newPhase.name,
                    startTimeMillis = newStartMillis,
                    endTimeMillis = newEndMillis,
                    lastUpdateMillis = System.currentTimeMillis()
                )
            )
        }
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

