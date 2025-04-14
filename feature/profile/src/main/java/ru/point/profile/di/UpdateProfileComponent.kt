package ru.point.profile.di

import dagger.Component
import ru.point.api.profile_data.data.ProfileDataService
import ru.point.profile.ui.update_profile_information.UpdateProfileInformationFragment
import kotlin.properties.Delegates.notNull

@Component(
    dependencies = [UpdateProfileDeps::class],
    modules = [UpdateProfileModule::class]
)
internal interface UpdateProfileComponent {
    fun inject(fragment: UpdateProfileInformationFragment)

    @Component.Builder
    interface Builder {
        fun deps(updateProfileDeps: UpdateProfileDeps): Builder
        fun build(): UpdateProfileComponent
    }
}

interface UpdateProfileDeps {
    val profileDataService: ProfileDataService
}

interface UpdateProfileDepsProvider {
    var deps: UpdateProfileDeps
    companion object : UpdateProfileDepsProvider by UpdateProfileDepsStore
}

object UpdateProfileDepsStore : UpdateProfileDepsProvider {
    override var deps: UpdateProfileDeps by notNull()
}