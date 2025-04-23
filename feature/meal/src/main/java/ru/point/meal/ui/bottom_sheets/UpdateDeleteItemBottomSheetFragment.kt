package ru.point.meal.ui.bottom_sheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jakarta.inject.Inject
import ru.point.meal.databinding.FragmentUpdateDeleteItemBottomSheetBinding
import ru.point.meal.di.DaggerMealProductSearchComponent
import ru.point.meal.di.MealProductSearchFragmentDepsProvider
import ru.point.meal.ui.UpdateDeleteItemViewModel
import ru.point.meal.ui.UpdateDeleteItemViewModelFactory

class UpdateDeleteItemBottomSheetFragment : BottomSheetDialogFragment() {


    private var _binding: FragmentUpdateDeleteItemBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdateDeleteItemViewModel by viewModels { updateDeleteItemViewModelFactory }

    @Inject
    lateinit var updateDeleteItemViewModelFactory: UpdateDeleteItemViewModelFactory

    override fun onAttach(context: Context) {
        DaggerMealProductSearchComponent.builder()
            .deps(MealProductSearchFragmentDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentUpdateDeleteItemBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}