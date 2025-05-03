package ru.point.fasting.ui

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import androidx.core.net.toUri
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

class FastingFragment : BaseFragment<FragmentFastingBinding>() {

    @Inject
    lateinit var viewModelFactory: FastingViewModelFactory
    private val viewModel: FastingViewModel by viewModels { viewModelFactory }

    private var countDownTimer: CountDownTimer? = null

    private val notifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) showNotificationPermissionRationale()
        }


    override fun onAttach(context: Context) {
        DaggerTimerComponent.builder()
            .application(context.applicationContext as Application)
            .deps(TimerFragmentDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFastingBinding = FragmentFastingBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestNotificationPermissionIfNeeded()

        ensureExactAlarmPermission()

        val userId = SecurePrefs.getUserId()!!

        viewModel.loadTimer(userId)

        collectUiState(userId)



        binding.trackerItem.switchFastingModeButton.setOnClickListener {
            viewModel.onToggleStartSwitch(System.currentTimeMillis(), userId)
            binding.trackerItem.circularProgressBarTimer.apply {
                progress = 0F
            }
        }

        binding.trackerItem.changeStartTimeButton.setOnClickListener {
            val now = Calendar.getInstance()
            val currentHour = now.get(Calendar.HOUR_OF_DAY)
            val currentMinute = now.get(Calendar.MINUTE)

            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(INPUT_MODE_CLOCK)
                .setHour(currentHour)
                .setMinute(currentMinute)
                .setTitleText("Выберите время старта")
                .build()

            picker.show(parentFragmentManager, "TIME_PICKER_TAG")

            picker.addOnPositiveButtonClickListener {
                val pickedHour = picker.hour
                val pickedMinute = picker.minute

                val pickedTime = LocalTime.of(pickedHour, pickedMinute)
                val nowTime = LocalTime.now()

                if (pickedTime.isAfter(nowTime)) {
                    Toast.makeText(
                        requireContext(),
                        "Нельзя выбирать время позже текущего", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val today = LocalDate.now()
                    val epochMillis = today
                        .atTime(pickedTime)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    Log.d("FastingFragment", "Epoch millis = $epochMillis, calling ViewModel…")

                    viewModel.changeStartInterval(epochMillis, userId)
                }
            }
        }


        binding.trackerItem.stopTimerButton.setOnClickListener {
            val userId = SecurePrefs.getUserId() ?: return@setOnClickListener
            binding.trackerItem.circularProgressBarTimer.apply {
                setProgressWithAnimation(0F, 500)
            }
            viewModel.onStop(userId)
        }

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

        parentFragmentManager.setFragmentResultListener(
            "scenario_updated",
            viewLifecycleOwner
        ) { _, bundle ->
            val updatedScenarioId = bundle.getString("updatedScenarioId")
            updatedScenarioId?.let {
                viewModel.loadTimer(userId)
                binding.trackerItem.circularProgressBarTimer.apply {
                    setProgressWithAnimation(0F, 500)
                }
            }
        }

        val fastingModePagerAdapter = FastingModePagerAdapter(modItems) { scenarioName ->
            ScenarioBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString("ARG_SCENARIO_NAME", scenarioName)
                }
            }.show(parentFragmentManager, "SCENARIO_SHEET")
        }

        viewPager.adapter = fastingModePagerAdapter
        PagerSnapHelper().attachToRecyclerView(binding.fastingModesViewPager)

    }

    private fun ensureExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = requireContext().getSystemService(AlarmManager::class.java)
            if (!am.canScheduleExactAlarms()) {
                startActivity(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        .setData("package:${requireContext().packageName}".toUri())
                )
            }
        }
    }

    private fun collectUiState(
        userId: String
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { userTimer ->
                if (userTimer == null) return@collect

                val off = userTimer.status == TimerStatus.OFF && !userTimer.isActive

                binding.trackerItem.centerProgressImage.setImageResource(
                    when (userTimer.scenario.name) {
                        "16/8" -> R.drawable.icon16_8
                        "14/10" -> R.drawable.icon14_10
                        "18/6" -> R.drawable.icon18_6
                        "20/4" -> R.drawable.icon20_4
                        "1/1" -> R.drawable.icon1_1
                        "3/1" -> R.drawable.icon3_1
                        else -> R.drawable.icon16_8
                    }
                )
                val pickedNameView = binding.trackerItem.pickedTrackerNameText

                val countdownView = binding.trackerItem.countdownTimerText
                val startTimeView = binding.trackerItem.startDateFasting
                val endTimeView = binding.trackerItem.endDateFasting

                if (off) {
                    countDownTimer?.cancel()
                    countDownTimer = null
                    binding.trackerItem.switchFastingModeButton.apply {
                        backgroundTintList =
                            ContextCompat.getColorStateList(
                                context,
                                R.color.dark_water_and_products_buttons
                            )
                        strokeColor =
                            ContextCompat.getColorStateList(
                                context,
                                R.color.dark_main_background_elements
                            )
                        strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke)
                        text = getString(R.string.on_tracker)
                    }
                    binding.trackerItem.statusTimer.text =
                        getString(R.string.setting_tracker_your_own)
                    binding.trackerItem.changeStartTimeButton.visibility = View.INVISIBLE
                    binding.trackerItem.yourTrackerText.text = getString(R.string.your_tracker)
                    pickedNameView.text = "${userTimer.scenario.name}"
                    binding.trackerItem.stopTimerButton.isVisible = false
                    pickedNameView.visibility = View.VISIBLE
                    countdownView.visibility = View.INVISIBLE

                    val now = Instant.now().atZone(ZoneId.systemDefault())

                    val fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm")
                    startTimeView.text = now.format(fmt)

                    val end = now.plusHours(userTimer.scenario.fastingHours.toLong())
                    endTimeView.text = end.format(fmt)
                } else {
                    binding.trackerItem.yourTrackerText.text = getString(R.string.last_time)
                    binding.trackerItem.changeStartTimeButton.visibility = View.VISIBLE

                    val fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm")
                    binding.trackerItem.stopTimerButton.isVisible = true
                    pickedNameView.visibility = View.INVISIBLE
                    countdownView.visibility = View.VISIBLE

                    if (userTimer.status == TimerStatus.FASTING) {
                        binding.trackerItem.statusTimer.text = getString(R.string.you_on_fasting)
                        binding.trackerItem.startDateFastingEatingText.text =
                            getString(R.string.start_fasting_period)
                        binding.trackerItem.endDateFastingEatingText.text =
                            getString(R.string.end_fasting_period)
                        binding.trackerItem.switchFastingModeButton.apply {
                            backgroundTintList =
                                ContextCompat.getColorStateList(
                                    context,
                                    R.color.start_tracker_fasting_color
                                )
                            strokeColor =
                                ContextCompat.getColorStateList(
                                    context,
                                    R.color.dark_main_background_elements
                                )
                            strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke)
                            text = getString(R.string.on_eating)
                        }
                    } else {
                        binding.trackerItem.statusTimer.text = getString(R.string.you_on_eating)
                        binding.trackerItem.startDateFastingEatingText.text =
                            getString(R.string.start_eating_period)
                        binding.trackerItem.endDateFastingEatingText.text =
                            getString(R.string.end_eating_period)
                        binding.trackerItem.switchFastingModeButton.apply {
                            backgroundTintList =
                                ContextCompat.getColorStateList(
                                    context,
                                    R.color.stop_tracker_fasting_color
                                )
                            strokeColor =
                                ContextCompat.getColorStateList(
                                    context,
                                    R.color.dark_main_background_elements
                                )
                            strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke)
                            text = getString(R.string.on_fasting)
                        }
                    }

                    userTimer.startTime?.let { startMillis ->
                        val startZdt = Instant
                            .ofEpochMilli(startMillis)
                            .atZone(ZoneId.systemDefault())
                        startTimeView.text =
                            startZdt.format(fmt)
                    }

                    userTimer.endTime?.let { endMillis ->
                        val endZdt = Instant.ofEpochMilli(endMillis)
                            .atZone(ZoneId.systemDefault())
                        endTimeView.text = endZdt.format(fmt)
                    }

                    countDownTimer?.cancel()

                    val ms = (userTimer.endTime ?: 0L) - System.currentTimeMillis()

                    val totalHours = when (userTimer.status) {
                        TimerStatus.FASTING -> userTimer.scenario.fastingHours
                        TimerStatus.EATING -> userTimer.scenario.eatingHours
                        else -> 0
                    }

                    val initialRemainingHours = (ms / 3_600_000f).coerceAtLeast(0f)
                    binding.trackerItem.circularProgressBarTimer.apply {
                        progressMax = totalHours.toFloat()
                        setProgressWithAnimation(initialRemainingHours, 500L)
                    }

                    countDownTimer = object : CountDownTimer(ms, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val h = millisUntilFinished / 3600000
                            val m = (millisUntilFinished % 3600000) / 60000
                            val s = (millisUntilFinished % 60000) / 1000
                            binding.trackerItem.countdownTimerText.text =
                                "%02d:%02d:%02d".format(h, m, s)

                            if (s == 0L) {
                                val remainingHours =
                                    (millisUntilFinished / 3_600_000f).coerceAtLeast(0f)
                                binding.trackerItem.circularProgressBarTimer.apply {
                                    setProgressWithAnimation(remainingHours, 500L)
                                }
                            }
                        }

                        override fun onFinish() {
                            viewModel.onAdjustStart(
                                userTimer.endTime ?: System.currentTimeMillis(),
                                userId
                            )
                        }
                    }.start()

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        countDownTimer = null
    }


    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                showNotificationPermissionRationale()
            } else {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    private fun showNotificationPermissionRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle("Уведомления")
            .setMessage("Разрешите уведомления, чтобы получать сигнал о смене фаз.")
            .setPositiveButton("Разрешить") { _, _ ->
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Не сейчас", null)
            .show()
    }


}