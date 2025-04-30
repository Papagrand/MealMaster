package ru.point.fasting.data

import ru.point.api.timer_fasting.data.ScenarioResponse
import ru.point.api.timer_fasting.data.UserFastingInfoResponse
import ru.point.core_data.entity.ScenarioEntity
import ru.point.core_data.entity.UserTimerEntity
import java.time.Instant

object FastingMapper {
    fun map(dto: UserFastingInfoResponse): UserTimerEntity = UserTimerEntity(
        userFastingId    = dto.userFastingId,
        userId           = dto.userId,
        status           = dto.status,
        startTimeMillis  = dto.startTime?.let(Instant::parse)?.toEpochMilli(),
        endTimeMillis    = dto.endTime?.let(Instant::parse)?.toEpochMilli(),
        eatingWhileFast  = dto.eatingWhileFasting,
        isActive         = dto.isActive,
        lastUpdateMillis = dto.lastUpdate?.let(Instant::parse)?.toEpochMilli()
            ?: System.currentTimeMillis()
    )

    fun map(dto: ScenarioResponse): ScenarioEntity = ScenarioEntity(
        scenarioName   = dto.scenarioName,
        fastingHours   = dto.scenarioFasting,
        eatingHours    = dto.scenarioEating,
        description    = dto.scenarioDescription,
        notice         = dto.scenarioNotice,
        lastUpdate     = System.currentTimeMillis()
    )
}