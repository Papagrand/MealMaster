package ru.point.meal.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import ru.point.meal.R
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import ru.point.meal.databinding.FragmentMealProductSearchBinding
import ru.point.meal.di.DaggerMealProductSearchComponent
import ru.point.meal.di.MealProductSearchFragmentDeps
import ru.point.meal.di.MealProductSearchFragmentDepsProvider
import ru.point.meal.ui.adapters.MealPickedAdapter
import ru.point.meal.ui.adapters.SearchedProductsAdapter
import ru.point.meal.ui.bottom_sheets.SearchProductsSettingsBottomSheetFragment

class MealProductSearchFragment : BaseFragment<FragmentMealProductSearchBinding>() {

    @Inject
    lateinit var mealProductSearchViewModelFactory: MealProductSearchViewModelFactory


    private val viewModel: MealProductSearchViewModel by viewModels {
        mealProductSearchViewModelFactory
    }

    private val mealProductAdapter by lazy { MealPickedAdapter() }

    private val searchedAdapter by lazy {
        SearchedProductsAdapter { productModel ->
            // Обработка клика по найденному продукту (например, добавить в приём)
            //viewModel.addProductToMeal(productModel)
        }
    }

    private var searchJob: Job? = null

    override fun onAttach(context: Context) {
        DaggerMealProductSearchComponent.builder()
            .deps(MealProductSearchFragmentDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }


    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMealProductSearchBinding =
        FragmentMealProductSearchBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        binding.mealProductRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealProductAdapter
        }
        binding.mealSearchResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchedAdapter
        }

        //Возможно придется перенести в lateinit в другую функцию жц
        val dailyConsumptionId = arguments?.getString("dailyConsumptionId")
        val mealType = arguments?.getString("mealType")
        val maxMealCarbons = arguments?.getDouble("mealCarbons")!!.toInt()
        val maxMealProteins = arguments?.getDouble("mealProteins")!!.toInt()
        val maxMealFats = arguments?.getDouble("mealFats")!!.toInt()
        val maxMealCalories = arguments?.getDouble("maxCalories")!!.toInt()

        if (dailyConsumptionId != null && mealType != null)
            viewModel.getMealInformation(dailyConsumptionId, mealType)

        binding.nutrientsItem.mealText.text = when (mealType) {
            "BREAKFAST" -> "Завтрак"
            "LUNCH" -> "Обед"
            "DINNER" -> "Ужин"
            "SNACK" -> "Перекус"
            else -> "Завтрак"
        }

        binding.nutrientsItem.circularProgressBarCalories.progressMax = maxMealCalories!!.toFloat()
        binding.nutrientsItem.maxCaloriesText.text = maxMealCalories.toString()

        binding.nutrientsItem.carbonsProgressBar.max = maxMealCarbons
        binding.nutrientsItem.proteinsProgressBar.max = maxMealProteins
        binding.nutrientsItem.fatsProgressBar.max = maxMealFats

        binding.nutrientsItem.maxCarbonsGrams.text = maxMealCarbons.toString()
        binding.nutrientsItem.maxProteinsGrams.text = maxMealProteins.toString()
        binding.nutrientsItem.maxFatsGrams.text = maxMealFats.toString()


        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.motionLayout.transitionToEnd()
            }
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(1500)
                val query = text?.toString().orEmpty()
                if (query.isNotBlank()) {
                    viewModel.getSearchedProducts(productName = query)
                    viewModel.updateSearchSettings(
                        viewModel.searchSettings.value.copy(searchText = query)
                    )
                }
            }
        }

        binding.searchTextInputLayout.setEndIconOnClickListener {
            val currentSettings = viewModel.searchSettings.value
            SearchProductsSettingsBottomSheetFragment
                .newInstance(currentSettings)
                .show(childFragmentManager, SearchProductsSettingsBottomSheetFragment.TAG)
        }

        childFragmentManager.setFragmentResultListener(
            SearchProductsSettingsBottomSheetFragment.RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val updatedVegan = bundle.getBoolean("arg_is_vegan")
            val updatedSort = bundle.getString("arg_sort_calories")
            viewModel.updateSearchSettings(
                viewModel.searchSettings.value.copy(
                    isVegan = updatedVegan,
                    sortCalories = updatedSort
                )
            )
            viewModel.searchSettings.value.searchText?.let { savedQuery ->
                if (savedQuery.isNotBlank()) viewModel.getSearchedProducts(productName = savedQuery)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.motionLayout.currentState == R.id.start) {
                        navigator.fromMealFragmentToHomeProgressFragment()
                    }
                    binding.searchEditText.text?.clear()
                    searchedAdapter.submitList(emptyList())
                    viewModel.updateSearchSettings(
                        viewModel.searchSettings.value.copy(searchText = null)
                    )
                    binding.searchEditText.clearFocus()
                    binding.motionLayout.transitionToStart()
                }
            }
        )


        collectUiState()
        collectUiEvent()

    }


    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.mealData?.let { mealInfo ->
                    if (mealInfo.mealItemsList.isNotEmpty()) {
                        binding.productsInMeal.isVisible = true
                    }

                    binding.nutrientsItem.circularProgressBarCalories.progress =
                        mealInfo.sumMealCalories.toFloat()
                    binding.nutrientsItem.eatedCaloriesText.text =
                        mealInfo.sumMealCalories.toInt().toString()

                    binding.nutrientsItem.carbonsProgressBar.progress =
                        mealInfo.sumMealCarbohydrates.toInt()
                    binding.nutrientsItem.proteinsProgressBar.progress =
                        mealInfo.sumMealProteins.toInt()
                    binding.nutrientsItem.fatsProgressBar.progress = mealInfo.sumMealFats.toInt()

                    binding.nutrientsItem.carbonsGrams.text =
                        mealInfo.sumMealCarbohydrates.toInt().toString()
                    binding.nutrientsItem.proteinsGrams.text =
                        mealInfo.sumMealProteins.toInt().toString()
                    binding.nutrientsItem.fatsGrams.text = mealInfo.sumMealFats.toInt().toString()

                    mealProductAdapter.submitList(mealInfo.mealItemsList)
                }

                state.searchedData?.let { results ->
                    searchedAdapter.submitList(results)
                }

                if (state.isLoading == false && state.mealData != null) {
                    binding.shimmerNutrients.stopShimmer()
                    binding.shimmerNutrients.isVisible = false
                    binding.nutrientsItem.root.isVisible = true
                }
            }
        }
    }

    private fun collectUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->

            }
        }
    }

}