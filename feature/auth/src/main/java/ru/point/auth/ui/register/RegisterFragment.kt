package ru.point.auth.ui.register

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ru.point.auth.R
import ru.point.auth.databinding.FragmentRegisterBinding
import ru.point.auth.ui.register.validators.EmailValidator
import ru.point.auth.ui.register.validators.LoginValidator
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

        // Обработка изменений в поле email с debounce
        binding.emailEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Отменяем предыдущую задачу, если пользователь продолжает ввод
                emailCheckJob?.cancel()
                // Скрываем ошибки и изображение до проверки
                binding.emailIsNotAvailableTextView.visibility = View.INVISIBLE
                binding.availableEmail.visibility = View.INVISIBLE
                binding.emailIsNotAvailableTextView.hint = "Некорректно введен Email"
                validEmail = ""
            }

            override fun afterTextChanged(s: Editable?) {
                emailCheckJob = lifecycleScope.launch {
                    delay(2000)  // Ждем 2000 мс после последнего ввода
                    val email = s.toString().trim()
                    // Локальная проверка формата email
                    if (!EmailValidator.isValid(email)) {
                        binding.emailIsNotAvailableTextView.apply {
                            hint = "Некорректно введен Email"
                            visibility = View.VISIBLE
                        }
                        binding.availableEmail.visibility = View.INVISIBLE
                    } else {
                        // Если локально формат корректный, отправляем запрос на сервер
                        registrationViewModel.checkEmail(email)
                            .catch { e ->
                                binding.emailIsNotAvailableTextView.apply {
                                    hint = "Ошибка проверки Email"
                                    visibility = View.VISIBLE
                                }
                            }
                            .collect { response ->
                                if (!response.available) {
                                    binding.emailIsNotAvailableTextView.apply {
                                        hint = response.message ?: "Email уже зарегистрирован"
                                        visibility = View.VISIBLE
                                    }
                                    binding.availableEmail.visibility = View.INVISIBLE
                                } else {
                                    binding.emailIsNotAvailableTextView.visibility = View.INVISIBLE
                                    binding.availableEmail.visibility = View.VISIBLE
                                    validEmail = email
                                }
                            }
                    }
                }
            }
        })

        binding.loginEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginCheckJob?.cancel()
                binding.loginIsNotAvailableTextView.visibility = View.INVISIBLE
                binding.availableLogin.visibility = View.INVISIBLE
                binding.loginIsNotAvailableTextView.hint =
                    "Формат логина A-z, 0-9 без знаков пунктуации"
                validLogin = ""
            }

            override fun afterTextChanged(s: Editable?) {
                loginCheckJob = lifecycleScope.launch {
                    delay(2000)  // Ждем 2000 мс после последнего ввода
                    val login = s.toString().trim()
                    if (!LoginValidator.isValid(login)) {
                        binding.loginIsNotAvailableTextView.apply {
                            hint = "Формат логина A-z, 0-9 без знаков пунктуации"
                            visibility = View.VISIBLE
                        }
                        binding.availableLogin.visibility = View.INVISIBLE
                    } else {
                        registrationViewModel.checkLogin(login)
                            .catch { e ->
                                binding.loginIsNotAvailableTextView.apply {
                                    hint = "Ошибка проверки Логин"
                                    visibility = View.VISIBLE
                                }
                            }
                            .collect { response ->
                                if (!response.available) {
                                    binding.loginIsNotAvailableTextView.apply {
                                        hint = response.message ?: "Логин уже зарегистрирован"
                                        visibility = View.VISIBLE
                                    }
                                    binding.availableLogin.visibility = View.INVISIBLE
                                } else {
                                    binding.loginIsNotAvailableTextView.visibility = View.INVISIBLE
                                    binding.availableLogin.visibility = View.VISIBLE
                                    validLogin = login
                                }
                            }
                    }
                }
            }
        })


        binding.passwordEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val inputType = binding.passwordEdittext.inputType
                val isPasswordHidden =
                    inputType and android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                val isPasswordVisible =
                    inputType and android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                if (isPasswordHidden || isPasswordVisible) {
                    binding.checkPassword.visibility = View.VISIBLE
                    binding.checkPassword.isClickable = true
                }

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.passwordIsNotAvailableTextView.visibility = View.INVISIBLE
//                binding.availablePassword.visibility = View.INVISIBLE
                binding.passwordIsNotAvailableTextView.hint =
                    "Пароль должен быть не короче 8 символов"
                validPassword = ""
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.passwordEdittext.text.isEmpty()) {
                    binding.checkPassword.visibility = View.INVISIBLE
                    binding.checkPassword.isClickable = false
                }
                passwordCheckJob = lifecycleScope.launch {
                    delay(2000) // Ждем 2000 мс после последнего ввода
                    val password = s.toString().trim()
                    // Проверяем длину пароля
                    if (!PasswordValidator.isLongEnough(password)) {
                        binding.passwordIsNotAvailableTextView.apply {
                            hint = "Пароль должен быть не менее 8 символов"
                            visibility = View.VISIBLE
                        }
                    }
                    // Если длина корректна, проверяем допустимые символы
                    else if (!PasswordValidator.containsOnlyAllowedCharacters(password)) {
                        binding.passwordIsNotAvailableTextView.apply {
                            hint = "Пароль содержит недопустимые символы"
                            visibility = View.VISIBLE
                        }
                    }
                    // Если обе проверки пройдены, скрываем ошибку и сохраняем пароль
                    else {
                        binding.passwordIsNotAvailableTextView.visibility = View.INVISIBLE
                    }
                }
            }
        })

        binding.checkPassword.setOnClickListener {
            if (binding.passwordEdittext.text.isNotEmpty()) {
                // Сохраняем текущие параметры
                val currentTypeface = binding.passwordEdittext.typeface
                val currentTextSize = binding.passwordEdittext.textSize

                val currentInputType = binding.passwordEdittext.inputType
                val variation = currentInputType and android.text.InputType.TYPE_MASK_VARIATION
                val isPasswordHidden =
                    variation == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                val isPasswordVisible =
                    variation == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                if (isPasswordHidden) {
                    // Переключаем на видимый пароль
                    binding.passwordEdittext.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    binding.checkPassword.setImageResource(R.drawable.password_close)
                } else if (isPasswordVisible) {
                    // Переключаем на скрытый пароль
                    binding.passwordEdittext.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    binding.checkPassword.setImageResource(R.drawable.password_open)
                }
                // Восстанавливаем сохранённые параметры
                binding.passwordEdittext.typeface = currentTypeface
                binding.passwordEdittext.setTextSize(
                    android.util.TypedValue.COMPLEX_UNIT_PX,
                    currentTextSize
                )
                // Перемещаем курсор в конец текста
                binding.passwordEdittext.setSelection(binding.passwordEdittext.text.length)
            }
        }




        binding.repeatPasswordEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val inputType = binding.repeatPasswordEdittext.inputType
                val isPasswordHidden =
                    inputType and android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                val isPasswordVisible =
                    inputType and android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                if (isPasswordHidden || isPasswordVisible) {
                    binding.checkRepeatPassword.visibility = View.VISIBLE
                    binding.checkRepeatPassword.isClickable = true
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Отменяем предыдущую задачу, если пользователь продолжает ввод
                repeatPasswordCheckJob?.cancel()
                // Скрываем сообщение об ошибке и устанавливаем начальный hint
                binding.repeatPasswordIsNotAvailableTextView.visibility = View.INVISIBLE
                binding.repeatPasswordIsNotAvailableTextView.hint = "Пароли не совпадают"
                validPassword = ""
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.repeatPasswordEdittext.text.isEmpty()) {
                    binding.checkRepeatPassword.visibility = View.INVISIBLE
                    binding.checkRepeatPassword.isClickable = false
                }
                repeatPasswordCheckJob = lifecycleScope.launch {
                    delay(2000) // Ждем 2000 мс после последнего ввода
                    val repeatPassword = s.toString()
                    // Проверяем, совпадает ли повторный пароль с валидированным паролем
                    if (repeatPassword != binding.passwordEdittext.text.toString()) {
                        binding.repeatPasswordIsNotAvailableTextView.apply {
                            binding.availablePassword.visibility = View.INVISIBLE
                            binding.availableRepeatPassword.visibility = View.INVISIBLE
                            hint = "Пароли не совпадают"
                            visibility = View.VISIBLE
                        }
                    } else {
                        if (repeatPassword.isNotEmpty()) {
                            binding.repeatPasswordIsNotAvailableTextView.visibility = View.INVISIBLE
                            binding.availablePassword.visibility = View.VISIBLE
                            binding.availableRepeatPassword.visibility = View.VISIBLE
                            validPassword = repeatPassword
                        }
                    }
                }
            }
        })

        binding.checkRepeatPassword.setOnClickListener {
            if (binding.repeatPasswordEdittext.text.isNotEmpty()) {
                // Сохраняем текущие параметры
                val currentTypeface = binding.repeatPasswordEdittext.typeface
                val currentTextSize = binding.repeatPasswordEdittext.textSize

                val currentInputType = binding.repeatPasswordEdittext.inputType
                val variation = currentInputType and android.text.InputType.TYPE_MASK_VARIATION
                val isPasswordHidden =
                    variation == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                val isPasswordVisible =
                    variation == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                if (isPasswordHidden) {
                    // Переключаем на видимый пароль
                    binding.repeatPasswordEdittext.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    binding.checkRepeatPassword.setImageResource(R.drawable.password_close)
                } else if (isPasswordVisible) {
                    // Переключаем на скрытый пароль
                    binding.repeatPasswordEdittext.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    binding.checkRepeatPassword.setImageResource(R.drawable.password_open)
                }
                // Восстанавливаем сохранённые параметры
                binding.repeatPasswordEdittext.typeface = currentTypeface
                binding.repeatPasswordEdittext.setTextSize(
                    android.util.TypedValue.COMPLEX_UNIT_PX,
                    currentTextSize
                )
                // Перемещаем курсор в конец текста
                binding.repeatPasswordEdittext.setSelection(binding.repeatPasswordEdittext.text.length)
            }
        }




        binding.buttonRegistration.setOnClickListener {
            if (validLogin != "" && validEmail != "" && validPassword != "")
            {
                // Запускаем корутину для сбора Flow
                lifecycleScope.launch {
                    registrationViewModel.register(validLogin, validEmail, validPassword)
                        .catch { e ->
                            // Логируем сообщение об ошибке
                            Log.e("RegistrationFlow", "Ошибка регистрации", e)

                            // Можно вывести Toast пользователю
                            Toast.makeText(
                                requireContext(),
                                "Ошибка регистрации: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .collect { response ->
                            if (response.success) {
                                // Если запрос выполнен успешно, переход в LoginFragment
                                Log.d(
                                    "RegistrationFlow",
                                    "Регистрация прошла успешно: ${response.message}"
                                )
                                navigator.fromRegisterFragmentToLoginFragment()
                            } else {
                                // Если сервер вернул success=false, показываем сообщение с response.message
                                Log.d("RegistrationFlow", "Ошибка регистрации: ${response.message}")
                                Toast.makeText(
                                    requireContext(),
                                    response.message ?: "Неизвестная ошибка",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    "Вы ввели не все данные, или в неправильном формате",
                    Toast.LENGTH_LONG
                ).show()
            }

        }


    }
}
