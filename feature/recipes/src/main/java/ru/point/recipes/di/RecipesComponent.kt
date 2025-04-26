package ru.point.recipes.di

import dagger.Component
import ru.point.api.recipes.data.RecipeService
import ru.point.recipes.ui.RecipesFragment
import kotlin.properties.Delegates.notNull

@Component(
    dependencies = [RecipesFragmentDeps::class],
    modules = [RecipesModule::class]
)
internal interface RecipesComponent {
    fun inject(fragment: RecipesFragment)

    @Component.Builder
    interface Builder {
        fun deps(recipesFragmentDeps: RecipesFragmentDeps): Builder
        fun build(): RecipesComponent
    }
}

interface RecipesFragmentDeps {
    val recipeService: RecipeService
}

interface RecipesFragmentDepsProvider {
    var deps: RecipesFragmentDeps

    companion object : RecipesFragmentDepsProvider by RecipesFragmentDepsStore
}

object RecipesFragmentDepsStore : RecipesFragmentDepsProvider {
    override var deps: RecipesFragmentDeps by notNull()
}