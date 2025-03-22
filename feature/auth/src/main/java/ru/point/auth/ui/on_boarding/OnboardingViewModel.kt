package ru.point.auth.ui.on_boarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.point.api.profile_creating.CreateProfileRequest
import ru.point.api.profile_creating.CreateProfileResponse
import ru.point.api.profile_creating.ProfileService
import ru.point.auth.R
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Provider

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

data class DialogData(
    val message: String,
    val positiveButtonText: String,
    val negativeButtonText: String
)

sealed class OnboardingUiEvent {
    object NavigateToHomeProgress : OnboardingUiEvent()
    data class ShowSnackBar(val message: String) : OnboardingUiEvent()
    data class ShowWarningDialog(val dialogData: DialogData) : OnboardingUiEvent()
}

class OnboardingViewModel(
    private val profileService: ProfileService
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<OnboardingUiEvent>()
    val uiEvent: SharedFlow<OnboardingUiEvent> = _uiEvent.asSharedFlow()

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

    fun updateCanGo(canGo: Boolean) {
        _profileData.value = _profileData.value.copy(canGoToNextStep = canGo)
    }

    fun getCanGo(): Boolean {
        return _profileData.value.canGoToNextStep
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

    fun createProfile(userProfileId: String) {
        viewModelScope.launch {
            val response: CreateProfileResponse = profileService.createProfile(
                CreateProfileRequest(
                    userProfileId,
                    _profileData.value.firstName,
                    _profileData.value.lastName,
                    _profileData.value.height.toDouble(),
                    _profileData.value.weight.toDouble(),
                    _profileData.value.age,
                    _profileData.value.gender,
                    _profileData.value.activityLevel,
                    _profileData.value.goal,
                    _profileData.value.targetWeight.toDouble(),
                    _profileData.value.dietEndDate
                )
            )
            if (response.success) {
                _uiEvent.emit(OnboardingUiEvent.ShowSnackBar("Создание профиля прошло успешно"))
                _uiEvent.emit(OnboardingUiEvent.NavigateToHomeProgress)
            } else {
                _uiEvent.emit(OnboardingUiEvent.ShowSnackBar("Введены некорректные данные"))
            }

        }
    }

    suspend fun checkMassRate(lossMess: String, gainMess: String): Boolean {
        val profile = _profileData.value
        return when (profile.goal) {
            1 -> { // Похудение: текущий вес должен быть больше целевого
                if (profile.weight <= profile.targetWeight) {
                    _uiEvent.emit(OnboardingUiEvent.ShowSnackBar("Некорректные данные: для похудения вес должен быть больше целевого"))
                    false
                } else {
                    try {
                        val now = LocalDateTime.now()
                        val dietEnd = LocalDateTime.parse(profile.dietEndDate)
                        val daysBetween = ChronoUnit.DAYS.between(now, dietEnd)
                        if (daysBetween <= 0) true
                        else {
                            val months = daysBetween / 30.0
                            val massLossPerMonth = (profile.weight - profile.targetWeight) / months
                            if (massLossPerMonth > 2.0) {
                                _uiEvent.emit(
                                    OnboardingUiEvent.ShowWarningDialog(
                                        DialogData(
                                            message = lossMess,
                                            positiveButtonText = "Все равно завершить",
                                            negativeButtonText = "Отменить"
                                        )
                                    )
                                )
                                false
                            } else {
                                true
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("OnboardingViewModel", "Error parsing dietEndDate: ${e.message}")
                        true
                    }
                }
            }
            2 -> { // Набор массы: текущий вес должен быть меньше целевого
                if (profile.weight >= profile.targetWeight) {
                    _uiEvent.emit(OnboardingUiEvent.ShowSnackBar("Некорректные данные: для набора массы вес должен быть меньше целевого"))
                    false
                } else {
                    try {
                        val now = LocalDateTime.now()
                        val dietEnd = LocalDateTime.parse(profile.dietEndDate)
                        val daysBetween = ChronoUnit.DAYS.between(now, dietEnd)
                        if (daysBetween <= 0) true
                        else {
                            val months = daysBetween / 30.0
                            val massGainPerMonth = (profile.targetWeight - profile.weight) / months
                            if (massGainPerMonth > 1.0) {
                                _uiEvent.emit(
                                    OnboardingUiEvent.ShowWarningDialog(
                                        DialogData(
                                            message = gainMess,
                                            positiveButtonText = "Все равно завершить",
                                            negativeButtonText = "Отменить"
                                        )
                                    )
                                )
                                false
                            } else {
                                true
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("OnboardingViewModel", "Error parsing dietEndDate: ${e.message}")
                        true
                    }
                }
            }
            3 -> { // Поддержание веса: текущий вес должен совпадать с целевым
                if (profile.weight != profile.targetWeight) {
                    _uiEvent.emit(OnboardingUiEvent.ShowSnackBar("Некорректные данные: для поддержания веса текущий вес должен совпадать с целевым"))
                    false
                } else {
                    true
                }
            }
            else -> {
                _uiEvent.emit(OnboardingUiEvent.ShowSnackBar("Цель не совпадает"))
                false
            }
        }
    }


    class Factory @Inject constructor(
        private val profileService: Provider<ProfileService>
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == OnboardingViewModel::class.java)
            return OnboardingViewModel(profileService.get()) as T
        }
    }
}