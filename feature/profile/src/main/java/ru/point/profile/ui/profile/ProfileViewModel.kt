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
import ru.point.api.profile_data.domain.models.ProfileMainDataModel
import ru.point.core.LogoutHandler
import ru.point.profile.domain.GetProfileMainDataUseCase
import ru.point.profile.domain.LogoutUserUseCase

data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val profileData: ProfileMainDataModel? = null
)

sealed class ProfileUiEvent {
    data class ShowToast(val message: String) : ProfileUiEvent()
    data object NavigateToUpdateProfileInformationFragment : ProfileUiEvent()
    data object NavigateToLoginFragment : ProfileUiEvent()
}

class ProfileViewModel (
    private val getProfileMainDataUseCase: GetProfileMainDataUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val logoutHandlers: Set<@JvmSuppressWildcards LogoutHandler>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent: SharedFlow<ProfileUiEvent> = _uiEvent.asSharedFlow()

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

    fun goToUpdateProfileInformation(){
        viewModelScope.launch {
            _uiEvent.emit(ProfileUiEvent.NavigateToUpdateProfileInformationFragment)
        }
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

}