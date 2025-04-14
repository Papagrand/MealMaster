package ru.point.profile.ui.update_profile_information

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.point.api.profile_data.domain.models.ProfileMainDataModel
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import ru.point.profile.R
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.profile.databinding.FragmentUpdateProfileInformationBinding
import ru.point.profile.di.DaggerUpdateProfileComponent
import ru.point.profile.di.UpdateProfileDepsProvider
import ru.point.profile.ui.update_profile_information.bottom_sheets.BottomSheetSwapActivityLevelFragment
import ru.point.profile.ui.update_profile_information.bottom_sheets.BottomSheetSwapDietTypeFragment
import java.util.Calendar


class UpdateProfileInformationFragment : BaseFragment<FragmentUpdateProfileInformationBinding>() {

    // TODO: Добавить обработку ошибок, исправить разметку для соответствующих полей (только числа, только символы и тд)

    @Inject
    lateinit var updateProfileInformationViewModelFactory: UpdateProfileInformationViewModelFactory

    private val viewModel: UpdateProfileInformationViewModel by viewModels{
        updateProfileInformationViewModelFactory
    }

    override fun onAttach(context: Context) {
        DaggerUpdateProfileComponent.builder()
            .deps(UpdateProfileDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    private lateinit var activityTitles: List<String>
    private lateinit var activityDescriptions: List<String>
    private lateinit var goalTitles: List<String>
    private lateinit var goalDescriptions: List<String>
    private lateinit var goalPfcs: List<String>

    private val activityIcons = listOf(
        R.drawable.very_low_activity,
        R.drawable.low_activity,
        R.drawable.medium_activity,
        R.drawable.high_activity,
        R.drawable.very_high_activity
    )

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUpdateProfileInformationBinding = FragmentUpdateProfileInformationBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        childFragmentManager.setFragmentResultListener("activityLevelKey", viewLifecycleOwner) { _, bundle ->
            val activityLevel = bundle.getString("activityLevel") ?: ""
            viewModel.onActivityLevelChanged(activityLevel)
        }

        binding.ItemActivityCL.setOnClickListener {
            val currentActivityLevel = viewModel.state.value.activityLevel
            val bottomSheet = BottomSheetSwapActivityLevelFragment.newInstance(currentActivityLevel)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        childFragmentManager.setFragmentResultListener("dietGoalKey", viewLifecycleOwner) { _, bundle ->
            val dietGoal = bundle.getString("dietGoal") ?: ""
            viewModel.onDietGoalChanged(dietGoal)
        }

        binding.ItemGoalCL.setOnClickListener {
            val currentDietGoal = viewModel.state.value.goal
            val bottomSheet = BottomSheetSwapDietTypeFragment.newInstance(currentDietGoal)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        activityTitles = listOf(
            getString(R.string.activity_title_lowest),
            getString(R.string.activity_title_low),
            getString(R.string.activity_title_medium),
            getString(R.string.activity_title_high),
            getString(R.string.activity_title_highest)
        )

        activityDescriptions = listOf(
            getString(R.string.activity_description_lowest),
            getString(R.string.activity_description_low),
            getString(R.string.activity_description_medium),
            getString(R.string.activity_description_high),
            getString(R.string.activity_description_highest)
        )

        goalTitles = listOf(
            getString(R.string.goal_title_loss),
            getString(R.string.goal_title_gain),
            getString(R.string.goal_title_maintenance)
        )

        goalDescriptions = listOf(
            getString(R.string.goal_description_loss),
            getString(R.string.goal_description_gain),
            getString(R.string.goal_description_maintenance),
        )

        goalPfcs = listOf(
            getString(R.string.goal_PFC_loss),
            getString(R.string.goal_PFC_gain),
            getString(R.string.goal_PFC_maintenance)
        )

        val genders = listOf("Мужской", "Женский")
        val adapter = ArrayAdapter(requireContext(), R.layout.gender_spinner_dropdown_item, genders)
        binding.genderTextview.setAdapter(adapter)

        binding.profileChangeGoalTimeEndEditTextLayout.editText?.setOnClickListener{
            binding.profileChangeGoalWeightEditTextLayout.clearFocus()
            hideKeyboard()

            val minDateCalendar = Calendar.getInstance().apply {
                // Добавляем один месяц к текущей дате
                add(Calendar.MONTH, 1)
                // Добавляем еще один день, чтобы выбор был строго начиная со следующего дня
                add(Calendar.DAY_OF_MONTH, 1)
            }

            // Используем minDateCalendar для начальных значений DatePicker
            val initialYear = minDateCalendar.get(Calendar.YEAR)
            val initialMonth = minDateCalendar.get(Calendar.MONTH)
            val initialDay = minDateCalendar.get(Calendar.DAY_OF_MONTH)

            // Создаем DatePickerDialog с начальными значениями
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Форматируем выбранную дату (учтите, что selectedMonth начинается с 0)
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    binding.profileChangeGoalTimeEndEditTextLayout.editText?.setText(formattedDate)
                },
                initialYear,
                initialMonth,
                initialDay
            )
            datePickerDialog.datePicker.minDate = minDateCalendar.timeInMillis

            datePickerDialog.show()
        }

        val profileJson = requireArguments().getString("profileData")
        if (profileJson != null) {
            // Десериализуем обратно в объект
            val profileData = Json.decodeFromString<ProfileMainDataModel>(profileJson)
            viewModel.initializeState(profileData)
        } else {
            Toast.makeText(requireContext(), "Не удалось получить данные профиля", Toast.LENGTH_SHORT).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                // Обновляем UI-элементы, например:
                binding.profileChangeNameEditTextLayout.editText?.setText(state.name)
                binding.profileChangeSecNameEditTextLayout.editText?.setText(state.secName)
                binding.profileChangeAgeEditTextLayout.editText?.setText(state.age)
                binding.profileChangeGoalWeightEditTextLayout.editText?.setText(state.goalWeight)
                binding.profileChangeGoalTimeEndEditTextLayout.editText?.setText(state.dietEndDate)
                binding.genderTextview.setText(state.gender, false)

                val goalIndex = when(state.goal){
                    "LOSS" -> 0
                    "GAIN" -> 1
                    "MAINTENANCE" -> 2
                    else -> throw IllegalStateException("Unknown goal")
                }
                val goalInclude = binding.currentItemGoal
                goalInclude.goalTitle.text = goalTitles[goalIndex]
                goalInclude.goalDescription.text = goalDescriptions[goalIndex]
                goalInclude.goalPfcText.text = goalPfcs[goalIndex]

                val activityIndex = when(state.activityLevel){
                    "VERY_LOW" -> 0
                    "LOW" -> 1
                    "MEDIUM" -> 2
                    "HIGH" -> 3
                    "VERY_HIGH" -> 4
                    else -> throw IllegalStateException("Unknown activity level")
                }
                val activityInclude = binding.currentItemActivity
                activityInclude.activityTitle.text = activityTitles[activityIndex]
                activityInclude.activityDescription.text = activityDescriptions[activityIndex]
                activityInclude.activityIcon.setImageResource(activityIcons[activityIndex])
            }
        }

        binding.profileChangeNameEditTextLayout.editText?.doAfterTextChanged { text ->
            viewModel.onNameChanged(text.toString())
        }
        binding.profileChangeNameEditTextLayout.editText?.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus){
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.processName()
                        }
                    }

                }
            }

        binding.profileChangeSecNameEditTextLayout.editText?.doAfterTextChanged { text ->
            viewModel.onSecNameChanged(text.toString())
        }

        binding.profileChangeSecNameEditTextLayout.editText?.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus){
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.processSecName()
                        }
                    }

                }
            }

        binding.profileChangeAgeEditTextLayout.editText?.doAfterTextChanged { text ->
            viewModel.onAgeChanged(text.toString())
        }

        binding.profileChangeAgeEditTextLayout.editText?.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus){
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.processAge()
                        }
                    }

                }
            }

        binding.profileChangeGenderTextLayout.editText?.doAfterTextChanged { text ->
            viewModel.onGenderChanged(text.toString())
        }

        binding.profileChangeGoalWeightEditTextLayout.editText?.doAfterTextChanged { text ->
            viewModel.onGoalWeightChanged(text.toString())
        }

        binding.profileChangeGoalWeightEditTextLayout.editText?.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus){
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.processGoalWeight()
                        }
                    }

                }
            }

        binding.profileChangeGoalTimeEndEditTextLayout.editText?.doAfterTextChanged { text ->
            viewModel.onDietEndDateChanged(text.toString())
        }

        binding.saveNewProfileData.setOnClickListener{
            binding.root.clearFocus()
            hideKeyboard()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.processName()
                viewModel.processSecName()
                viewModel.processAge()
                viewModel.processGoalWeight()
            }
            val userProfileId = SecurePrefs.getUserId().toString()
            viewModel.updateAndSaveProfile(userProfileId)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is UpdateProfileUiEvent.NavigateToProfile -> {
                            navigator.fromUpdateProfileInformationFragmentToProfileFragment()
                        }
                        is UpdateProfileUiEvent.ShowSnackBar -> {
                            Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}