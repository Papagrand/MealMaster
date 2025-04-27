package ru.point.pick_meal.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import ru.point.recipe_information.di.DaggerRecipeInformationComponent
import ru.point.recipe_information.di.RecipeInformationDepsProvider
import ru.point.recipes.databinding.FragmentPickMealBottomSheetBinding
import ru.point.recipes.R
import ru.point.recipes.di.DaggerRecipesComponent
import ru.point.recipes.di.RecipesFragmentDepsProvider
import javax.inject.Inject


class PickMealBottomSheetFragment : BottomSheetDialogFragment() {


    @Inject
    lateinit var pickMealBottomSheetViewModelFactory: PickMealBottomSheetViewModelFactory

    private val viewModel: PickMealBottomSheetViewModel by viewModels {
        pickMealBottomSheetViewModelFactory
    }

    private var _binding: FragmentPickMealBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var userId: String
    private lateinit var productId: String
    private var servingSize: Double = 0.0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerRecipeInformationComponent.builder()
            .deps(RecipeInformationDepsProvider.deps)
            .build()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = requireArguments().getString(ARG_USER_ID)
            ?: throw IllegalStateException("userId is required")
        productId = requireArguments().getString(ARG_PRODUCT_ID)
            ?: throw IllegalStateException("productId is required")
        servingSize = requireArguments().getDouble(ARG_SERVING_SIZE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickMealBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMealIds(userId)

        collectUiState()
        collectUiEvent()

        binding.buttonBack.setOnClickListener { dismiss() }

        binding.buttonConfirm.setOnClickListener {
            val checkedId = binding.radioGroupPortions.checkedRadioButtonId
            if (checkedId == View.NO_ID) {
                viewModel.sendUiEvent(PickMealUiEvent.ShowToast("Выберите прием пищи"))
            } else {
                val selectedType = when (checkedId) {
                    R.id.radioButtonBreakfast -> "BREAKFAST"
                    R.id.radioButtonLunch -> "LUNCH"
                    R.id.radioButtonDinner -> "DINNER"
                    R.id.radioButtonSnack -> "SNACK"
                    else -> null
                }
                val mealInfo = viewModel.uiState.value.mealIdsData
                    ?.firstOrNull { it.mealType == selectedType }

                if (mealInfo != null) {
                    viewModel.addProductToMeal(
                        mealId = mealInfo.mealId,
                        productId = productId,
                        servingSize = servingSize
                    )
                } else {
                    viewModel.sendUiEvent(PickMealUiEvent.ShowToast("Некорректный выбор"))
                }
            }
        }

        // Устанавливаем дефолтный выбор
        if (binding.radioGroupPortions.checkedRadioButtonId == View.NO_ID) {
            binding.radioGroupPortions.check(R.id.radioButtonBreakfast)
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                } }
        }
    }

    private fun collectUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is PickMealUiEvent.ShowToast ->
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()

                    is PickMealUiEvent.NavigateBack -> dismiss()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dlg ->
            val bottomSheet =
                dlg.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                sheet.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                BottomSheetBehavior.from(sheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PickMealBottomSheet"

        private const val ARG_USER_ID = "userId"
        private const val ARG_PRODUCT_ID = "productId"
        private const val ARG_SERVING_SIZE = "servingSize"

        fun newInstance(
            userId: String,
            productId: String,
            servingSize: Double
        ): PickMealBottomSheetFragment =
            PickMealBottomSheetFragment().apply {
                arguments = bundleOf(
                    ARG_USER_ID to userId,
                    ARG_PRODUCT_ID to productId,
                    ARG_SERVING_SIZE to servingSize
                )
            }
    }
}

