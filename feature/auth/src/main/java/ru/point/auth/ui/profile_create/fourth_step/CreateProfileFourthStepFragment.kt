package ru.point.auth.ui.profile_create.fourth_step

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.point.auth.databinding.FragmentCreateProfileFourthStepBinding
import ru.point.auth.ui.on_boarding.ui.OnboardingStep
import ru.point.auth.ui.on_boarding.ui.OnboardingViewModel
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import java.util.Calendar

class CreateProfileFourthStepFragment : BaseFragment<FragmentCreateProfileFourthStepBinding>(),
    OnboardingStep {

    // Общий ViewModel для онбординга (сбор данных со всех шагов)
    private val onboardingViewModel: OnboardingViewModel by activityViewModels()
    // ViewModel для четвертого шага, отвечающая за целевой вес
    private val viewModel: CreateProfileFourthStepViewModel by activityViewModels {
        CreateProfileFourthStepViewModelFactory(
            validateTargetWeightUseCase = ValidateTargetWeightUseCase()
        )
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateProfileFourthStepBinding =
        FragmentCreateProfileFourthStepBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        // Обработка ввода целевого веса
        binding.goalWeightEdittext.doAfterTextChanged { text ->
            viewModel.onTargetWeightChanged(text.toString())
        }

        binding.goalWeightEdittext.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus){
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.validateAndFormatTargetWeight()
                        }
                    }

                }
            }

        // Подписка на состояние ViewModel для обновления UI
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                // Обновляем EditText, если значение изменилось
                if (binding.goalWeightEdittext.text.toString() != state.targetWeight) {
                    binding.goalWeightEdittext.setText(state.targetWeight)
                    binding.goalWeightEdittext.setSelection(state.targetWeight.length)
                }
                // Отображаем сообщение об ошибке, если оно есть
                binding.goalWeightErrorTextview.text = state.targetWeightError ?: ""
                binding.goalWeightErrorTextview.isVisible =
                    if (state.targetWeightError != null) true else false
            }
        }

        // Обработка выбора даты окончания диеты
        binding.endDietTextview.setOnClickListener {
            binding.goalWeightEdittext.clearFocus()
            hideKeyboard()
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.endDietTextview.text = formattedDate
            }, year, month, day)
            datePickerDialog.show()
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun saveData() {
        val targetWeightText = binding.goalWeightEdittext.text.toString().trim()
        val dietEndDate = binding.endDietTextview.text.toString().trim()

        val fieldsNotEmpty = targetWeightText.isNotEmpty() && dietEndDate.isNotEmpty()
        if (fieldsNotEmpty && viewModel.state.value.targetWeightError == null){
            val targetWeight = targetWeightText.toFloat()
            onboardingViewModel.updateCanGo(true)
            onboardingViewModel.updateDietInfo(targetWeight, dietEndDate)
        }else{
            onboardingViewModel.updateCanGo(false)
        }
    }

    companion object {
        fun getNewInstance() = CreateProfileFourthStepFragment()
    }
}