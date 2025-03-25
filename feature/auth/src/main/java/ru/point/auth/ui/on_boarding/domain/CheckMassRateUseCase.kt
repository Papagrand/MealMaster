package ru.point.auth.ui.on_boarding.domain

import ru.point.api.profile_creating.data.OnboardingProfileData
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

sealed class MassRateCheckResult {
    object Valid : MassRateCheckResult()
    data class Warning(val message: String) : MassRateCheckResult()
    data class Error(val message: String) : MassRateCheckResult()
}

class CheckMassRateUseCase {

    operator fun invoke(
        profile: OnboardingProfileData,
        lossMess: String,
        gainMess: String
    ): MassRateCheckResult {
        return when (profile.goal) {
            1 -> checkWeightLoss(profile, lossMess)
            2 -> checkWeightGain(profile, gainMess)
            3 -> checkMaintenance(profile)
            else -> MassRateCheckResult.Error("Цель не совпадает")
        }
    }

    private fun checkWeightLoss(
        profile: OnboardingProfileData,
        lossMess: String
    ): MassRateCheckResult {
        if (profile.weight <= profile.targetWeight) {
            return MassRateCheckResult.Error(
                "Некорректные данные: для похудения вес должен быть больше целевого"
            )
        }

        return try {
            val now = LocalDateTime.now()
            val dietEnd = LocalDateTime.parse(profile.dietEndDate)
            val daysBetween = ChronoUnit.DAYS.between(now, dietEnd)
            if (daysBetween <= 0) {
                MassRateCheckResult.Valid
            } else {
                val months = daysBetween / 30.0
                val massLossPerMonth = (profile.weight - profile.targetWeight) / months
                if (massLossPerMonth > 2.0) {
                    MassRateCheckResult.Warning(lossMess)
                } else {
                    MassRateCheckResult.Valid
                }
            }
        } catch (e: Exception) {
            MassRateCheckResult.Valid // Или вернуть ошибку
        }
    }

    private fun checkWeightGain(
        profile: OnboardingProfileData,
        gainMess: String
    ): MassRateCheckResult {
        if (profile.weight >= profile.targetWeight) {
            return MassRateCheckResult.Error(
                "Некорректные данные: для набора массы вес должен быть меньше целевого"
            )
        }

        return try {
            val now = LocalDateTime.now()
            val dietEnd = LocalDateTime.parse(profile.dietEndDate)
            val daysBetween = ChronoUnit.DAYS.between(now, dietEnd)
            if (daysBetween <= 0) {
                MassRateCheckResult.Valid
            } else {
                val months = daysBetween / 30.0
                val massGainPerMonth = (profile.targetWeight - profile.weight) / months
                if (massGainPerMonth > 1.0) {
                    MassRateCheckResult.Warning(gainMess)
                } else {
                    MassRateCheckResult.Valid
                }
            }
        } catch (e: Exception) {
            MassRateCheckResult.Valid // Или вернуть ошибку
        }
    }

    private fun checkMaintenance(profile: OnboardingProfileData): MassRateCheckResult {
        return if (profile.weight != profile.targetWeight) {
            MassRateCheckResult.Error(
                "Некорректные данные: для поддержания веса текущий вес должен совпадать с целевым"
            )
        } else {
            MassRateCheckResult.Valid
        }
    }
}