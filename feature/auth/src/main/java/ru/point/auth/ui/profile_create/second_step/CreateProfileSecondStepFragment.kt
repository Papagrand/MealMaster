package ru.point.auth.ui.profile_create.second_step

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ru.point.auth.R
import ru.point.auth.databinding.FragmentCreateProfileSecondStepBinding
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import worker8.com.github.radiogroupplus.RadioGroupPlus

class CreateProfileSecondStepFragment : BaseFragment<FragmentCreateProfileSecondStepBinding>() {

    // Инициализация списков будет происходить позже
    private lateinit var titles: List<String>
    private lateinit var descriptions: List<String>

    private val icons = listOf(
        R.drawable.very_low_activity,
        R.drawable.low_activity,
        R.drawable.medium_activity,
        R.drawable.high_activity,
        R.drawable.very_low_activity
    )

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateProfileSecondStepBinding =
        FragmentCreateProfileSecondStepBinding.inflate(inflater, container, false)

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (requireActivity() as BottomBarManager).hide()
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

        val radioGroup = view.findViewById<RadioGroupPlus>(R.id.radio_group)

        // Получаем ConstraintLayout, который является единственным дочерним элементом RadioGroupPlus
        val constraintLayout = radioGroup.getChildAt(0) as ConstraintLayout

        // Список include и RadioButton
        val includes = listOf(
            constraintLayout.findViewById<View>(R.id.item_lowest),
            constraintLayout.findViewById(R.id.item_low),
            constraintLayout.findViewById(R.id.item_medium),
            constraintLayout.findViewById(R.id.item_high),
            constraintLayout.findViewById(R.id.item_very_high)
        )

        val radioButtons = listOf(
            constraintLayout.findViewById<RadioButton>(R.id.very_low_radio_button),
            constraintLayout.findViewById(R.id.low_radio_button),
            constraintLayout.findViewById(R.id.medium_radio_button),
            constraintLayout.findViewById(R.id.high_radio_button),
            constraintLayout.findViewById(R.id.very_high_radio_button)
        )

        // Инициализация текстов и иконок
        for (i in includes.indices) {
            val itemView = includes[i]
            val title = itemView.findViewById<TextView>(R.id.title)
            val description = itemView.findViewById<TextView>(R.id.description)
            val icon = itemView.findViewById<ImageView>(R.id.icon)

            title.text = titles[i]
            description.text = descriptions[i]
            icon.setImageResource(icons[i])
        }

        // Обработчик выбора RadioButton
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.very_low_radio_button -> { /* Логика для первого элемента */ }
                R.id.low_radio_button -> { /* Логика для второго элемента */ }
                R.id.medium_radio_button -> { /* Логика для третьего элемента */ }
                R.id.high_radio_button -> { /* Логика для четвертого элемента */ }
                R.id.very_high_radio_button -> { /* Логика для пятого элемента */ }
            }
        }

        // Устанавливаем обработчики кликов для include, чтобы они также переключали RadioButton
        for (i in includes.indices) {
            includes[i].setOnClickListener {
                radioButtons[i].isChecked = true
            }
        }

        binding.goToStepThree.setOnClickListener {
            navigator.fromCreateProfileSecondStepFragmentToCreateProfileThirdStepFragment()
        }

    }
}