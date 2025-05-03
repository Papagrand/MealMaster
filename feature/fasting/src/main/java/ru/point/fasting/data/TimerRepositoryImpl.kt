package ru.point.fasting.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.point.api.timer_fasting.data.TimerService
import ru.point.api.timer_fasting.data.UserFastingInfoResponse
import ru.point.api.timer_fasting.data.ScenarioResponse
import ru.point.api.timer_fasting.data.UpdateUserFastingRequest
import ru.point.api.timer_fasting.data.UpdateUserPickedScenarioRequest
import ru.point.api.timer_fasting.data.UpdateUserPickedScenarioResponse
import ru.point.api.timer_fasting.domain.UpdateFastingBackendInfoResult
import ru.point.core.secure_prefs.SecurePrefs
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


    override suspend fun updateFastingBackendInfo(
        userFastingId: String,
        userId: String,
        status: String,
        startTimeMillis: Long?,
        endTimeMillis: Long?,
        eatingWhileFast: Boolean,
        isActive: Boolean,
        lastUpdateMillis: Long
    ): UpdateFastingBackendInfoResult {
        return try {
            val response = service.updateUserFastingInfo(
                UpdateUserFastingRequest(
                    userFastingId = userFastingId,
                    userId = userId,
                    status = status,
                    startTimeMillis = startTimeMillis,
                    endTimeMillis = endTimeMillis,
                    eatingWhileFast = eatingWhileFast,
                    isActive = isActive,
                    lastUpdateMillis = lastUpdateMillis
                )
            )
            if (response.success) {
                UpdateFastingBackendInfoResult.Success
            } else {
                UpdateFastingBackendInfoResult.Error(response.message ?: "Неизвестная ошибка")
            }
        } catch (e: Exception) {
            UpdateFastingBackendInfoResult.Error(e.message ?: "Ошибка соединения")
        }
    }


    override suspend fun fetchAndCache(userId: String) {
        val localTimer = timerDao.getTimerFlow(userId).firstOrNull()
        if (localTimer != null) return

        val respTimer = service.getUserFasting(userId)
        if (respTimer.isSuccessful) {
            respTimer.body()?.data?.let { dto ->
                timerDao.upsert(mapToUserTimerEntity(dto))

                service.getFastingScenarioById(dto.pickedScenarioId)
                    .takeIf { it.isSuccessful }
                    ?.body()?.data
                    ?.let { scDto ->
                        scenarioDao.upsert(mapToScenarioEntity(scDto))
                    }
            }
        }
    }

    override suspend fun getScenarioById(scenarioId: String): Scenario? {
        val response = service.getFastingScenarioById(fastingId = scenarioId)

        if (!response.isSuccessful) {
            throw Exception("Ошибка сети при получении сценария: ${response.code()} ${response.message()}")
        }

        val dto = response.body()?.data
        return dto?.let {
            Scenario(
                name = it.scenarioName,
                fastingHours = it.scenarioFasting,
                eatingHours = it.scenarioEating,
                description = it.scenarioDescription,
                notice = it.scenarioNotice
            )
        }
    }

    override suspend fun updateUserScenario(
        userId: String,
        scenarioId: String
    ): UpdateFastingBackendInfoResult {

        return try {
            val response = service.updateUserPickedScenario(
                UpdateUserPickedScenarioRequest(
                    userId = userId,
                    pickedScenarioId = scenarioId
                )
            )
            if (response.success) {
                UpdateFastingBackendInfoResult.Success
            } else {
                UpdateFastingBackendInfoResult.Error(response.message ?: "Неизвестная ошибка")
            }
        } catch (e: Exception) {
            UpdateFastingBackendInfoResult.Error(e.message ?: "Ошибка соединения")
        }

    }


    override suspend fun clearLocalData() {
        // Удаляем все записи из таблиц
        timerDao.clearAllTimers()
        scenarioDao.clearAllScenarios()
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

    override suspend fun updateStartTime(userId: String, newStartMillis: Long) {
        val timer = timerDao.getTimerFlow(userId).firstOrNull() ?: return
        val scenario = scenarioDao.getCurrentScenarioFlow().firstOrNull() ?: return

        val durationHours = when (timer.status) {
            TimerStatus.FASTING.name -> scenario.fastingHours
            TimerStatus.EATING.name -> scenario.eatingHours
            else -> null
        }

        val now = System.currentTimeMillis()
        val newStart = newStartMillis
        val newEnd = durationHours?.let { newStart + it * 60 * 60 * 1000 }

        timerDao.upsert(
            timer.copy(
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

