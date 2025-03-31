package ru.point.meal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.appbar.AppBarLayout
import ru.point.core.navigation.BottomBarManager
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import ru.point.core.ui.BaseFragment
import ru.point.meal.databinding.FragmentMealProductSearchBinding

class MealProductSearchFragment : BaseFragment<FragmentMealProductSearchBinding>() {

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMealProductSearchBinding = FragmentMealProductSearchBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        binding.searchEditText.doOnTextChanged { text, start, count, after ->
            binding.motionLayout.transitionToEnd()
        }

        binding.searchEditText.doAfterTextChanged {
            val text = binding.searchEditText.text.toString()
            if (text.isEmpty()){
                binding.motionLayout.transitionToStart()
            }
        }

    }
}