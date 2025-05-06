package ru.point.recipes.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import jakarta.inject.Inject
import ru.point.recipes.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import ru.point.recipes.databinding.FragmentRecipesBinding
import ru.point.recipes.di.DaggerRecipesComponent
import ru.point.recipes.di.RecipesFragmentDepsProvider
import ru.point.recipes.ui.adapters.SearchedRecipesAdapter
import ru.point.recipes.ui.bottom_sheets.RecipeSearchSettingBottomSheetFragment

class RecipesFragment : BaseFragment<FragmentRecipesBinding>() {

    @Inject
    lateinit var recipesViewModelFactory: RecipesViewModelFactory

    private val viewModel: RecipesViewModel by viewModels {
        recipesViewModelFactory
    }

    private val searchedAdapter by lazy {
        SearchedRecipesAdapter { recipeId, productId ->
            val bundle = Bundle().apply {
                putString("recipeId", recipeId)
                putString("productId", productId)
            }
            navigator.fromRecipeFragmentToRecipeInformationFragment(bundle)
        }
    }

    private var searchJob: Job? = null

    override fun onAttach(context: Context) {
        DaggerRecipesComponent.builder()
            .deps(RecipesFragmentDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecipesBinding = FragmentRecipesBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).show()

        binding.searchedRecipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchedAdapter
        }

        binding.searchRecipesEditText.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) {
                searchedAdapter.clear()
                binding.motionLayout.transitionToEnd()
            }
        }

        if (viewModel.searchSettings.value.searchText != null) {
            binding.motionLayout.setTransitionDuration(0)
            binding.motionLayout.transitionToEnd()
            binding.motionLayout.setTransitionDuration(200)
        }

        binding.searchRecipesEditText.doOnTextChanged { text, _, _, _ ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(1000)
                val query = text?.toString().orEmpty()
                if (query.isNotBlank()) {
                    viewModel.getSearchedRecipes(recipeName = query)
                    viewModel.updateSearchSettings(
                        viewModel.searchSettings.value.copy(searchText = query)
                    )
                }
            }
        }

        binding.searchTextInputLayout.setEndIconOnClickListener {
            val current = viewModel.searchSettings.value
            RecipeSearchSettingBottomSheetFragment
                .newInstance(current)
                .show(childFragmentManager, RecipeSearchSettingBottomSheetFragment.TAG)
        }

        childFragmentManager.setFragmentResultListener(
            RecipeSearchSettingBottomSheetFragment.RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val updatedSettings = viewModel.searchSettings.value.copy(
                cookingTime = bundle.getInt("arg_cooking_time"),
                difficulty = bundle.getInt("arg_difficulty"),
                isVegan = bundle.getBoolean("arg_is_vegan"),
                maxCalories = bundle.getDouble("arg_max_calories")
            )
            viewModel.updateSearchSettings(updatedSettings)

            updatedSettings.searchText?.takeIf { it.isNotBlank() }?.let { query ->
                viewModel.getSearchedRecipes(recipeName = query)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.motionLayout.currentState == R.id.start) {
                        navigator.fromMealFragmentToHomeProgressFragment()
                    }
                    binding.searchRecipesEditText.text?.clear()
                    searchedAdapter.submitList(emptyList())
                    viewModel.updateSearchSettings(
                        viewModel.searchSettings.value.copy(searchText = null)
                    )
                    binding.searchRecipesEditText.clearFocus()
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
                state.searchedData?.let { results ->
                    searchedAdapter.submitList(results)
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