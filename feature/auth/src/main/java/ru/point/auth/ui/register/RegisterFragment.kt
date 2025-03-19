package ru.point.auth.ui.register

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.point.auth.R
import ru.point.auth.databinding.FragmentRegisterBinding
import ru.point.core.navigation.BottomBarManager
import ru.point.core.ui.BaseFragment
import javax.inject.Inject

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    @Inject
    lateinit var registrationViewModelFactory: RegistrationViewModel.Factory

    private val registrationViewModel: RegistrationViewModel by viewModels {
        registrationViewModelFactory
    }

    private var emailCheckJob: Job? = null
    private var loginCheckJob: Job? = null
    private var passwordCheckJob: Job? = null
    private var repeatPasswordCheckJob: Job? = null

    private var validEmail: String = ""
    private var validLogin: String = ""
    private var validPassword: String = ""

    override fun onAttach(context: Context) {
        // Создаем компонент Registration и внедряем зависимости
        DaggerRegisterComponent.builder()
            .deps(RegistrationDepsProvider.deps)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        (requireActivity() as BottomBarManager).hide()
        super.onViewStateRestored(savedInstanceState)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRegisterBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BottomBarManager).hide()

        binding.backToAuth.setOnClickListener {
            navigator.fromRegisterFragmentToLoginFragment()
        }

        // Передача изменений из EditText во ViewModel
        binding.emailEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                registrationViewModel.onEmailChanged(s.toString())
            }
        })

        binding.loginEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                registrationViewModel.onLoginChanged(s.toString())
            }
        })

        binding.passwordEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                registrationViewModel.onPasswordChanged(s.toString())
            }
        })

        binding.repeatPasswordEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                registrationViewModel.onRepeatPasswordChanged(s.toString())
            }
        })

        binding.checkPassword.setOnClickListener {
            registrationViewModel.togglePasswordVisibility()
        }
        binding.checkRepeatPassword.setOnClickListener {
            registrationViewModel.toggleRepeatPasswordVisibility()
        }

        binding.buttonRegistration.setOnClickListener {
            registrationViewModel.register()
        }

        // Подписка на UI-состояние для обновления ошибок и индикации загрузки
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registrationViewModel.uiState.collect { state ->
                    // Здесь предполагается, что в layout присутствуют TextView для отображения ошибок
                    binding.emailIsNotAvailableTextView.text = state.emailError ?: ""
                    binding.emailIsNotAvailableTextView.visibility = if (state.emailError != null) View.VISIBLE else View.INVISIBLE

                    binding.loginIsNotAvailableTextView.text = state.loginError ?: ""
                    binding.loginIsNotAvailableTextView.visibility = if (state.loginError != null) View.VISIBLE else View.INVISIBLE

                    binding.passwordIsNotAvailableTextView.text = state.passwordError ?: ""
                    binding.passwordIsNotAvailableTextView.visibility = if (state.passwordError != null) View.VISIBLE else View.INVISIBLE

                    binding.repeatPasswordIsNotAvailableTextView.text = state.repeatPasswordError ?: ""
                    binding.repeatPasswordIsNotAvailableTextView.visibility = if (state.repeatPasswordError != null) View.VISIBLE else View.INVISIBLE


                    if (state.isPasswordVisible) {
                        binding.passwordEdittext.inputType =
                            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.checkPassword.setImageResource(R.drawable.password_close)
                    } else {
                        binding.passwordEdittext.inputType =
                            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.checkPassword.setImageResource(R.drawable.password_open)
                    }
                    // Перемещаем курсор в конец текста
                    binding.passwordEdittext.setSelection(binding.passwordEdittext.text.length)

                    // Аналогично для repeat password
                    if (state.isRepeatPasswordVisible) {
                        binding.repeatPasswordEdittext.inputType =
                            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.checkRepeatPassword.setImageResource(R.drawable.password_close)
                    } else {
                        binding.repeatPasswordEdittext.inputType =
                            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.checkRepeatPassword.setImageResource(R.drawable.password_open)
                    }
                    binding.repeatPasswordEdittext.setSelection(binding.repeatPasswordEdittext.text.length)

                    // Можно добавить индикацию загрузки, например:
                    // binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                }
            }
        }

        // Подписка на одноразовые события (навигация, Toast)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registrationViewModel.uiEvent.collect { event ->
                    when (event) {
                        is RegisterUiEvent.NavigateToLogin -> {
                            navigator.fromRegisterFragmentToLoginFragment()
                        }
                        is RegisterUiEvent.ShowToast -> {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }


    }
}
