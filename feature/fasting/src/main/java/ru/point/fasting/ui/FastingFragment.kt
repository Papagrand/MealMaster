package ru.point.fasting.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import kotlinx.coroutines.launch
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core.ui.BaseFragment
import ru.point.fasting.R
import ru.point.fasting.databinding.FragmentFastingBinding
import ru.point.fasting.di.DaggerTimerComponent
import ru.point.fasting.di.TimerFragmentDepsProvider
import ru.point.fasting.domain.TimerStatus
import ru.point.fasting.ui.adapters.FastingModePagerAdapter
import ru.point.fasting.ui.adapters.MyItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FastingFragment : BaseFragment<FragmentFastingBinding>() {

    @Inject
    lateinit var viewModelFactory: FastingViewModelFactory
    private val viewModel: FastingViewModel by viewModels { viewModelFactory }


    override fun onAttach(context: Context) {
        DaggerTimerComponent.builder()
            .deps(TimerFragmentDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFastingBinding = FragmentFastingBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = SecurePrefs.getUserId()!!

        viewModel.loadTimer(userId)

        collectUiState()

        val viewPager = binding.fastingModesViewPager

        val modItems = listOf(
            MyItem(
                R.drawable.icon16_8,
                getString(R.string.tracker_name_16_8),
                getString(R.string.tracker16_8_description)
            ),
            MyItem(
                R.drawable.icon14_10,
                getString(R.string.tracker_name_14_10),
                getString(R.string.tracker14_10_description)
            ),
            MyItem(
                R.drawable.icon18_6,
                getString(R.string.tracker_name_18_6),
                getString(R.string.tracker18_6_description)
            ),
            MyItem(
                R.drawable.icon20_4,
                getString(R.string.tracker_name_20_4),
                getString(R.string.tracker20_4_description)
            ),
            MyItem(
                R.drawable.icon3_1,
                getString(R.string.tracker_name_3_1),
                getString(R.string.tracker3_1_description)
            ),
            MyItem(
                R.drawable.icon1_1,
                getString(R.string.tracker_name_1_1),
                getString(R.string.tracker1_1_description)
            )

        )

        val fastingModePagerAdapter = FastingModePagerAdapter(modItems)
        viewPager.adapter = fastingModePagerAdapter
        PagerSnapHelper().attachToRecyclerView(binding.fastingModesViewPager)

    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { userTimer ->
                if (userTimer == null) {
                    Log.d("FastingFragment", "Timer not initialized yet")
                } else {
                    Log.d("FastingFragment", "Loaded timer: $userTimer")

                    val pickedNameView = binding.trackerItem.pickedTrackerNameText

                    val countdownView = binding.trackerItem.countdownTimerText
                    val startTimeView = binding.trackerItem.startDateFasting
                    val endTimeView = binding.trackerItem.endDateFasting

                    if (userTimer.status == TimerStatus.OFF && !userTimer.isActive) {
                        // Показываем имя сценария + смайлик
                        pickedNameView.text = "${userTimer.scenario.name}"

                        // Скрываем таймер обратного отсчёта
                        countdownView.visibility = View.INVISIBLE

                        // Берём текущее время
                        val now = Instant.now().atZone(ZoneId.systemDefault())

                        // Форматируем как "dd.MM HH:mm"
                        val fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm")
                        startTimeView.text = now.format(fmt)

                        // Вычисляем окончание фазы: сейчас + fastingHours
                        val end = now.plusHours(userTimer.scenario.fastingHours.toLong())
                        endTimeView.text = end.format(fmt)

                    }
//                    else {
//                        // Иначе — можно показывать текущее состояние фазы
//                        pickedNameView.text = when (userTimer.status) {
//                            TimerStatus.FASTING -> getString(R.string.status_fasting)
//                            TimerStatus.EATING -> getString(R.string.status_eating)
//                            else -> pickedNameView.text
//                        }
//                    }
                }
            }
        }
    }

}