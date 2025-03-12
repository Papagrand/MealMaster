package ru.point.auth.ui.profile_create.third_step

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ru.point.auth.R
import ru.point.auth.databinding.FragmentCreateProfileThirdStepBinding
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import worker8.com.github.radiogroupplus.RadioGroupPlus

class CreateProfileThirdStepFragment : BaseFragment<FragmentCreateProfileThirdStepBinding>() {

    private lateinit var titles: List<String>
    private lateinit var descriptions: List<String>
    private lateinit var pfcs: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateProfileThirdStepBinding =
        FragmentCreateProfileThirdStepBinding.inflate(inflater, container, false)

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (requireActivity() as BottomBarManager).hide()
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

        val radioGroup = view.findViewById<RadioGroupPlus>(R.id.radioGroupGoal)

        val constraintLayout = radioGroup.getChildAt(0) as ConstraintLayout

        val includes = listOf(
            constraintLayout.findViewById<View>(R.id.item_loss),
            constraintLayout.findViewById(R.id.item_gain),
            constraintLayout.findViewById(R.id.item_maintenance),
        )

        val radioButtons = listOf(
            constraintLayout.findViewById<RadioButton>(R.id.loss_radio_button),
            constraintLayout.findViewById(R.id.gain_radio_button),
            constraintLayout.findViewById(R.id.maintenance_radio_button)
        )

        for (i in includes.indices) {
            val itemView = includes[i]
            val title = itemView.findViewById<TextView>(R.id.title)
            val description = itemView.findViewById<TextView>(R.id.description)
            val pfc = itemView.findViewById<TextView>(R.id.pfc_text)

            title.text = titles[i]
            description.text = descriptions[i]
            pfc.text = pfcs[i]
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.loss_radio_button -> { /* Логика для первого элемента */ }
                R.id.gain_radio_button -> { /* Логика для второго элемента */ }
                R.id.maintenance_radio_button -> { /* Логика для третьего элемента */ }
            }
        }

        // Устанавливаем обработчики кликов для include, чтобы они также переключали RadioButton
        for (i in includes.indices) {
            includes[i].setOnClickListener {
                radioButtons[i].isChecked = true
            }
        }


        binding.goToStepFour.setOnClickListener {
            navigator.fromCreateProfileThirdStepFragmentToCreateProfileFourthStepFragment()
        }

    }


}