package ru.point.meal.ui.bottom_sheets

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import ru.point.meal.R
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.point.meal.databinding.FragmentUpdateDeleteItemBottomSheetBinding
import ru.point.meal.di.DaggerMealProductSearchComponent
import ru.point.meal.di.MealProductSearchFragmentDepsProvider
import ru.point.meal.ui.UpdateDeleteItemUiEvent
import ru.point.meal.ui.UpdateDeleteItemViewModel
import ru.point.meal.ui.UpdateDeleteItemViewModelFactory

class UpdateDeleteItemBottomSheetFragment : BottomSheetDialogFragment() {

    private var servingSizeJob: Job? = null
    private var caloriesJob: Job? = null

    private var isEditingWeight = false
    private var isEditingCalories = false
    private var isInputsInitialized = false

    companion object {
        const val TAG = "UpdateDeleteItemBottomSheet"
        private const val ARG_ITEM_ID = "arg_item_id"
        private const val ARG_SERVING_SIZE = "arg_serving_size"
        private const val ARG_CALORIES = "arg_calories"

        fun newInstance(
            itemId: String,
            servingSize: Double,
            calories: Double
        ): UpdateDeleteItemBottomSheetFragment {
            return UpdateDeleteItemBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_ID, itemId)
                    putDouble(ARG_SERVING_SIZE, servingSize)
                    putDouble(ARG_CALORIES, calories)
                }
            }
        }
    }

    private var _binding: FragmentUpdateDeleteItemBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdateDeleteItemViewModel by viewModels { updateDeleteItemViewModelFactory }

    private lateinit var itemId: String
    private var servingSize: Double = 0.0
    private var calories: Double = 0.0

    @Inject
    lateinit var updateDeleteItemViewModelFactory: UpdateDeleteItemViewModelFactory

    override fun onAttach(context: Context) {
        DaggerMealProductSearchComponent.builder()
            .deps(MealProductSearchFragmentDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemId = it.getString(ARG_ITEM_ID)!!
            servingSize = it.getDouble(ARG_SERVING_SIZE)
            calories = it.getDouble(ARG_CALORIES)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentUpdateDeleteItemBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e(
            "check_bottom_sheet",
            "itemId ${itemId}, servingSize ${servingSize.toString()}, calories ${calories.toString()}"
        )

        viewModel.insertCurrentData(itemId, servingSize, calories)

        collectUiState()
        collectUiEvent()
        setupTextWatchers()

        binding.buttonBack.setOnClickListener { dismiss() }

        binding.buttonConfirm.setOnClickListener {
            viewModel.updateItemMealServingSize(servingSize)
        }

        binding.deleteItemButton.setOnClickListener {
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.MyAlertDialogStyle
            )
                .setTitle("Удаление продукта")
                .setMessage("Вы уверены, что хотите удалить продукт?")
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Удалить") { dialog, _ ->
                    viewModel.deleteItemFromMeal()
                    dialog.dismiss()
                }
                .show()
        }

    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.itemData?.let { itemInfo ->
                    if (!isInputsInitialized) {
                        isInputsInitialized = true
                        binding.servingSizeEditTextLayout.editText?.setText(
                            itemInfo.currentServingSize.toString()
                        )
                        binding.caloriesEditTextLayout.editText?.setText(
                            itemInfo.currentCalories.toString()
                        )
                    } else {
                        if (isEditingWeight) {
                            binding.caloriesEditTextLayout.editText
                                ?.setText(itemInfo.currentCalories.toString())
                        } else if (isEditingCalories) {
                            binding.servingSizeEditTextLayout.editText
                                ?.setText(itemInfo.currentServingSize.toString())
                        }
                    }
                }
            }
        }
    }


    private fun collectUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when(event){
                    is UpdateDeleteItemUiEvent.ShowSneaker -> {
                        Snackbar
                            .make(binding.root, event.message, Snackbar.LENGTH_SHORT)
                            .show()

                        parentFragmentManager.setFragmentResult(
                            "itemUpdated",
                            bundleOf(ARG_ITEM_ID to itemId)
                        )

                        dismiss()
                    }
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



    override fun onStart() {
        super.onStart()
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { sheet ->
                sheet.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                BottomSheetBehavior.from(sheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}