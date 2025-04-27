package ru.point.recipe_information.di

import dagger.Component
import ru.point.api.daily_consumption_and_product.data.DailyConsumptionService
import ru.point.api.recipes.data.RecipeService
import ru.point.pick_meal.ui.PickMealBottomSheetFragment
import ru.point.recipe_information.ui.RecipeInformationFragment
import kotlin.properties.Delegates.notNull

@Component(
    dependencies = [RecipeInformationDeps::class],
    modules = [RecipeInformationModule::class]
)
internal interface RecipeInformationComponent {
    fun inject(fragment: RecipeInformationFragment)
    fun inject(bottomSheetFragment: PickMealBottomSheetFragment)

    @Component.Builder
    interface Builder {
        fun deps(recipeInformationDeps: RecipeInformationDeps): Builder
        fun build(): RecipeInformationComponent
    }
}

interface RecipeInformationDeps {
    val recipeService: RecipeService
    val dailyConsumptionService: DailyConsumptionService
}

interface RecipeInformationDepsProvider {
    var deps: RecipeInformationDeps
    companion object : RecipeInformationDepsProvider by RecipeInformationDepsStore
}

object RecipeInformationDepsStore : RecipeInformationDepsProvider {
    override var deps: RecipeInformationDeps by notNull()
}


