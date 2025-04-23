package ru.point.meal.di

import dagger.Component
import ru.point.api.meal.data.MealService
import ru.point.meal.ui.MealProductSearchFragment
import ru.point.meal.ui.bottom_sheets.UpdateDeleteItemBottomSheetFragment

import kotlin.properties.Delegates.notNull

@Component(
    dependencies = [MealProductSearchFragmentDeps::class],
    modules = [MealProductSearchModule::class]
)
internal interface MealProductSearchComponent {
    fun inject(fragment: MealProductSearchFragment)
    fun inject(bottomSheet: UpdateDeleteItemBottomSheetFragment)

    @Component.Builder
    interface Builder {
        fun deps(mealProductSearch: MealProductSearchFragmentDeps): Builder
        fun build(): MealProductSearchComponent
    }
}

interface MealProductSearchFragmentDeps {
    val mealService: MealService
}

interface MealProductSearchFragmentDepsProvider {
    var deps: MealProductSearchFragmentDeps
    companion object : MealProductSearchFragmentDepsProvider by MealProductSearchFragmentDepsStore
}

object MealProductSearchFragmentDepsStore : MealProductSearchFragmentDepsProvider {
    override var deps: MealProductSearchFragmentDeps by notNull()
}