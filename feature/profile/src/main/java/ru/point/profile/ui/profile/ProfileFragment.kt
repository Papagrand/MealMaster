package ru.point.profile.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import ru.point.api.profile_data.domain.models.ProfileMainDataModel
import ru.point.core.navigation.BottomBarManager
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core.ui.BaseFragment
import ru.point.profile.databinding.FragmentProfileBinding
import ru.point.profile.di.DaggerProfileComponent
import ru.point.profile.di.ProfileDepsProvider
import ru.point.profile.R
import ru.point.profile.ui.transformations.CircleWithBorderTransformation
import javax.inject.Inject

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModelFactory

    private val viewModel: ProfileViewModel by activityViewModels {
        profileViewModelFactory
    }

    override fun onAttach(context: Context) {
        DaggerProfileComponent.builder()
            .deps(ProfileDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).show()

        val userProfileId = SecurePrefs.getUserId().toString()
        collectUiState()
        collectUiEvent()
        viewModel.getProfileData(userProfileId)

        binding.editDataItem.setOnClickListener{
            viewModel.goToUpdateProfileInformation()
        }
    }

    private fun collectUiState() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.profileScrollView.isVisible = !state.isLoading
                state.profileData?.let { profile ->
                    val profilePictureString = profile.profilePicture

                    // Например, получаем borderWidth в пикселях из ресурсов
                    val borderWidthDp = 5.0F
                    val typedValue = TypedValue()
                    context?.theme?.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true)
                    val borderColor = typedValue.data

                    if (profilePictureString.startsWith("data:image", ignoreCase = true)) {
                        val base64Part = profilePictureString.substringAfter("base64,")
                        val decodedBytes = android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
                        binding.profilePictureImage.load(decodedBytes) {
                            crossfade(true)
                            allowHardware(false)
                            transformations(CircleWithBorderTransformation(borderWidthDp, borderColor))
                        }
                    } else {
                        binding.profilePictureImage.load(profilePictureString) {
                            crossfade(true)
                            allowHardware(false)
                            transformations(CircleWithBorderTransformation(borderWidthDp, borderColor))
                        }
                    }


                    binding.userIdTextView.text = profile.userProfileId

                    binding.firstLastNameTextView.text = "${profile.name} ${profile.secName}"

                    val dietGoal = when(profile.goal){
                        "LOSS" -> "Похудение"
                        "GAIN" -> "Набор Веса"
                        "MAINTENANCE" -> "Поддержание веса"
                        else -> "Похудение"
                    }
                    binding.dietGoalTextView.text = "${getString(R.string.goalDiet)} ${dietGoal}"

                    val activityLevel = when(profile.activityLevel){
                        "VERY_LOW" -> "Очень низкий"
                        "LOW" -> "Низкий"
                        "MEDIUM" -> "Средний"
                        "HIGH" -> "Высокий"
                        "VERY_HIGH" -> "Очень высокий"
                        else -> "Средний"
                    }
                    binding.activityLevelTextView.text = "${getString(R.string.activityLevel)} ${activityLevel}"

                    binding.profileWeightEditTextLayout.editText?.setText(
                        profile.weight.toString()
                    )

                    binding.profileHeightEditTextLayout.editText?.setText(
                        profile.height.toString()
                    )

                    binding.dateStartDietTextView.text = "${getString(R.string.diet_start_date)} ${profile.goalTimeStart.substringBefore("T")}"
                    binding.dateEndDietTextView.text = "${getString(R.string.diet_end_date)} ${profile.goalTimeEnd.substringBefore("T")}"

                }
            }
        }

    }

    private fun collectUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is ProfileUiEvent.ShowToast -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }

                    ProfileUiEvent.NavigateToUpdateProfileInformationFragment -> {
                        // Получаем данные профиля из uiState ViewModel
                        val profileData = viewModel.uiState.value.profileData
                        if (profileData != null) {
                            // Сериализуем профиль в JSON
                            val profileJson = Json.encodeToString(ProfileMainDataModel.serializer(), profileData)
                            // Создаём Bundle и кладём JSON-строку
                            val bundle = Bundle().apply {
                                putString("profileData", profileJson)
                            }
                            // Переход с передачей аргументов
                            navigator.fromProfileFragmentToUpdateProfileInformationFragment(bundle)
                        } else {
                            Toast.makeText(requireContext(), "Профиль не найден", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


}