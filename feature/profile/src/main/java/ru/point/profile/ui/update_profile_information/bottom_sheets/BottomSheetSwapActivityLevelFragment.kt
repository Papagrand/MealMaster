package ru.point.profile.ui.update_profile_information.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.point.profile.R
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.point.profile.databinding.FragmentBottomSheetSwapActivityLevelBinding
import worker8.com.github.radiogroupplus.RadioGroupPlus

class BottomSheetSwapActivityLevelFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetSwapActivityLevelBinding? = null
    private val binding get() = _binding!!

    private lateinit var titles: List<String>
    private lateinit var descriptions: List<String>

    private val icons = listOf(
        R.drawable.very_low_activity,
        R.drawable.low_activity,
        R.drawable.medium_activity,
        R.drawable.high_activity,
        R.drawable.very_high_activity
    )

    companion object {
        private const val ARG_DEFAULT_ACTIVITY_LEVEL = "defaultActivityLevel"

        fun newInstance(defaultActivityLevel: String): BottomSheetSwapActivityLevelFragment {
            return BottomSheetSwapActivityLevelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DEFAULT_ACTIVITY_LEVEL, defaultActivityLevel)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetSwapActivityLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titles = listOf(
            getString(R.string.activity_title_lowest),
            getString(R.string.activity_title_low),
            getString(R.string.activity_title_medium),
            getString(R.string.activity_title_high),
            getString(R.string.activity_title_highest)
        )

        descriptions = listOf(
            getString(R.string.activity_description_lowest),
            getString(R.string.activity_description_low),
            getString(R.string.activity_description_medium),
            getString(R.string.activity_description_high),
            getString(R.string.activity_description_highest)
        )

        val radioGroup: RadioGroupPlus = binding.radioChangeActivityGroup

        val containerLayout = radioGroup.getChildAt(0) as ConstraintLayout

        val includes = listOf(
            containerLayout.findViewById<View>(R.id.item_lowest),
            containerLayout.findViewById(R.id.item_low),
            containerLayout.findViewById(R.id.item_medium),
            containerLayout.findViewById(R.id.item_high),
            containerLayout.findViewById(R.id.item_very_high)
        )

        for (i in includes.indices) {
            val itemView = includes[i]
            val title = itemView.findViewById<TextView>(R.id.activity_title)
            val description = itemView.findViewById<TextView>(R.id.activity_description)
            val icon = itemView.findViewById<ImageView>(R.id.activity_icon)

            title.text = titles[i]
            description.text = descriptions[i]
            icon.setImageResource(icons[i])
        }

        arguments?.getString(ARG_DEFAULT_ACTIVITY_LEVEL)?.let { defaultActivityLevel ->
            val radioButtonId = when (defaultActivityLevel) {
                "VERY_LOW" -> R.id.very_low_radio_button
                "LOW" -> R.id.low_radio_button
                "MEDIUM" -> R.id.medium_radio_button
                "HIGH" -> R.id.high_radio_button
                "VERY_HIGH" -> R.id.very_high_radio_button
                else -> -1
            }
            if (radioButtonId != -1) {
                binding.radioChangeActivityGroup.check(radioButtonId)
            }
        }

        // Обработка нажатия на кнопку "Сохранить"
        binding.saveNewActivityDataButton.setOnClickListener {
            // Получаем выбранный id радио кнопки
            val selectedId = binding.radioChangeActivityGroup.checkedRadioButtonId

            // Сопоставляем id радио кнопок со значениями уровня активности
            val selectedActivityLevel = when (selectedId) {
                R.id.very_low_radio_button -> "VERY_LOW"
                R.id.low_radio_button -> "LOW"
                R.id.medium_radio_button -> "MEDIUM"
                R.id.high_radio_button -> "HIGH"
                R.id.very_high_radio_button -> "VERY_HIGH"
                else -> ""
            }

            // Передаем выбранное значение родительскому фрагменту через FragmentResult API
            parentFragmentManager.setFragmentResult("activityLevelKey", Bundle().apply {
                putString("activityLevel", selectedActivityLevel)
            })
            dismiss()
        }

        // Обработка нажатия на кнопку "Отменить"
        binding.cancelNewActivityDataButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dlg ->
            // Получаем контейнер нижнего листа
            val bottomSheet = dlg.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                sheet.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                // Разворачиваем нижний лист
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

