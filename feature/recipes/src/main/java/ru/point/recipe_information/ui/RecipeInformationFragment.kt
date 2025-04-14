package ru.point.recipe_information.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import ru.point.recipes.R
import kotlinx.coroutines.launch
import ru.point.core.navigation.BottomBarManager
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

    // Инициализируем ViewModel через activityViewModels
    private val viewModel: RecipeInformationViewModel by activityViewModels {
        recipeViewModelFactory
    }


    override fun onAttach(context: Context) {
        DaggerRecipeInformationComponent.builder()
            .deps(RecipeInformationDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    // Пример: два адаптера, один для шагов, другой для ингредиентов
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

        collectUiState()
        collectUiEvent()

        val recipeId = "r-ht38c1nmo75r"
        viewModel.loadRecipe(recipeId)

        binding.errorRetryButton.setOnClickListener{
            collectUiState()
            collectUiEvent()
            viewModel.loadRecipe(recipeId)
        }

        binding.addRecipeToMealButton.setOnClickListener {
            val bottomSheet = PickMealBottomSheetFragment()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
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
                    binding.errorContainer.isVisible = false

                    binding.recipeAppBarLayout.isVisible = true
                    binding.recipeScrollView.isVisible = true
                    binding.addRecipeToMealButton.isVisible = true

                    binding.recipeCollapsingToolbar.title = recipe.recipeName


                    val backdropString = recipe.recipeBackdrop
                    if (backdropString.startsWith("data:image", ignoreCase = true)) {
                        val base64Part = backdropString.substringAfter("base64,")
                        val decodedBytes = android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
                        binding.productImage.load(decodedBytes) {
                            crossfade(true)
                        }
                    } else {
                        binding.productImage.load(backdropString) {
                            crossfade(true)
                        }
                    }
                    val textDefaultPortion = getString(R.string.default_product_portion)
                    binding.portionSize.setText(textDefaultPortion + " " + recipe.recipeProductServingSizeDefault)

                    binding.productProteinValue.setText(recipe.recipeProductProtein.toString() + " гр")
                    binding.productFatValue.setText(recipe.recipeProductFat.toString() + " гр")
                    binding.productCarbohydratesValue.setText(recipe.recipeProductCarbohydrates.toString() + " гр")


                    binding.servingSizeEditTextLayout.editText?.setText(
                        recipe.recipeProductServingSizeDefault.toString()
                    )
                    binding.caloriesEditTextLayout.editText?.setText(
                        recipe.recipeProductCalories.toString()
                    )

                    ingredientsAdapter.submitList(recipe.recipeIngredients)

                    stepsAdapter.submitLists(recipe.recipeSteps, recipe.recipeImages)
                }
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
                        // Например, навигация назад
                        requireActivity().onBackPressed()
                    }
                }
            }
        }
    }
}