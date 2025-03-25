package ru.point.auth.ui.profile_create.first_step

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.point.auth.R
import ru.point.auth.databinding.FragmentCreateProfileFirstStepBinding
import ru.point.auth.ui.on_boarding.ui.OnboardingStep
import ru.point.auth.ui.on_boarding.ui.OnboardingViewModel
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment

class CreateProfileFirstStepFragment : BaseFragment<FragmentCreateProfileFirstStepBinding>(),
    OnboardingStep {

    // Общий ViewModel для обмена данными онбординга
    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    // ViewModel для первого шага
    private val viewModel: CreateProfileFirstStepViewModel by activityViewModels {
        CreateProfileFirstStepViewModelFactory(
            profileValidationUseCase = ProfileValidationUseCase()
        )
    }


    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateProfileFirstStepBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        val genders = listOf("Мужской", "Женский")
        val adapter = ArrayAdapter(requireContext(), R.layout.gender_spinner_dropdown_item, genders)
        val autoCompleteTextView =
            binding.genderMenu.findViewById<AutoCompleteTextView>(R.id.gender_textview)
        autoCompleteTextView.setAdapter(adapter)

        // Передаем текст для роста во ViewModel
        binding.heightEdittext.doAfterTextChanged { text ->
            viewModel.onHeightChanged(text.toString())
        }

        binding.heightEdittext.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus){
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.processHeight()
                        }
                    }

                }
            }
        // Передаем текст для веса во ViewModel
        binding.weightEdittext.doAfterTextChanged { text ->
            viewModel.onWeightChanged(text.toString())
        }
        binding.weightEdittext.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus){
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.processWeight()
                        }
                    }

                }
            }

        // Передаем текст для возраста во ViewModel
        binding.ageEdittext.doAfterTextChanged { text ->
            viewModel.onAgeChanged(text.toString())
        }


        // Подписка на состояние ViewModel для обновления UI (ошибки, форматирование)
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                if (binding.heightEdittext.text.toString() != state.height) {
                    binding.heightEdittext.setText(state.height)
                    binding.heightEdittext.setSelection(state.height.length)
                }
                if (binding.weightEdittext.text.toString() != state.weight) {
                    binding.weightEdittext.setText(state.weight)
                    binding.weightEdittext.setSelection(state.weight.length)
                }
                binding.heightErrorTextview.text = state.heightError ?: ""
                binding.heightErrorTextview.isVisible = state.heightError != null

                binding.weightErrorTextview.text = state.weightError ?: ""
                binding.weightErrorTextview.isVisible = state.weightError != null

                binding.ageErrorTextview.text = state.ageError ?: ""
                binding.ageErrorTextview.isVisible = state.ageError != null
            }
        }
    }

    override fun saveData() {

        val firstName = binding.nameEdittext.text.toString().trim()
        val lastName = binding.secondNameEdittext.text.toString().trim()
        val heightText = binding.heightEdittext.text.toString().trim()
        val weightText = binding.weightEdittext.text.toString().trim()
        val ageText = binding.ageEdittext.text.toString().trim()
        val genderText =
            binding.genderMenu.findViewById<AutoCompleteTextView>(R.id.gender_textview).text.toString()
                .trim()
        // Проверяем, что поля не пусты
        val fieldsNotEmpty = firstName.isNotEmpty() &&
                lastName.isNotEmpty() &&
                heightText.isNotEmpty() &&
                weightText.isNotEmpty() &&
                ageText.isNotEmpty() &&
                genderText.isNotEmpty()

        // Проверяем, что локальные ошибки отсутствуют (например, ошибки форматирования, диапазона)
        val errorsAbsent = viewModel.state.value.heightError == null &&
                viewModel.state.value.weightError == null &&
                viewModel.state.value.ageError == null

        if (fieldsNotEmpty && errorsAbsent) {
            // Если все поля заполнены, преобразуем в нужные типы (предполагается, что toFloatOrNull() уже даст корректное значение, т.к. ошибки отсутствуют)
            val height = heightText.toFloat()
            val weight = weightText.toFloat()
            val age = ageText.toInt()
            // gender можно интерпретировать, например, как булево значение
            val gender = (genderText == "Мужской")
            // Обновляем общий ViewModel – флаг, что можно перейти, и сохраняем персональные данные
            onboardingViewModel.updateCanGo(true)
            onboardingViewModel.updatePersonalInfo(firstName, lastName, height, weight, age, gender)
        } else {
            onboardingViewModel.updateCanGo(false)
        }

    }

    companion object {
        fun getNewInstance() = CreateProfileFirstStepFragment()
    }
}