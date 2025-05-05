package ru.point.profile.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.point.api.meal.domain.UpdateMealItemResult
import ru.point.api.profile_data.domain.LogoutUserResult
import ru.point.api.profile_data.domain.UpdateProfileResult
import ru.point.api.profile_data.domain.models.ProfileMainDataModel
import ru.point.core.LogoutHandler
import ru.point.profile.domain.GetProfileMainDataUseCase
import ru.point.profile.domain.LogoutUserUseCase
import ru.point.profile.domain.UpdateWeightUseCase
import ru.point.profile.domain.ValidationResult
import ru.point.profile.ui.update_profile_information.UpdateProfileUiEvent

data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val profileData: ProfileMainDataModel? = null,
    val currentWeight: String = "",
    val currentWeightError: String? = null,
    var weightChanged: Boolean = false
)

sealed class ProfileUiEvent {
    data class ShowToast(val message: String) : ProfileUiEvent()
    data object NavigateToUpdateProfileInformationFragment : ProfileUiEvent()
    data object NavigateToLoginFragment : ProfileUiEvent()
}

class ProfileViewModel (
    private val getProfileMainDataUseCase: GetProfileMainDataUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val updateWeightUseCase: UpdateWeightUseCase,
    private val logoutHandlers: Set<@JvmSuppressWildcards LogoutHandler>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent: SharedFlow<ProfileUiEvent> = _uiEvent.asSharedFlow()

    private val _rawWeightInput = MutableStateFlow(_uiState.value.currentWeight)

    fun getProfileData(userProfileId: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = getProfileMainDataUseCase(userProfileId)
            result.fold(
                onSuccess = { profileResponse ->
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        profileData = profileResponse.data
                    )
                },
                onFailure = {ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.message
                    )
                    _uiEvent.emit(ProfileUiEvent.ShowToast("Ошибка: ${ex.message}"))
                }
            )
        }
    }

    fun updateWeight(userProfileId: String){
        viewModelScope.launch {
            val currentWeight = _uiState.value.currentWeight
            when(val result = updateWeightUseCase(
                userProfileId,
                currentWeight.toDouble()
            )){
                is UpdateProfileResult.Success -> {
                    getProfileData(userProfileId)
                    _uiEvent.emit(ProfileUiEvent.ShowToast("Данные успешно обновлены"))
                }
                is UpdateProfileResult.Error -> {
                    _uiEvent.emit(ProfileUiEvent.ShowToast(result.message))
                }
            }
        }
    }

    fun backToDefault(){
        _rawWeightInput.value = _uiState.value.profileData?.weight.toString()
    }

    fun goToUpdateProfileInformation(){
        viewModelScope.launch {
            _uiEvent.emit(ProfileUiEvent.NavigateToUpdateProfileInformationFragment)
        }
    }

    fun onWeightChanged(input: String) {
        _rawWeightInput.value = input.trim()

    }

    fun processWeight(){
        val result = validateAge(_rawWeightInput.value)
        _uiState.value = _uiState.value.copy(
            currentWeight = result.formattedValue,
            currentWeightError = result.errorMessage,
            weightChanged = true
        )
    }


    fun logoutUser(userId: String, deviceId: String){
        viewModelScope.launch {
            val result = logoutUserUseCase(userId, deviceId)
            when(result){
                is LogoutUserResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    logoutHandlers.forEach { it.onLogout() }
                    _uiEvent.emit(ProfileUiEvent.NavigateToLoginFragment)
                }

                is LogoutUserResult.Failure -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _uiEvent.emit(ProfileUiEvent.ShowToast(result.message))
                }
            }
        }
    }


    private fun validateAge(rawInput: String): ValidationResult {
        val formatted = rawInput.trim()
        val number = formatted.toIntOrNull()
        val error = when {
            formatted.isEmpty() -> "Введите возраст"
            number == null -> "Некорректное значение"
            number < 14 -> "Пользователь должен быть старше 14 лет"
            number > 120 -> "Возраст не может быть больше 120"
            else -> null
        }
        return ValidationResult(formatted, error)
    }

}