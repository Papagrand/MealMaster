package ru.point.meal.ui.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.point.core.enums.SortCaloriesType
import ru.point.meal.databinding.FragmentSettingSearchProductsBottomSheetBinding
import ru.point.meal.ui.SearchSettings

class SearchProductsSettingsBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "SearchSettingsBottomSheet"
        private const val ARG_IS_VEGAN = "arg_is_vegan"
        private const val ARG_SORT_CALORIES = "arg_sort_calories"
        const val RESULT_KEY = "searchSettingsResult"

        fun newInstance(current: SearchSettings): SearchProductsSettingsBottomSheetFragment {
            return SearchProductsSettingsBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_VEGAN, current.isVegan == true)
                    putString(ARG_SORT_CALORIES, current.sortCalories)
                }
            }
        }
    }

    private var _binding: FragmentSettingSearchProductsBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentSettingSearchProductsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedVegan = arguments?.getBoolean(ARG_IS_VEGAN) == true
        val receivedSort = arguments?.getString(ARG_SORT_CALORIES) ?: SortCaloriesType.OFF.type

        binding.onlyVeganCheckBox.isChecked = receivedVegan
        when (receivedSort) {
            SortCaloriesType.ASC.type -> binding.radioButtonAscending.isChecked = true
            SortCaloriesType.DESC.type -> binding.radioButtonDescending.isChecked = true
            else -> binding.radioButtonOffSort.isChecked = true
        }

        binding.buttonBack.setOnClickListener { dismiss() }

        binding.buttonConfirm.setOnClickListener {
            val newVegan = binding.onlyVeganCheckBox.isChecked
            val newSort = when {
                binding.radioButtonAscending.isChecked -> SortCaloriesType.ASC.type
                binding.radioButtonDescending.isChecked -> SortCaloriesType.DESC.type
                else -> SortCaloriesType.OFF.type
            }
            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                bundleOf(
                    ARG_IS_VEGAN to newVegan,
                    ARG_SORT_CALORIES to newSort
                )
            )
            dismiss()
        }
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