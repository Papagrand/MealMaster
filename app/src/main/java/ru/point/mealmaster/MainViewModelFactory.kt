package ru.point.mealmaster

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.point.api.login.data.LoginService
import ru.point.core_data.SessionRepository

class MainViewModelFactory(
    private val context: Context,
    private val repo: SessionRepository,
    private val loginService: LoginService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == MainViewModel::class.java)
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(context, repo, loginService) as T
    }
}