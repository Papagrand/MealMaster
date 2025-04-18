package ru.point.home.di

import dagger.Component
import ru.point.api.daily_consumption_and_product.data.DailyConsumptionService
import ru.point.api.profile_data.data.ProfileDataService
import ru.point.home.ui.HomeProgressFragment
import kotlin.properties.Delegates.notNull

@Component(
    dependencies = [HomeFragmentDeps::class],
    modules = [HomeFragmentModule::class]
)
internal interface HomeFragmentComponent {
    fun inject(fragment: HomeProgressFragment)

    @Component.Builder
    interface Builder {
        fun deps(homeFragmentDeps: HomeFragmentDeps): Builder
        fun build(): HomeFragmentComponent
    }
}


interface HomeFragmentDeps {
    val profileDataService: ProfileDataService
    val dailyConsumptionService: DailyConsumptionService
}

interface HomeFragmentDepsProvider {
    var deps: HomeFragmentDeps
    companion object : HomeFragmentDepsProvider by HomeFragmentDepsStore
}

object HomeFragmentDepsStore : HomeFragmentDepsProvider {
    override var deps: HomeFragmentDeps by notNull()
}