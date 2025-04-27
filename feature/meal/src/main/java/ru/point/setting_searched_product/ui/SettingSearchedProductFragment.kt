package ru.point.setting_searched_product.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.snackbar.Snackbar
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import ru.point.meal.databinding.FragmentSettingSearchedProductBinding
import ru.point.setting_searched_product.di.DaggerSettingSearchedProductComponent
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.point.setting_searched_product.di.SettingSearchedProductFragmentDepsProvider

class SettingSearchedProductFragment : BaseFragment<FragmentSettingSearchedProductBinding>() {

    @Inject
    lateinit var settingsSearchedProductViewModelFactory: SettingSearchedProductViewModelFactory

    private val viewModel: SettingSearchedProductViewModel by viewModels {
        settingsSearchedProductViewModelFactory
    }

    private var servingSizeJob: Job? = null
    private var caloriesJob: Job? = null

    private var isEditingWeight = false
    private var isEditingCalories = false
    private var isInputsInitialized = false


    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingSearchedProductBinding =
        FragmentSettingSearchedProductBinding.inflate(inflater, container, false)

    override fun onAttach(context: Context) {
        DaggerSettingSearchedProductComponent.builder()
            .deps(SettingSearchedProductFragmentDepsProvider.deps)
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
        setupTextWatchers()

        binding.addProductToMealButton.setOnClickListener{
            viewModel.addProductToMeal(mealId, productId)
            viewModel.navigateToMealSearchFragment()
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

                state.productData?.let { productInfo ->

                    if (!isInputsInitialized) {
                        isInputsInitialized = true
                        binding.errorContainer.isVisible = false

                        binding.productAppBarLayout.isVisible = true
                        binding.productScrollView.isVisible = true
                        binding.addProductToMealButton.isVisible = true

                        binding.productCollapsingToolbar.title = productInfo.productName
                        val backdropString = productInfo.productBackdrop
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
                            productInfo.productServingSizeDefault.toString()
                        )
                        binding.caloriesEditTextLayout.editText?.setText(
                            productInfo.productCalories.toString()
                        )

                    }else {

                        if (isEditingWeight) {
                            binding.caloriesEditTextLayout.editText
                                ?.setText(productInfo.productCalories.toString())
                        } else if (isEditingCalories) {
                            // юзер поменял калории → обновляем вес
                            binding.servingSizeEditTextLayout.editText
                                ?.setText(productInfo.productServingSizeDefault.toString())
                        }
                    }

                    binding.apply {
                        binding.minorProductDataText.text =
                            "Количество нутриентов в ${productInfo.productServingSizeDefault.toString()} гр. продукта:"
                        binding.productProteinValue.text = productInfo.productProtein.toString()
                        binding.productFatValue.text = productInfo.productFat.toString()
                        binding.productCarbohydratesValue.text =
                            productInfo.productCarbohydrates.toString()
                        productCaloriesMinorValue.text = productInfo.productCalories.toString()
                        productServingSizeMinorValue.text =
                            productInfo.productServingSizeDefault.toString()
                        productProteinMinorValue.text = productInfo.productProtein.toString()
                        productFatMinorValue.text = productInfo.productFat.toString()
                        productCarbohydratesMinorValue.text =
                            productInfo.productCarbohydrates.toString()
                        productDietaryFiberMinorValue.text =
                            productInfo.productDietaryFiber.toString()
                        productSugarsMinorValue.text = productInfo.productSugars.toString()
                        productStarchDextrinsMinorValue.text =
                            productInfo.productStarchDextrins.toString()
                        productPotassiumMinorValue.text = productInfo.productPotassium.toString()
                        productCalciumMinorValue.text = productInfo.productCalcium.toString()
                        productSiliconMinorValue.text = productInfo.productSilicon.toString()
                        productMagnesiumMinorValue.text = productInfo.productMagnesium.toString()
                        productSodiumMinorValue.text = productInfo.productSodium.toString()
                        productSulfurMinorValue.text = productInfo.productSulfur.toString()
                        productPhosphorusMinorValue.text = productInfo.productPhosphorus.toString()
                        productChlorineMinorValue.text = productInfo.productChlorine.toString()
                        productIronMinorValue.text = productInfo.productIron.toString()
                        productZincMinorValue.text = productInfo.productZinc.toString()
                        productOmega3MinorValue.text = productInfo.productOmega3.toString()
                        productOmega6MinorValue.text = productInfo.productOmega6.toString()
                        productVitaminAMinorValue.text = productInfo.productVitaminA.toString()
                        productVitaminB1MinorValue.text = productInfo.productVitaminB1.toString()
                        productVitaminB2MinorValue.text = productInfo.productVitaminB2.toString()
                        productVitaminB4MinorValue.text = productInfo.productVitaminB4.toString()
                        productVitaminCMinorValue.text = productInfo.productVitaminC.toString()
                        productVitaminDMinorValue.text = productInfo.productVitaminD.toString()
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


    private fun collectUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when(event){
                    is ProductDataUiEvent.ShowToast -> {
                        Snackbar
                            .make(binding.root, event.message, Snackbar.LENGTH_SHORT)
                            .show()
                    }

                    is ProductDataUiEvent.NavigateToMealProductSearch -> {
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