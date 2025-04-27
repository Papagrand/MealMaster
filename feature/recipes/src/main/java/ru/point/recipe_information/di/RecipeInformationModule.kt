package ru.point.recipe_information.di

import dagger.Module
import dagger.Provides
import ru.point.api.daily_consumption_and_product.data.DailyConsumptionRepositoryImpl
import ru.point.api.daily_consumption_and_product.data.DailyConsumptionService
import ru.point.api.daily_consumption_and_product.domain.DailyConsumptionRepository
import ru.point.api.meal.domain.MealRepository
import ru.point.api.recipes.data.RecipeRepositoryImpl
import ru.point.api.recipes.data.RecipeService
import ru.point.api.recipes.domain.RecipeRepository
import ru.point.pick_meal.ui.PickMealBottomSheetViewModelFactory
import ru.point.recipe_information.domain.AddRecipePortionToMealUseCase
import ru.point.recipe_information.domain.CalculateRecipeDataUseCase
import ru.point.recipe_information.domain.GetFullRecipeUseCase
import ru.point.recipe_information.domain.GetMealIdsUseCase
import ru.point.recipe_information.ui.RecipeInformationViewModelFactory

@Module
object RecipeInformationModule {

    @Provides
    fun provideRecipeRepository(service: RecipeService): RecipeRepository {
        return RecipeRepositoryImpl(service)
    }

    @Provides
    fun provideDailyConsumptionRepository(service: DailyConsumptionService): DailyConsumptionRepository{
        return DailyConsumptionRepositoryImpl(service)
    }

    @Provides
    fun provideGetFullRecipeUseCase(repo: RecipeRepository) = GetFullRecipeUseCase(repo)

    @Provides
    fun provideCalculateRecipeDataUseCase() =
        CalculateRecipeDataUseCase()

    @Provides
    fun provideGetMealIdsUseCase(repo: RecipeRepository) =
        GetMealIdsUseCase(repo)

    @Provides
    fun provideAddRecipePortionToMealUseCase(dailyConsumptionRepository: DailyConsumptionRepository) =
        AddRecipePortionToMealUseCase(dailyConsumptionRepository)

    @Provides
    fun provideRecipeViewModelFactory(
        getFullRecipeUseCase: GetFullRecipeUseCase,
        calculateRecipeDataUseCase: CalculateRecipeDataUseCase
    ) = RecipeInformationViewModelFactory(
        getFullRecipeUseCaseProvider = { getFullRecipeUseCase },
        calculateRecipeDataUseCaseProvider = { calculateRecipeDataUseCase }
    )

    @Provides
    fun providePickMealBottomSheetViewModelFactory(
        getMealIdsUseCase: GetMealIdsUseCase,
        addRecipePortionToMealUseCase: AddRecipePortionToMealUseCase
    ) = PickMealBottomSheetViewModelFactory(
        getMealIdsUseCaseProvider = { getMealIdsUseCase },
        addRecipePortionToMealUseCaseProvider = { addRecipePortionToMealUseCase }
    )
}
