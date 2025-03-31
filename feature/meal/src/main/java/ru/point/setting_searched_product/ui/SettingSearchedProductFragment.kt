package ru.point.setting_searched_product.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import ru.point.meal.R
import ru.point.meal.databinding.FragmentMealProductSearchBinding
import ru.point.meal.databinding.FragmentSettingSearchedProductBinding

class SettingSearchedProductFragment : BaseFragment<FragmentSettingSearchedProductBinding>() {
    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingSearchedProductBinding = FragmentSettingSearchedProductBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

    }



}