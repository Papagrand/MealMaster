package ru.point.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import ru.point.splash.databinding.FragmentSplashBinding

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val viewModel: SplashViewModel by viewModels { SplashViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding =
        FragmentSplashBinding.inflate(inflater, container, false)

    @SuppressLint("HardwareIds")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()
        
        // Подписываемся на события навигации из SplashViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            val deviceId = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
            viewModel.updateDeviceId(deviceId)
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is SplashUiEvent.NavigateToLogin -> {
                        navigator.fromSplashFragmentToLoginFragment()
                    }
                    is SplashUiEvent.NavigateToCreateProfile -> {
                        navigator.fromSplashFragmentToOnboardingFragment()
                    }
                    is SplashUiEvent.NavigateToMain -> {
                        navigator.fromSplashFragmentToHomeProgressFragment()
                    }
                }
            }
        }
    }

}