package ru.point.auth.ui.login

import dagger.Component
import ru.point.api.login.LoginService
import kotlin.properties.Delegates.notNull

@Component(dependencies = [LoginDeps::class])
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