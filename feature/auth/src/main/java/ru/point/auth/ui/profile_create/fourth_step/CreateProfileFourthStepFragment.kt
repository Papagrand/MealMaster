package ru.point.auth.ui.profile_create.fourth_step

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import ru.point.auth.R
import ru.point.auth.databinding.FragmentCreateProfileFourthStepBinding
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import java.util.Calendar

class CreateProfileFourthStepFragment : BaseFragment<FragmentCreateProfileFourthStepBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateProfileFourthStepBinding =
        FragmentCreateProfileFourthStepBinding.inflate(inflater,container, false)

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (requireActivity() as BottomBarManager).hide()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // При клике на TextView открываем диалог выбора веса
        binding.endDietTextview.setOnClickListener {
            // Получаем текущую дату для инициализации диалога
            binding.goalWeightEdittext.clearFocus()
            hideKeyboard()
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Создаем DatePickerDialog
            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Форматируем дату: месяц в DatePickerDialog начинается с 0, поэтому добавляем 1
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.endDietTextview.text = formattedDate
            }, year, month, day)
            // Показываем диалог
            datePickerDialog.show()
        }

        binding.goalWeightEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    val originalText = it.toString()
                    var modifiedText = originalText

                    val dotIndex = modifiedText.indexOf('.')
                    if (dotIndex != -1) {
                        val decimalPart = modifiedText.substring(dotIndex + 1)
                        if (decimalPart.length > 1) {
                            // Оставляем только один символ после точки
                            modifiedText = modifiedText.substring(0, dotIndex + 2)
                        }
                    }

                    // Если текст изменился, обновляем EditText, чтобы избежать рекурсии:
                    if (modifiedText != originalText) {
                        binding.goalWeightEdittext.removeTextChangedListener(this)
                        binding.goalWeightEdittext.setText(modifiedText)
                        // Устанавливаем курсор в конец
                        binding.goalWeightEdittext.setSelection(modifiedText.length)
                        binding.goalWeightEdittext.addTextChangedListener(this)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    // Функция для скрытия клавиатуры
    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}