package ru.point.recipes.ui.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.point.core.enums.CookingTime
import ru.point.core.enums.DifficultyLevel
import ru.point.recipes.databinding.FragmentBottomSheetRecipeSearchSettingsBinding
import ru.point.recipes.ui.SearchRecipeSettings

class RecipeSearchSettingBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "RecipeSearchSettingsBottomSheet"
        private const val ARG_COOKING_TIME = "arg_cooking_time"
        private const val ARG_DIFFICULTY = "arg_difficulty"
        private const val ARG_IS_VEGAN = "arg_is_vegan"
        private const val ARG_MAX_CALORIES = "arg_max_calories"
        const val RESULT_KEY = "recipeSearchSettingsResult"

        fun newInstance(current: SearchRecipeSettings): RecipeSearchSettingBottomSheetFragment {
            return RecipeSearchSettingBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COOKING_TIME, current.cookingTime ?: CookingTime.ANY.type)
                    putInt(ARG_DIFFICULTY, current.difficulty ?: DifficultyLevel.ANY.type)
                    putBoolean(ARG_IS_VEGAN, current.isVegan == true)
                    putDouble(ARG_MAX_CALORIES, current.maxCalories)
                }
            }
        }
    }

    private var _binding: FragmentBottomSheetRecipeSearchSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentBottomSheetRecipeSearchSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedCookingTime = arguments?.getInt(ARG_COOKING_TIME) ?: CookingTime.ANY.type
        val receivedDifficulty = arguments?.getInt(ARG_DIFFICULTY) ?: DifficultyLevel.ANY.type
        val receivedMaxCalories = arguments?.getDouble(ARG_MAX_CALORIES) ?: 1000.0
        val receivedVegan = arguments?.getBoolean(ARG_IS_VEGAN) ?: false

        when (receivedCookingTime) {
            CookingTime.UNDER15.type -> binding.under15MinutesRadioButton.isChecked = true
            CookingTime.UNDER30.type -> binding.under30MinutesRadioButton.isChecked = true
            CookingTime.UNDER60.type -> binding.under60MinutesRadioButton.isChecked = true
            else -> binding.anyMinutesRadioButton.isChecked = true
        }

        when (receivedDifficulty) {
            DifficultyLevel.UNDER_FIRST.type -> binding.firstLevelDifficultyRadioButton.isChecked =
                true

            DifficultyLevel.UNDER_SECOND.type -> binding.secondLevelDifficultyRadioButton.isChecked =
                true

            DifficultyLevel.UNDER_THIRD.type -> binding.thirdLevelDifficultyRadioButton.isChecked =
                true

            DifficultyLevel.UNDER_FOURTH.type -> binding.fourthLevelDifficultyRadioButton.isChecked =
                true

            DifficultyLevel.UNDER_FIFTH.type -> binding.fifthLevelDifficultyRadioButton.isChecked =
                true

            else -> binding.anyLevelDifficultyRadioButton.isChecked = true
        }

        binding.isVeganYesRadioButton.isChecked = receivedVegan
        binding.isVeganNoRadioButton.isChecked = !receivedVegan

        binding.caloriesSlider.value = receivedMaxCalories.toFloat()
        binding.servingSizeEditTextLayout.editText?.setText(receivedMaxCalories.toString())

        binding.caloriesSlider.addOnChangeListener { _, value, _ ->
            val text = value.toInt().toString()
            if (binding.servingSizeEditTextLayout.editText?.text?.toString() != text) {
                binding.servingSizeEditTextLayout.editText?.setText(text)
            }
        }

        binding.servingSizeEditTextLayout.editText?.doOnTextChanged { text, _, _, _ ->
            val newVal = text.toString().toFloatOrNull() ?: return@doOnTextChanged

            // ограничим ввод диапазоном самого Slider’а
            val clamped = newVal.coerceIn(binding.caloriesSlider.valueFrom, binding.caloriesSlider.valueTo)

            // обновляем, только если значение реально изменилось
            if (binding.caloriesSlider.value != clamped) {
                binding.caloriesSlider.value = clamped
            }
        }

        // ---------- кнопка «Применить» ----------
        binding.buttonConfirm.setOnClickListener {
            val newCooking = when {
                binding.under15MinutesRadioButton.isChecked -> CookingTime.UNDER15.type
                binding.under30MinutesRadioButton.isChecked -> CookingTime.UNDER30.type
                binding.under60MinutesRadioButton.isChecked -> CookingTime.UNDER60.type
                binding.anyMinutesRadioButton.isChecked -> CookingTime.ANY.type
                else -> CookingTime.ANY.type
            }
            val newDifficulty = when {
                binding.firstLevelDifficultyRadioButton.isChecked -> DifficultyLevel.UNDER_FIRST.type
                binding.secondLevelDifficultyRadioButton.isChecked -> DifficultyLevel.UNDER_SECOND.type
                binding.thirdLevelDifficultyRadioButton.isChecked -> DifficultyLevel.UNDER_THIRD.type
                binding.fourthLevelDifficultyRadioButton.isChecked -> DifficultyLevel.UNDER_FOURTH.type
                binding.fifthLevelDifficultyRadioButton.isChecked -> DifficultyLevel.UNDER_FIFTH.type
                binding.anyLevelDifficultyRadioButton.isChecked -> DifficultyLevel.ANY.type
                else -> DifficultyLevel.ANY.type
            }
            val newVegan = binding.isVeganYesRadioButton.isChecked
            val newMaxCal =
                binding.servingSizeEditTextLayout.editText?.text?.toString()?.toDoubleOrNull() ?: 1000.0

            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                bundleOf(
                    ARG_COOKING_TIME to newCooking,
                    ARG_DIFFICULTY to newDifficulty,
                    ARG_IS_VEGAN to newVegan,
                    ARG_MAX_CALORIES to newMaxCal
                )
            )
            dismiss()
        }

        binding.buttonBack.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dlg ->
            val bottomSheet =
                dlg.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                sheet.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                BottomSheetBehavior.from(sheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}