package ru.point.meal.di

import dagger.Component
import ru.point.api.meal.data.MealService
import ru.point.meal.ui.MealProductSearchFragment

import kotlin.properties.Delegates.notNull

@Component(
    dependencies = [MealProductSearchFragmentDeps::class],
    modules = [MealProductSearchModule::class]
)
internal interface MealProductSearchComponent {
    fun inject(fragment: MealProductSearchFragment)

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