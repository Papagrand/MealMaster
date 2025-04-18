package ru.point.setting_searched_product.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import ru.point.meal.R
import ru.point.meal.databinding.FragmentMealProductSearchBinding
import ru.point.meal.databinding.FragmentSettingSearchedProductBinding
import ru.point.setting_searched_product.di.DaggerSettingSearchedProductComponent
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import ru.point.setting_searched_product.di.SettingSearchedProductFragmentDepsProvider

class SettingSearchedProductFragment : BaseFragment<FragmentSettingSearchedProductBinding>() {

    @Inject
    lateinit var settingsSearchedProductViewModelFactory: SettingSearchedProductViewModelFactory

    private val viewModel: SettingSearchedProductViewModel by viewModels{
        settingsSearchedProductViewModelFactory
    }


    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingSearchedProductBinding = FragmentSettingSearchedProductBinding.inflate(inflater, container, false)

    override fun onAttach(context: Context) {
        DaggerSettingSearchedProductComponent.builder()
            .deps(SettingSearchedProductFragmentDepsProvider.deps )
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        val productId = arguments?.getString("productId")
        val mealId = arguments?.getString("mealId")

        if (productId != null)
            viewModel.getProductInformation(productId)

        collectUiState()
        collectUiEvent()


    }

    private fun collectUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.productData?.let { productInfo ->

                }
            }
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->

            }
        }
    }


}