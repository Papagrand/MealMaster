package ru.point.profile.di

import dagger.Component
import ru.point.api.profile_data.data.ProfileDataService
import ru.point.profile.ui.profile.ProfileFragment
import kotlin.properties.Delegates.notNull


@Component(
    dependencies = [ProfileDeps::class],
    modules = [ProfileModule::class]
)
internal interface ProfileComponent {
    fun inject(fragment: ProfileFragment)

    @Component.Builder
    interface Builder {
        fun deps(profileDeps: ProfileDeps): Builder
        fun build(): ProfileComponent
    }
}

interface ProfileDeps {
    val profileDataService: ProfileDataService
}

interface ProfileDepsProvider {
    var deps: ProfileDeps
    companion object : ProfileDepsProvider by ProfileDepsStore
}

object ProfileDepsStore : ProfileDepsProvider {
    override var deps: ProfileDeps by notNull()
}