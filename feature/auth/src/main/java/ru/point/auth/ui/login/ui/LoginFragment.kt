package ru.point.auth.ui.login.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.point.auth.databinding.FragmentLoginBinding
import ru.point.auth.ui.login.di.DaggerLoginComponent
import ru.point.auth.ui.login.di.LoginDepsProvider
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import javax.inject.Inject

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    @Inject
    lateinit var loginViewModelFactory: LoginViewModelFactory

    private val loginViewModel: LoginViewModel by activityViewModels{
        loginViewModelFactory
    }


    override fun onAttach(context: Context) {
        DaggerLoginComponent.builder()
            .deps(LoginDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLoginBinding.inflate(inflater, container, false)


    @SuppressLint("HardwareIds")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        binding.buttonRegistrationInAuth.setOnClickListener {
            navigator.fromLoginFragmentToRegisterFragment()
        }


        binding.loginEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.loginNotFoundTextView.visibility = View.INVISIBLE
            }
            override fun afterTextChanged(s: Editable?) {
                loginViewModel.onLoginChanged(s.toString())
            }
        })


        binding.passwordEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                loginViewModel.onPasswordChanged(s.toString())
            }
        })


        binding.buttonAuth.setOnClickListener {
            val deviceId = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
            loginViewModel.login(deviceId)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiState.collect { state ->
                    if (state.loginError != null) {
                        binding.loginNotFoundTextView.apply {
                            text = state.loginError
                            isVisible = true
                        }
                    } else {
                        binding.loginNotFoundTextView.isVisible = false
                    }

                    // Отображение ошибки для поля пароля
                    if (state.passwordError != null) {
                        binding.passwordIsEmptyTextView.apply {
                            text = state.passwordError
                            isVisible = true
                        }
                    } else {
                        binding.passwordIsEmptyTextView.isVisible = false
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiEvent.collect { event ->
                    when (event) {
                        is LoginUiEvent.NavigateToOnboarding -> {

                            navigator.fromLoginFragmentToOnboardingFragment()
                        }
                        is LoginUiEvent.NavigateToHomeProgress ->{
                            navigator.fromLoginFragmentToHomeProgressFragment()
                        }
                        is LoginUiEvent.ShowToast -> {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }


    }


}