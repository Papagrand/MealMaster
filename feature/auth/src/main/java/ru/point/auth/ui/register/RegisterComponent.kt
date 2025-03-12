package ru.point.auth.ui.register

import dagger.Component
import ru.point.api.registration.RegistrationService
import kotlin.properties.Delegates.notNull

@Component(dependencies = [RegistrationDeps::class])
internal interface RegisterComponent {
    fun inject(fragment: RegisterFragment)

    @Component.Builder
    interface Builder {
        fun deps(registrationDeps: RegistrationDeps):Builder
        fun build():RegisterComponent
    }
}

interface RegistrationDeps {
    val registerService: RegistrationService
}

interface RegistrationDepsProvider {
    var deps: RegistrationDeps

    companion object : RegistrationDepsProvider by RegistrationDepsStore
}

object RegistrationDepsStore : RegistrationDepsProvider {
    override var deps: RegistrationDeps by notNull()
}