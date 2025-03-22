package ru.point.auth.ui.on_boarding

import dagger.Component
import ru.point.api.profile_creating.ProfileService
import kotlin.properties.Delegates.notNull

@Component(dependencies = [OnboardingDeps::class])
internal interface OnboardingComponent {
    fun inject(fragment: OnboardingFragment)

    @Component.Builder
    interface Builder {
        fun deps(onboardingDeps: OnboardingDeps): Builder
        fun build(): OnboardingComponent
    }
}


interface OnboardingDeps {
    val profileService: ProfileService
}

interface OnboardingDepsProvider {
    var deps: OnboardingDeps

    companion object : OnboardingDepsProvider by OnboardingDepsStore
}

object OnboardingDepsStore : OnboardingDepsProvider {
    override var deps: OnboardingDeps by notNull()
}