package ru.point.auth.ui.profile_create.second_step

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import ru.point.auth.R
import ru.point.auth.databinding.FragmentCreateProfileSecondStepBinding
import ru.point.auth.ui.on_boarding.OnboardingStep
import ru.point.auth.ui.on_boarding.OnboardingViewModel
import ru.point.auth.ui.profile_create.first_step.CreateProfileFirstStepFragment
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import worker8.com.github.radiogroupplus.RadioGroupPlus

class CreateProfileSecondStepFragment : BaseFragment<FragmentCreateProfileSecondStepBinding>(),
    OnboardingStep {

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

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


    override fun saveData() {
        // Читаем id выбранного RadioButton из radioGroup, определённого в binding
        val selectedId = binding.radioGroup.checkedRadioButtonId
        // Определяем уровень активности (число от 1 до 5)
        val activityLevel = when (selectedId) {
            R.id.very_low_radio_button -> 1
            R.id.low_radio_button -> 2
            R.id.medium_radio_button -> 3
            R.id.high_radio_button -> 4
            R.id.very_high_radio_button -> 5
            else -> 0
        }
        if (activityLevel != 0){
            onboardingViewModel.updateCanGo(true)
            onboardingViewModel.updateActivityLevel(activityLevel)
        }else{
            onboardingViewModel.updateCanGo(false)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

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

        // Используем binding для получения RadioGroupPlus
        val radioGroup: RadioGroupPlus = binding.radioGroup

        // Получаем ConstraintLayout, являющийся контейнером для элементов внутри radioGroup
        val containerLayout = radioGroup.getChildAt(0) as ConstraintLayout

        // Получаем списки include-элементов и RadioButton
        val includes = listOf(
            containerLayout.findViewById<View>(R.id.item_lowest),
            containerLayout.findViewById(R.id.item_low),
            containerLayout.findViewById(R.id.item_medium),
            containerLayout.findViewById(R.id.item_high),
            containerLayout.findViewById(R.id.item_very_high)
        )

        val radioButtons = listOf(
            containerLayout.findViewById<RadioButton>(R.id.very_low_radio_button),
            containerLayout.findViewById(R.id.low_radio_button),
            containerLayout.findViewById(R.id.medium_radio_button),
            containerLayout.findViewById(R.id.high_radio_button),
            containerLayout.findViewById(R.id.very_high_radio_button)
        )

        // Инициализация текстовых полей и иконок для каждого элемента
        for (i in includes.indices) {
            val itemView = includes[i]
            val title = itemView.findViewById<TextView>(R.id.title)
            val description = itemView.findViewById<TextView>(R.id.description)
            val icon = itemView.findViewById<ImageView>(R.id.icon)

            title.text = titles[i]
            description.text = descriptions[i]
            icon.setImageResource(icons[i])
        }

        // Устанавливаем обработчик кликов для include-элементов, чтобы они переключали соответствующий RadioButton
        for (i in includes.indices) {
            includes[i].setOnClickListener {
                radioButtons[i].isChecked = true
            }
        }

        // Обработчик выбора RadioButton можно оставить пустым, если все данные будут сохранены через saveData()
        radioGroup.setOnCheckedChangeListener { _, _ -> /* Можно обновлять UI на лету, если требуется */ }
    }

    companion object {
        fun getNewInstance() = CreateProfileSecondStepFragment()
    }
}