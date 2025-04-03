package ru.point.recipe_information.di

import dagger.Module
import dagger.Provides
import ru.point.api.recipes.data.RecipeRepositoryImpl
import ru.point.api.recipes.data.RecipeService
import ru.point.api.recipes.domain.RecipeRepository
import ru.point.recipe_information.domain.GetFullRecipeUseCase
import ru.point.recipe_information.ui.RecipeInformationViewModelFactory

@Module
object RecipeInformationModule {

    @Provides
    fun provideRecipeRepository(service: RecipeService): RecipeRepository {
        return RecipeRepositoryImpl(service)
    }

    @Provides
    fun provideGetFullRecipeUseCase(repo: RecipeRepository) = GetFullRecipeUseCase(repo)

    @Provides
    fun provideRecipeViewModelFactory(
        getFullRecipeUseCase: GetFullRecipeUseCase
    ) = RecipeInformationViewModelFactory(
        getFullRecipeUseCaseProvider = { getFullRecipeUseCase }
    )
}
