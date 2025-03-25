package ru.point.auth.ui.on_boarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.auth.ui.on_boarding.domain.CheckMassRateUseCase
import ru.point.auth.ui.on_boarding.domain.CreateProfileUseCase
import ru.point.auth.ui.on_boarding.domain.MassRateCheckResult
import ru.point.api.profile_creating.domain.ProfileCreateResult

class OnboardingViewModel(
    private val createProfileUseCase: CreateProfileUseCase,
    private val checkMassRateUseCase: CheckMassRateUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(OnboardingViewState())
    val viewState: StateFlow<OnboardingViewState> = _viewState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<OnboardingUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // --------------------------
    // Методы обновления state
    // --------------------------

    fun updatePersonalInfo(
        firstName: String,
        lastName: String,
        height: Float,
        weight: Float,
        age: Int,
        gender: Boolean
    ) {
        val oldState = _viewState.value
        val newProfile = oldState.profileData.copy(
            firstName = firstName,
            lastName = lastName,
            height = height,
            weight = weight,
            age = age,
            gender = gender
        )
        _viewState.value = oldState.copy(profileData = newProfile)
    }

    fun updateActivityLevel(level: Int) {
        val oldState = _viewState.value
        val newProfile = oldState.profileData.copy(activityLevel = level)
        _viewState.value = oldState.copy(profileData = newProfile)
    }

    fun updateGoal(goal: Int) {
        val oldState = _viewState.value
        val newProfile = oldState.profileData.copy(goal = goal)
        _viewState.value = oldState.copy(profileData = newProfile)
    }

    fun updateDietInfo(targetWeight: Float, dietEndDate: String) {
        val oldState = _viewState.value
        val trimmedDate = dietEndDate.trim()
        val formattedDate = if (!trimmedDate.contains("T")) {
            "${trimmedDate}T18:00:00"
        } else {
            trimmedDate
        }

        val newProfile = oldState.profileData.copy(
            targetWeight = targetWeight,
            dietEndDate = formattedDate
        )
        _viewState.value = oldState.copy(profileData = newProfile)
    }

    fun updateCanGo(canGo: Boolean) {
        _viewState.value = _viewState.value.copy(canGoToNextStep = canGo)
    }

    fun getCanGo(): Boolean = _viewState.value.canGoToNextStep

    // --------------------------
    // Логика создания профиля
    // --------------------------

    fun createProfile(userProfileId: String) {
        viewModelScope.launch {
            val profileData = _viewState.value.profileData
            when (val result = createProfileUseCase(userProfileId, profileData)) {
                is ProfileCreateResult.Success -> {
                    _uiEvent.emit(OnboardingUiEvent.ShowSnackBar("Создание профиля прошло успешно"))
                    _uiEvent.emit(OnboardingUiEvent.NavigateToHomeProgress)
                }

                is ProfileCreateResult.Error -> {
                    _uiEvent.emit(OnboardingUiEvent.ShowSnackBar(result.message))
                }
            }
        }
    }

    // --------------------------
    // Логика проверки темпа изменения массы
    // --------------------------

    suspend fun checkMassRate(lossMess: String, gainMess: String): Boolean {
        val profile = _viewState.value.profileData
        return when (val result = checkMassRateUseCase(profile, lossMess, gainMess)) {
            MassRateCheckResult.Valid -> {
                true
            }

            is MassRateCheckResult.Warning -> {
                // Показываем диалог
                _uiEvent.emit(
                    OnboardingUiEvent.ShowWarningDialog(
                        message = result.message,
                        positiveButtonText = "Все равно завершить",
                        negativeButtonText = "Отменить"
                    )
                )
                false
            }

            is MassRateCheckResult.Error -> {
                _uiEvent.emit(OnboardingUiEvent.ShowSnackBar(result.message))
                false
            }
        }
    }
}