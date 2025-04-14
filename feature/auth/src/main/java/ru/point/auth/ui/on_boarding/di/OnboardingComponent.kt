package ru.point.auth.ui.on_boarding.di

import dagger.Component
import ru.point.api.profile_creating.data.ProfileCreateService
import ru.point.auth.ui.on_boarding.ui.OnboardingFragment
import kotlin.properties.Delegates.notNull

@Component(dependencies = [OnboardingDeps::class], modules = [OnboardingModule::class])
internal interface OnboardingComponent {
    fun inject(fragment: OnboardingFragment)

    @Component.Builder
    interface Builder {
        fun deps(onboardingDeps: OnboardingDeps): Builder
        fun build(): OnboardingComponent
    }
}


interface OnboardingDeps {
    val profileCreateService: ProfileCreateService
}

interface OnboardingDepsProvider {
    var deps: OnboardingDeps

    companion object : OnboardingDepsProvider by OnboardingDepsStore
}

object OnboardingDepsStore : OnboardingDepsProvider {
    override var deps: OnboardingDeps by notNull()
}