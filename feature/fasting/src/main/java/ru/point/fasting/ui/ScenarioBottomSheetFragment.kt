package ru.point.fasting.ui

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.fasting.R
import ru.point.fasting.databinding.FragmentScenarioBottomSheetBinding
import ru.point.fasting.di.DaggerTimerComponent
import ru.point.fasting.di.TimerFragmentDepsProvider
import javax.inject.Inject

class ScenarioBottomSheetFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: FastingViewModelFactory

    private val sharedViewModel: FastingViewModel by activityViewModels { viewModelFactory }

    private var _binding: FragmentScenarioBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var scenarioName: String


    override fun onAttach(context: Context) {
        DaggerTimerComponent.builder()
            .application(requireActivity().application as Application)
            .deps(TimerFragmentDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scenarioName = requireArguments().getString("ARG_SCENARIO_NAME")!!

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScenarioBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var scenarioId = ""

        when (scenarioName) {
            "16/8" -> {
                scenarioId = "fasting16-8"
                binding.scenarioIcon.setImageResource(R.drawable.icon16_8)
            }

            "14/10" -> {
                scenarioId = "fasting14-10"
                binding.scenarioIcon.setImageResource(R.drawable.icon14_10)
            }

            "18/6" -> {
                scenarioId = "fasting18-6"
                binding.scenarioIcon.setImageResource(R.drawable.icon18_6)
            }

            "20/4" -> {
                scenarioId = "fasting20-4"
                binding.scenarioIcon.setImageResource(R.drawable.icon20_4)
            }

            "1/1" -> {
                scenarioId = "fasting1-1"
                binding.scenarioIcon.setImageResource(R.drawable.icon1_1)
            }

            "3/1" -> {
                scenarioId = "fasting3-1"
                binding.scenarioIcon.setImageResource(R.drawable.icon3_1)
            }
        }

        sharedViewModel.loadScenario(scenarioId = scenarioId)

        collectUiState()

        binding.backFromInfoButton.setOnClickListener {
            dismiss()
        }

        val userId = SecurePrefs.getUserId()!!

        binding.saveNewScenarioButton.setOnClickListener {
            sharedViewModel.onStop(userId)

            it.isEnabled = false
            sharedViewModel.updateScenario(userId, scenarioId)

            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.updateScenarioResult.first { it }
                parentFragmentManager.setFragmentResult(
                    "scenario_updated",
                    Bundle().apply { putString("updatedScenarioId", scenarioId) }
                )
                dismiss()
            }
        }

    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog ?: return
        val bottomSheet = dialog
            .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?: return

        BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            peekHeight = resources.displayMetrics.heightPixels
            skipCollapsed = true
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.scenarioUiState.collect { scenario ->
                if (scenario != null) {
                    binding.scenarioName.text =
                        "${getString(R.string.scenario_name_string)} ${scenario.name}"
                    binding.scenarioDescriptionValue.text =
                        scenario.description
                    if (scenario.notice != null) {
                        binding.noticeDivider.isVisible = true
                        binding.scenarioNoticeText.isVisible = true
                        binding.scenarioNoticeValue.apply {
                            isVisible = true
                            text = scenario.notice
                        }
                    } else {
                        binding.noticeDivider.isVisible = false
                        binding.scenarioNoticeText.isVisible = false
                        binding.scenarioNoticeValue.isVisible = false
                    }
                }
            }
        }
    }

}