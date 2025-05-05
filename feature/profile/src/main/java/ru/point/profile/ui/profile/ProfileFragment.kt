package ru.point.profile.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import ru.point.api.profile_data.domain.models.ProfileMainDataModel
import ru.point.core.navigation.BottomBarManager
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core.secure_prefs.ThemeMode
import ru.point.core.secure_prefs.toNightMode
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

    private lateinit var currentMode: ThemeMode

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


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).show()

        val userProfileId = SecurePrefs.getUserId().toString()
        collectUiState()
        collectUiEvent()
        viewModel.getProfileData(userProfileId)

        binding.editDataItem.setOnClickListener {
            viewModel.goToUpdateProfileInformation()
        }

        val currentMode = SecurePrefs.getTheme()
        val isDarkTheme = when (currentMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM -> {
                val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                nightModeFlags == Configuration.UI_MODE_NIGHT_YES
            }
        }

        binding.themeSwitch.isChecked = isDarkTheme

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val newMode = if (isChecked) ThemeMode.DARK else ThemeMode.LIGHT
            SecurePrefs.saveTheme(newMode)
            AppCompatDelegate.setDefaultNightMode(newMode.toNightMode())
            requireActivity().recreate()
        }

        val deviceId =
            Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
        val userId = SecurePrefs.getUserId()!!
        binding.buttonExit.setOnClickListener {
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.MyAlertDialogStyle
            )
                .setMessage("Вы уверены, что хотите выйти?")
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Выйти") { dialog, _ ->
                    viewModel.logoutUser(userId, deviceId)
                    SecurePrefs.removeUserId()
                    dialog.dismiss()
                }
                .show()
        }

        val edit = binding.profileWeightEditTextLayout.editText
        edit?.doAfterTextChanged { text ->
            if (edit.hasFocus()) {
                viewModel.onWeightChanged(text.toString())
                binding.cancelEditWeight.visibility = View.VISIBLE
                binding.changeEditWeight.visibility = View.VISIBLE
            }
        }

        binding.profileWeightEditTextLayout.editText?.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.processWeight()
                        }
                    }

                }
            }

        binding.cancelEditWeight.setOnClickListener {
            binding.root.clearFocus()
            binding.cancelEditWeight.visibility = View.INVISIBLE
            binding.changeEditWeight.visibility = View.INVISIBLE
            viewModel.backToDefault()
        }

        binding.changeEditWeight.setOnClickListener {
            binding.root.clearFocus()
            binding.cancelEditWeight.visibility = View.INVISIBLE
            binding.changeEditWeight.visibility = View.INVISIBLE
            viewModel.updateWeight(userProfileId)
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
                    context?.theme?.resolveAttribute(
                        com.google.android.material.R.attr.colorSecondary,
                        typedValue,
                        true
                    )
                    val borderColor = typedValue.data

                    if (profilePictureString.startsWith("data:image", ignoreCase = true)) {
                        val base64Part = profilePictureString.substringAfter("base64,")
                        val decodedBytes =
                            android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
                        binding.profilePictureImage.load(decodedBytes) {
                            crossfade(true)
                            allowHardware(false)
                            transformations(
                                CircleWithBorderTransformation(
                                    borderWidthDp,
                                    borderColor
                                )
                            )
                        }
                    } else {
                        binding.profilePictureImage.load(profilePictureString) {
                            crossfade(true)
                            allowHardware(false)
                            transformations(
                                CircleWithBorderTransformation(
                                    borderWidthDp,
                                    borderColor
                                )
                            )
                        }
                    }


                    binding.userIdTextView.text = profile.userProfileId

                    binding.firstLastNameTextView.text = "${profile.name} ${profile.secName}"

                    val dietGoal = when (profile.goal) {
                        "LOSS" -> "Похудение"
                        "GAIN" -> "Набор Веса"
                        "MAINTENANCE" -> "Поддержание веса"
                        else -> "Похудение"
                    }
                    binding.dietGoalTextView.text = "${getString(R.string.goalDiet)} ${dietGoal}"

                    val activityLevel = when (profile.activityLevel) {
                        "VERY_LOW" -> "Очень низкий"
                        "LOW" -> "Низкий"
                        "MEDIUM" -> "Средний"
                        "HIGH" -> "Высокий"
                        "VERY_HIGH" -> "Очень высокий"
                        else -> "Средний"
                    }
                    binding.activityLevelTextView.text =
                        "${getString(R.string.activityLevel)} ${activityLevel}"

                    binding.profileWeightEditTextLayout.editText?.setText(
                        profile.weight.toString()
                    )

                    binding.dateStartDietTextView.text = "${getString(R.string.diet_start_date)} ${
                        profile.goalTimeStart.substringBefore("T")
                    }"
                    binding.dateEndDietTextView.text = "${getString(R.string.diet_end_date)} ${
                        profile.goalTimeEnd.substringBefore("T")
                    }"

                }
            }
        }

    }

    private fun collectUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is ProfileUiEvent.ShowToast -> {
                        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
                    }

                    ProfileUiEvent.NavigateToUpdateProfileInformationFragment -> {
                        val profileData = viewModel.uiState.value.profileData
                        if (profileData != null) {
                            val profileJson =
                                Json.encodeToString(ProfileMainDataModel.serializer(), profileData)
                            val bundle = Bundle().apply {
                                putString("profileData", profileJson)
                            }
                            navigator.fromProfileFragmentToUpdateProfileInformationFragment(bundle)
                        } else {
                            Snackbar.make(binding.root, "Профиль не найден", Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }

                    ProfileUiEvent.NavigateToLoginFragment -> {
                        navigator.fromProfileFragmentToLoginFragment()
                    }
                }
            }
        }
    }


}