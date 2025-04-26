package ru.point.setting_searched_product.di

import dagger.Component
import ru.point.api.daily_consumption_and_product.data.DailyConsumptionService
import ru.point.setting_searched_product.ui.SettingSearchedProductFragment
import kotlin.properties.Delegates.notNull

@Component(
    dependencies = [SettingSearchedProductFragmentDeps::class],
    modules = [SettingSearchedProductModule::class]
)
internal interface SettingSearchedProductComponent {
    fun inject(fragment: SettingSearchedProductFragment)

    @Component.Builder
    interface Builder {
        fun deps(settingSearchedProductFragmentDeps: SettingSearchedProductFragmentDeps): Builder
        fun build(): SettingSearchedProductComponent
    }
}

interface SettingSearchedProductFragmentDeps {
    val dailyConsumptionService: DailyConsumptionService
}

interface SettingSearchedProductFragmentDepsProvider {
    var deps: SettingSearchedProductFragmentDeps

    companion object :
        SettingSearchedProductFragmentDepsProvider by SettingSearchedProductFragmentDepsStore
}

object SettingSearchedProductFragmentDepsStore : SettingSearchedProductFragmentDepsProvider {
    override var deps: SettingSearchedProductFragmentDeps by notNull()
}