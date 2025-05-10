package ru.point.home.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import ru.point.core.ContentLoadListener
import ru.point.core.navigation.BottomBarManager
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core.ui.BaseFragment
import ru.point.home.databinding.FragmentHomeProgressBinding
import ru.point.home.di.DaggerHomeFragmentComponent
import ru.point.home.di.HomeFragmentDepsProvider
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class HomeProgressFragment : BaseFragment<FragmentHomeProgressBinding>() {

    @Inject
    lateinit var homeProgressViewModelFactory: HomeProgressViewModelFactory

    private val viewModel: HomeProgressViewModel by viewModels {
        homeProgressViewModelFactory
    }

    private var listener: ContentLoadListener? = null

    override fun onAttach(context: Context) {
        DaggerHomeFragmentComponent.builder()
            .deps(HomeFragmentDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)

        listener = context as? ContentLoadListener
            ?: throw IllegalStateException(
                "Host must implement ContentLoadListener"
            )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeProgressBinding =
        FragmentHomeProgressBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).show()

        val userProfileId = SecurePrefs.getUserId().toString()
        collectUiState()
        collectUiEvent()
        viewModel.getMaxNutrientsData(userProfileId)
        viewModel.getDailyConsumptionData(userProfileId, getCurrentDateMidnightUTC() )


        binding.mealsItem.addBreakfastDishButton.setOnClickListener {
            viewModel.navigateToBreakfastMeal()
        }

        binding.mealsItem.addLunchDishButton.setOnClickListener {
            viewModel.navigateToLunchMeal()
        }

        binding.mealsItem.addDinnerDishButton.setOnClickListener {
            viewModel.navigateToDinnerMeal()
        }

        binding.mealsItem.addSnackDishButton.setOnClickListener {
            viewModel.navigateToSnackMeal()
        }
    }

    fun getCurrentDateMidnightUTC(): String {
        val zoneId = ZoneOffset.ofHours(3)
        val localDate = LocalDate.now(zoneId)
        val midnight = localDate.atStartOfDay(zoneId)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return midnight.format(formatter)
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.nutrientsData?.let { nutrients ->
                    binding.nutrientsItem.maxCaloriesText.text = nutrients.maxCalories.toInt().toString()
                    binding.nutrientsItem.circularProgressBarCalories.progressMax = nutrients.maxCalories.toFloat()
                    binding.nutrientsItem.maxCarbonsGrams.text = "${nutrients.maxCarbohydrates}г"
                    binding.nutrientsItem.maxProteinsGrams.text = "${nutrients.maxProtein}г"
                    binding.nutrientsItem.maxFatsGrams.text = "${nutrients.maxFat}г"
                    binding.nutrientsItem.carbonsProgressBar.max = nutrients.maxCarbohydrates.toInt()
                    binding.nutrientsItem.proteinsProgressBar.max = nutrients.maxProtein.toInt()
                    binding.nutrientsItem.fatsProgressBar.max = nutrients.maxFat.toInt()


                    binding.mealsItem.circularProgressBarBreakfast.progressMax = nutrients.maxBreakfastCalories.toFloat()
                    binding.mealsItem.maxBreakfastCaloriesText.text = "${nutrients.maxBreakfastCalories.toInt()} ккал"
                    binding.mealsItem.circularProgressBarLunch.progressMax = nutrients.maxLunchCalories.toFloat()
                    binding.mealsItem.maxLunchCaloriesText.text = "${nutrients.maxLunchCalories.toInt()} ккал"
                    binding.mealsItem.circularProgressBarDinner.progressMax = nutrients.maxDinnerCalories.toFloat()
                    binding.mealsItem.maxDinnerCaloriesText.text = "${nutrients.maxDinnerCalories.toInt()} ккал"
                    binding.mealsItem.circularProgressBarSnack.progressMax = nutrients.maxSnackCalories.toFloat()
                    binding.mealsItem.maxSnackCaloriesText.text = "${nutrients.maxSnackCalories.toInt()} ккал"


                }

                state.consumptionData?.let { consumption ->

                    binding.nutrientsItem.eatedCaloriesText.text = consumption.totalCalories.toInt().toString()

                    binding.nutrientsItem.circularProgressBarCalories.progress = consumption.totalCalories.toFloat()

                    binding.nutrientsItem.carbonsGrams.text = consumption.totalCarbohydrates.toString()
                    binding.nutrientsItem.proteinsGrams.text = consumption.totalProteins.toString()
                    binding.nutrientsItem.fatsGrams.text = consumption.totalFats.toString()

                    binding.nutrientsItem.carbonsProgressBar.progress = consumption.totalCarbohydrates.toInt()
                    binding.nutrientsItem.proteinsProgressBar.progress = consumption.totalProteins.toInt()
                    binding.nutrientsItem.fatsProgressBar.progress = consumption.totalFats.toInt()

                    binding.mealsItem.circularProgressBarBreakfast.progress = consumption.totalBreakfastCalories.toFloat()
                    binding.mealsItem.circularProgressBarLunch.progress = consumption.totalLunchCalories.toFloat()
                    binding.mealsItem.circularProgressBarDinner.progress = consumption.totalDinnerCalories.toFloat()
                    binding.mealsItem.circularProgressBarSnack.progress = consumption.totalSnackCalories.toFloat()

                    binding.mealsItem.breakfastCaloriesText.text = "${consumption.totalBreakfastCalories.toInt()} из"
                    binding.mealsItem.lunchCaloriesText.text = "${consumption.totalLunchCalories.toInt()} из"
                    binding.mealsItem.dinnerCaloriesText.text = "${consumption.totalDinnerCalories.toInt()} из "
                    binding.mealsItem.snackCaloriesText.text = "${consumption.totalSnackCalories.toInt()} из"

                    binding.mealsItem.breakfastDishesText.text = "${consumption.totalBreakfastItemText}"
                    binding.mealsItem.lunchDishesText.text = "${consumption.totalLunchItemText}"
                    binding.mealsItem.dinnerDishesText.text = "${consumption.totalDinnerItemText}"
                    binding.mealsItem.snackDishesText.text = "${consumption.totalSnackItemText}"




                }
                if (state.isLoading == false && state.nutrientsData != null){
                    binding.shimmerMeals.stopShimmer()
                    binding.shimmerMeals.isVisible = false
                    binding.shimmerNutrients.stopShimmer()
                    binding.shimmerNutrients.isVisible = false

                    binding.mealsItem.root.isVisible = true
                    binding.nutrientsItem.root.isVisible = true

                    lifecycleScope.launch {
                        listener?.onContentLoaded()
                    }
                }
            }
        }
    }


    private fun collectUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->

                val dailyConsumptionId = viewModel.uiState.value.consumptionData?.consumptionId
                if (dailyConsumptionId == null) {
                    return@collect
                }

                when (event) {
                    is HomeProgressUiEvent.ShowToast -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }

                    HomeProgressUiEvent.NavigateToBreakfastMeal -> {
                        val maxMealCarbons =
                            viewModel.uiState.value.nutrientsData?.maxCarbohydrates?.toDouble()
                                ?.times(0.27)
                        val maxMealProteins =
                            viewModel.uiState.value.nutrientsData?.maxProtein?.toDouble()
                                ?.times(0.27)
                        val maxMealFats =
                            viewModel.uiState.value.nutrientsData?.maxFat?.toDouble()
                                ?.times(0.27)
                        val maxMealCalories =
                            viewModel.uiState.value.nutrientsData?.maxBreakfastCalories

                        val bundle = bundleOf(
                            "dailyConsumptionId" to dailyConsumptionId,
                            "mealType" to "BREAKFAST",
                            "mealCarbons" to maxMealCarbons,
                            "mealProteins" to maxMealProteins,
                            "mealFats" to maxMealFats,
                            "maxCalories" to maxMealCalories
                        )
                        navigator.fromHomeProgressFragmentToMealProductSearchFragment(bundle)
                    }

                    HomeProgressUiEvent.NavigateToLunchMeal -> {
                        val maxMealCarbons =
                            viewModel.uiState.value.nutrientsData?.maxCarbohydrates?.toDouble()
                                ?.times(0.38)
                        val maxMealProteins =
                            viewModel.uiState.value.nutrientsData?.maxProtein?.toDouble()
                                ?.times(0.38)
                        val maxMealFats =
                            viewModel.uiState.value.nutrientsData?.maxFat?.toDouble()
                                ?.times(0.38)
                        val maxMealCalories =
                            viewModel.uiState.value.nutrientsData?.maxLunchCalories

                        val bundle = bundleOf(
                            "dailyConsumptionId" to dailyConsumptionId,
                            "mealType" to "LUNCH",
                            "mealCarbons" to maxMealCarbons,
                            "mealProteins" to maxMealProteins,
                            "mealFats" to maxMealFats,
                            "maxCalories" to maxMealCalories
                        )
                        navigator.fromHomeProgressFragmentToMealProductSearchFragment(bundle)
                    }
                    HomeProgressUiEvent.NavigateToDinnerMeal -> {
                        val maxMealCarbons =
                            viewModel.uiState.value.nutrientsData?.maxCarbohydrates?.toDouble()
                                ?.times(0.28)
                        val maxMealProteins =
                            viewModel.uiState.value.nutrientsData?.maxProtein?.toDouble()
                                ?.times(0.28)
                        val maxMealFats =
                            viewModel.uiState.value.nutrientsData?.maxFat?.toDouble()
                                ?.times(0.28)
                        val maxMealCalories =
                            viewModel.uiState.value.nutrientsData?.maxDinnerCalories

                        val bundle = bundleOf(
                            "dailyConsumptionId" to dailyConsumptionId,
                            "mealType" to "DINNER",
                            "mealCarbons" to maxMealCarbons,
                            "mealProteins" to maxMealProteins,
                            "mealFats" to maxMealFats,
                            "maxCalories" to maxMealCalories
                        )
                        navigator.fromHomeProgressFragmentToMealProductSearchFragment(bundle)
                    }
                    HomeProgressUiEvent.NavigateToSnackMeal -> {
                        val maxMealCarbons =
                            viewModel.uiState.value.nutrientsData?.maxCarbohydrates?.toDouble()
                                ?.times(0.07)
                        val maxMealProteins =
                            viewModel.uiState.value.nutrientsData?.maxProtein?.toDouble()
                                ?.times(0.07)
                        val maxMealFats =
                            viewModel.uiState.value.nutrientsData?.maxFat?.toDouble()
                                ?.times(0.07)
                        val maxMealCalories =
                            viewModel.uiState.value.nutrientsData?.maxSnackCalories

                        val bundle = bundleOf(
                            "dailyConsumptionId" to dailyConsumptionId,
                            "mealType" to "SNACK",
                            "mealCarbons" to maxMealCarbons,
                            "mealProteins" to maxMealProteins,
                            "mealFats" to maxMealFats,
                            "maxCalories" to maxMealCalories
                        )
                        navigator.fromHomeProgressFragmentToMealProductSearchFragment(bundle)
                    }
                }
            }
        }
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
