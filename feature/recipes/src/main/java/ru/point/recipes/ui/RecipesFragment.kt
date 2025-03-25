package ru.point.recipes.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import ru.point.recipes.R
import ru.point.recipes.databinding.FragmentRecipesBinding

class RecipesFragment : BaseFragment<FragmentRecipesBinding>() {

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecipesBinding = FragmentRecipesBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).show()
    }
}