package ru.point.profile.ui.update_profile_information

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.api.profile_data.data.UpdatedProfileData
import ru.point.api.profile_data.domain.UpdateProfileResult
import ru.point.api.profile_data.domain.models.ProfileMainDataModel
import ru.point.profile.domain.UpdateProfileInformationUseCase
import ru.point.profile.domain.UpdateProfileValidationUseCase
import ru.point.core.enums.ActivityLevel
import ru.point.core.enums.UserGoal
import javax.inject.Inject


data class UpdateProfileInformationState(
    val name: String = "",
    val nameError: String? = null,
    val secName: String = "",
    val secNameError: String? = null,
    val age: String = "",
    val ageError: String? = null,
    val gender: String = "",
    val genderError: String? = null,
    val activityLevel: String = "",
    val activityLevelError: String? = null,
    val goal: String = "",
    val goalError: String? = null,
    val goalWeight: String = "",
    val goalWeightError: String? = null,
    val dietEndDate: String = "",
    val dietEndDateError: String? = null,
    var wasUpdated: Boolean = false
)

class UpdateProfileInformationViewModel @Inject constructor(
    private val updateProfileValidationUseCase: UpdateProfileValidationUseCase,
    private val updateProfileInformationUseCase: UpdateProfileInformationUseCase
) : ViewModel() {

    // Поток состояния, который наблюдает UI
    private val _state = MutableStateFlow(UpdateProfileInformationState())
    val state: StateFlow<UpdateProfileInformationState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UpdateProfileUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun initializeState(profileData: ProfileMainDataModel) {
        _state.value = UpdateProfileInformationState(
            name = profileData.name ?: "",
            secName = profileData.secName ?: "",
            age = profileData.age.toString(),
            gender = if (profileData.gender) "Мужской" else "Женский",
            activityLevel = profileData.activityLevel,
            goal = profileData.goal,
            goalWeight = profileData.goalWeight.toString(),
            dietEndDate = profileData.goalTimeEnd.substringBefore("T")
        )
    }
    private val _rawNameInput = MutableStateFlow(_state.value.name)
    private val _rawSecNameInput = MutableStateFlow(_state.value.secName)
    private val _rawAgeInput = MutableStateFlow(_state.value.age)
    private val _rawGoalWeightInput = MutableStateFlow(_state.value.goalWeight)


    fun onNameChanged(input: String) {
        _rawNameInput.value = input.trim()
    }

    fun processName(){
        val result = updateProfileValidationUseCase.validateName(_rawNameInput.value, true)
        _state.value = _state.value.copy(
            name = result.formattedValue,
            nameError = result.errorMessage,
            wasUpdated = true
        )
    }

    fun onSecNameChanged(input: String) {
        _rawSecNameInput.value = input.trim()
    }

    fun processSecName(){
        val result = updateProfileValidationUseCase.validateName(_rawSecNameInput.value, false)
        _state.value = _state.value.copy(
            secName = result.formattedValue,
            secNameError = result.errorMessage,
            wasUpdated = true
        )
    }

    // Обновление поля возраста
    fun onAgeChanged(input: String) {
        _rawAgeInput.value = input.trim()
    }

    fun processAge(){
        val result = updateProfileValidationUseCase.validateAge(_rawAgeInput.value)
        _state.value = _state.value.copy(
            age = result.formattedValue,
            ageError = result.errorMessage,
            wasUpdated = true
        )
    }

    fun onGenderChanged(input: String) {
        val newGender = input.trim()
        _state.value = _state.value.copy(
            gender = newGender,
            wasUpdated = true
        )
    }

    fun onGoalWeightChanged(input: String) {
        _rawGoalWeightInput.value = input.trim()

    }

    fun processGoalWeight(){
        val result = updateProfileValidationUseCase.validateAge(_rawGoalWeightInput.value)
        _state.value = _state.value.copy(
            goalWeight = result.formattedValue,
            goalWeightError = result.errorMessage,
            wasUpdated = true
        )
    }

    fun onActivityLevelChanged(newActivityLevel: String) {
        _state.value = _state.value.copy(
            activityLevel = newActivityLevel,
            wasUpdated = true
        )
    }

    fun onDietGoalChanged(newDietGoal: String) {
        _state.value = _state.value.copy(
            goal = newDietGoal,
            wasUpdated = true
        )
    }


    fun onDietEndDateChanged(input: String) {
        val trimmedDate = input.trim()
        _state.value = _state.value.copy(
            dietEndDate = trimmedDate,
            wasUpdated = true
        )

    }
    fun updateAndSaveProfile(userProfileId: String) {
        viewModelScope.launch() {
            // Получите финальное состояние
            val currentState = _state.value

            when (val result = updateProfileInformationUseCase(
                userProfileId,
                UpdatedProfileData(
                    firstName = currentState.name,
                    lastName = currentState.secName,
                    age = currentState.age.toInt(),
                    gender = currentState.gender == "Мужской",
                    activityLevel = currentState.activityLevel,
                    goal = currentState.goal,
                    targetWeight = currentState.goalWeight.toDouble(),
                    dietEndDate = "${currentState.dietEndDate}T18:00:00"
                )
                )){
                is UpdateProfileResult.Success -> {
                    _uiEvent.emit(UpdateProfileUiEvent.ShowSnackBar("Данные профиля успешно обновлены"))
                    _uiEvent.emit(UpdateProfileUiEvent.NavigateToProfile)
                }

                is UpdateProfileResult.Error -> {
                    _uiEvent.emit(UpdateProfileUiEvent.ShowSnackBar(result.message))
                }
            }
        }
    }
}

sealed class UpdateProfileUiEvent {
    object NavigateToProfile : UpdateProfileUiEvent()
    data class ShowSnackBar(val message: String) : UpdateProfileUiEvent()
}