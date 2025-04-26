package ru.point.recipes.di

import dagger.Module
import dagger.Provides
import ru.point.api.recipes.data.RecipeRepositoryImpl
import ru.point.api.recipes.data.RecipeService
import ru.point.api.recipes.domain.RecipeRepository
import ru.point.recipes.domain.GetSearchedRecipesUseCase
import ru.point.recipes.ui.RecipesViewModelFactory

@Module
object RecipesModule {

    @Provides
    fun provideRecipeRepository(recipeService: RecipeService): RecipeRepository {
        return RecipeRepositoryImpl(recipeService)
    }

    @Provides
    fun provideGetSearchedRecipesUseCase(recipeRepository: RecipeRepository)=
        GetSearchedRecipesUseCase(recipeRepository)

    @Provides
    fun provideRecipeViewModelFactory(
        getSearchedRecipesUseCase: GetSearchedRecipesUseCase
    ) = RecipesViewModelFactory(
        getSearchedRecipesUseCaseProvider = { getSearchedRecipesUseCase }
    )

}