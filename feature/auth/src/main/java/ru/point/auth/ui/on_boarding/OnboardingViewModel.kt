package ru.point.auth.ui.on_boarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Данные, собираемые на всех экранах онбординга
data class OnboardingProfileData(
    val firstName: String = "",
    val lastName: String = "",
    val height: Float = 0f,
    val weight: Float = 0f,
    val age: Int = 0,
    val gender: Boolean = true,
    // Второй экран: уровень активности от 1 до 5
    val activityLevel: Int = 0,
    // Третий экран: цель (например, 1 — похудение, 2 — набор массы, 3 — поддержание)
    val goal: Int = 0,
    // Четвёртый экран: конечный вес и дата окончания диеты
    val targetWeight: Float = 0f,
    val dietEndDate: String = "",
    val canGoToNextStep: Boolean = false
)

class OnboardingViewModel : ViewModel() {
    // StateFlow для хранения профиля, который будет обновляться по мере ввода данных
    private val _profileData = MutableStateFlow(OnboardingProfileData())
    val profileData: StateFlow<OnboardingProfileData> = _profileData

    fun updatePersonalInfo(
        firstName: String,
        lastName: String,
        height: Float,
        weight: Float,
        age: Int,
        gender: Boolean
    ) {
        _profileData.value = _profileData.value.copy(
            firstName = firstName,
            lastName = lastName,
            height = height,
            weight = weight,
            age = age,
            gender = gender
        )
    }

    fun updateCanGo(canGo: Boolean){
        _profileData.value = _profileData.value.copy(canGoToNextStep = canGo)
    }

    fun updateActivityLevel(level: Int) {
        _profileData.value = _profileData.value.copy(activityLevel = level)
    }

    fun updateGoal(goal: Int) {
        _profileData.value = _profileData.value.copy(goal = goal)
    }

    fun updateDietInfo(targetWeight: Float, dietEndDate: String) {
        val trimmedDate = dietEndDate.trim()
        val formattedDate = if (!trimmedDate.contains("T")) {
            "${trimmedDate}T18:00:00"
        } else {
            trimmedDate
        }
        _profileData.value = _profileData.value.copy(
            targetWeight = targetWeight,
            dietEndDate = formattedDate
        )
    }
}