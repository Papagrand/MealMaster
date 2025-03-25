package ru.point.auth.ui.login.di

import dagger.Component
import ru.point.api.login.data.LoginService
import ru.point.auth.ui.login.ui.LoginFragment
import kotlin.properties.Delegates.notNull

@Component(
    dependencies = [LoginDeps::class],
    modules = [LoginModule::class]
)
internal interface LoginComponent {
    fun inject(fragment: LoginFragment)

    @Component.Builder
    interface Builder {
        fun deps(loginDeps: LoginDeps): Builder
        fun build(): LoginComponent
    }
}

interface LoginDeps {
    val loginService: LoginService
}

interface LoginDepsProvider {
    var deps: LoginDeps

    companion object : LoginDepsProvider by LoginDepsStore
}

object LoginDepsStore : LoginDepsProvider {
    override var deps: LoginDeps by notNull()
}