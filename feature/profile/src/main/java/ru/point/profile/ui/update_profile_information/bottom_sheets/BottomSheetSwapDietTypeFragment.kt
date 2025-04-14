package ru.point.profile.ui.update_profile_information.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.point.profile.R
import ru.point.profile.databinding.FragmentBottomSheetSwapDietTypeBinding
import worker8.com.github.radiogroupplus.RadioGroupPlus

class BottomSheetSwapDietTypeFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetSwapDietTypeBinding? = null
    private val binding get() = _binding!!

    private lateinit var titles: List<String>
    private lateinit var descriptions: List<String>
    private lateinit var pfcs: List<String>

    companion object {
        private const val ARG_DEFAULT_DIET_GOAL = "defaultDietGoal"

        fun newInstance(defaultActivityLevel: String): BottomSheetSwapDietTypeFragment {
            return BottomSheetSwapDietTypeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DEFAULT_DIET_GOAL, defaultActivityLevel)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetSwapDietTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titles = listOf(
            getString(R.string.goal_title_loss),
            getString(R.string.goal_title_gain),
            getString(R.string.goal_title_maintenance)
        )

        descriptions = listOf(
            getString(R.string.goal_description_loss),
            getString(R.string.goal_description_gain),
            getString(R.string.goal_description_maintenance),
        )

        pfcs = listOf(
            getString(R.string.goal_PFC_loss),
            getString(R.string.goal_PFC_gain),
            getString(R.string.goal_PFC_maintenance)
        )

        val radioGroup: RadioGroupPlus = binding.radioGroupGoal

        val constraintLayout = radioGroup.getChildAt(0) as ConstraintLayout

        val includes = listOf(
            constraintLayout.findViewById<View>(R.id.item_loss),
            constraintLayout.findViewById(R.id.item_gain),
            constraintLayout.findViewById(R.id.item_maintenance),
        )

        for (i in includes.indices) {
            val itemView = includes[i]
            val title = itemView.findViewById<TextView>(R.id.goalTitle)
            val description = itemView.findViewById<TextView>(R.id.goalDescription)
            val pfc = itemView.findViewById<TextView>(R.id.goalPfc_text)

            title.text = titles[i]
            description.text = descriptions[i]
            pfc.text = pfcs[i]
        }

        arguments?.getString(ARG_DEFAULT_DIET_GOAL)?.let { defaultDietGoal ->
            val radioButtonId = when (defaultDietGoal) {
                "LOSS" -> R.id.loss_radio_button
                "GAIN" -> R.id.gain_radio_button
                "MAINTENANCE" -> R.id.maintenance_radio_button
                else -> -1
            }
            if (radioButtonId != -1) {
                binding.radioGroupGoal.check(radioButtonId)
            }
        }

        // Обработка нажатия на кнопку "Сохранить"
        binding.saveNewDietDataButton.setOnClickListener {
            // Получаем выбранный id радио кнопки
            val selectedId = binding.radioGroupGoal.checkedRadioButtonId

            // Сопоставляем id радио кнопок со значениями уровня активности
            val selectedActivityLevel = when (selectedId) {
                R.id.loss_radio_button -> "LOSS"
                R.id.gain_radio_button -> "GAIN"
                R.id.maintenance_radio_button -> "MAINTENANCE"
                else -> ""
            }

            // Передаем выбранное значение родительскому фрагменту через FragmentResult API
            parentFragmentManager.setFragmentResult("dietGoalKey", Bundle().apply {
                putString("dietGoal", selectedActivityLevel)
            })
            dismiss()
        }

        // Обработка нажатия на кнопку "Отменить"
        binding.cancelNewDietDataButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dlg ->
            // Получаем ссылку на контейнер нижнего листа по идентификатору из библиотеки Material
            val bottomSheet = dlg.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                sheet.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                // Получаем объект поведения и переводим его в состояние развернутого отображения
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}