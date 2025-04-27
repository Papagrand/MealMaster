package ru.point.recipe_information.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import ru.point.recipes.R
import kotlinx.coroutines.launch
import ru.point.core.navigation.BottomBarManager
import ru.point.core.secure_prefs.SecurePrefs
import ru.point.core.ui.BaseFragment
import ru.point.pick_meal.ui.PickMealBottomSheetFragment
import ru.point.recipe_information.di.DaggerRecipeInformationComponent
import ru.point.recipe_information.di.RecipeInformationDepsProvider
import ru.point.recipe_information.ui.adapters.RecipeIngredientsAdapter
import ru.point.recipe_information.ui.adapters.RecipeStepsAdapter
import javax.inject.Inject
import ru.point.recipes.databinding.FragmentRecipeInformationBinding

class RecipeInformationFragment : BaseFragment<FragmentRecipeInformationBinding>() {

    @Inject
    lateinit var recipeViewModelFactory: RecipeInformationViewModelFactory

    private val viewModel: RecipeInformationViewModel by viewModels {
        recipeViewModelFactory
    }

    private var servingSizeJob: Job? = null
    private var caloriesJob: Job? = null
    private var isEditingWeight = false
    private var isEditingCalories = false
    private var isInputsInitialized = false


    override fun onAttach(context: Context) {
        DaggerRecipeInformationComponent.builder()
            .deps(RecipeInformationDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    private val stepsAdapter by lazy { RecipeStepsAdapter() }
    private val ingredientsAdapter by lazy { RecipeIngredientsAdapter() }


    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecipeInformationBinding {
        return FragmentRecipeInformationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ingredientsRecyclerView.adapter = ingredientsAdapter

        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.stepsRecyclerView.adapter = stepsAdapter

        val recipeId = arguments?.getString("recipeId")
        val productId = arguments?.getString("productId")
        viewModel.loadRecipe(recipeId!!)

        collectUiState()
        collectUiEvent()


        binding.errorRetryButton.setOnClickListener {
            collectUiState()
            collectUiEvent()
            viewModel.loadRecipe(recipeId)
        }

        binding.addRecipeToMealButton.setOnClickListener {
            val recipe = viewModel.uiState.value.recipeData ?: return@setOnClickListener

            val servingText = binding.servingSizeEditTextLayout.editText?.text.toString()
            val servingSize = servingText.toDoubleOrNull()
                ?: recipe.recipeProductServingSizeDefault

            val userId = SecurePrefs.getUserId()

            val bottomSheet = PickMealBottomSheetFragment.newInstance(
                userId = userId!!,
                productId = productId!!,
                servingSize = servingSize
            )

            bottomSheet.show(childFragmentManager, PickMealBottomSheetFragment.TAG)
        }

        setupTextWatchers()
    }


    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.isLoading) {
                    binding.progressIndicator.isVisible = true
                } else {
                    binding.progressIndicator.isVisible = false
                }

                if (state.error != null) {
                    binding.errorContainer.isVisible = true
                } else {
                    binding.errorContainer.isVisible = false
                }

                state.recipeData?.let { recipe ->
                    if (!isInputsInitialized) {
                        isInputsInitialized = true

                        binding.errorContainer.isVisible = false
                        binding.recipeAppBarLayout.isVisible = true
                        binding.recipeScrollView.isVisible = true
                        binding.addRecipeToMealButton.isVisible = true
                        binding.recipeCollapsingToolbar.title = recipe.recipeName

                        val backdropString = recipe.recipeBackdrop
                        if (backdropString.startsWith("data:image", ignoreCase = true)) {
                            val base64Part = backdropString.substringAfter("base64,")
                            val decodedBytes =
                                android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
                            binding.productImage.load(decodedBytes) {
                                crossfade(true)
                            }
                        } else {
                            binding.productImage.load(backdropString) {
                                crossfade(true)
                            }
                        }
                        binding.servingSizeEditTextLayout.editText?.setText(
                            recipe.recipeProductServingSizeDefault.toString()
                        )
                        binding.caloriesEditTextLayout.editText?.setText(
                            recipe.recipeProductCalories.toString()
                        )

                    } else {
                        if (isEditingWeight) {
                            binding.caloriesEditTextLayout.editText
                                ?.setText(recipe.recipeProductCalories.toString())
                        } else if (isEditingCalories) {
                            binding.servingSizeEditTextLayout.editText
                                ?.setText(recipe.recipeProductServingSizeDefault.toString())
                        }
                    }

                    binding.apply {
                        val textDefaultPortion = getString(R.string.default_product_portion)
                        binding.portionSize.setText(textDefaultPortion + " " + recipe.recipeProductServingSizeDefault)

                        binding.productFatValue.setText(recipe.recipeProductFat.toString() + " гр")
                        binding.productProteinValue.setText(recipe.recipeProductProtein.toString() + " гр")
                        binding.productCarbohydratesValue.setText(recipe.recipeProductCarbohydrates.toString() + " гр")
                    }

                    ingredientsAdapter.submitList(recipe.recipeIngredients)
                    stepsAdapter.submitLists(recipe.recipeSteps, recipe.recipeImages)
                }
            }
        }
    }


    private fun setupTextWatchers() = with(binding) {
        val weightEdit = servingSizeEditTextLayout.editText!!
        val caloriesEdit = caloriesEditTextLayout.editText!!

        weightEdit.doAfterTextChanged { text ->
            if (!weightEdit.isFocused) return@doAfterTextChanged
            isEditingWeight = true
            isEditingCalories = false

            servingSizeJob?.cancel()
            servingSizeJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                text.toString().toDoubleOrNull()?.let { viewModel.onServingSizeChanged(it) }
            }
        }

        caloriesEdit.doAfterTextChanged { text ->
            if (!caloriesEdit.isFocused) return@doAfterTextChanged
            isEditingCalories = true
            isEditingWeight = false

            caloriesJob?.cancel()
            caloriesJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                text.toString().toDoubleOrNull()?.let { viewModel.onCaloriesChanged(it) }
            }
        }
    }


    private fun collectUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is RecipeUiEvent.ShowToast -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }

                    is RecipeUiEvent.NavigateBack -> {
                        findNavController()
                            .previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("clearSearch", "YES")

                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}