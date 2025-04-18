package ru.point.meal.di

import dagger.Module
import dagger.Provides
import ru.point.api.meal.data.MealRepositoryImpl
import ru.point.api.meal.data.MealService
import ru.point.api.meal.domain.MealRepository
import ru.point.meal.domain.GetDailyConsumptionMealItemsUseCase
import ru.point.meal.domain.SearchProductsUseCase
import ru.point.meal.ui.MealProductSearchViewModelFactory


@Module
object MealProductSearchModule {

    @Provides
    fun provideProfileDataRepository(mealService: MealService): MealRepository{
        return MealRepositoryImpl(mealService)
    }

    @Provides
    fun provideGetDailyConsumptionMealItemsUseCase(mealRepository: MealRepository) =
        GetDailyConsumptionMealItemsUseCase(mealRepository)

    @Provides
    fun provideSearchProductsUseCase(mealRepository: MealRepository) =
        SearchProductsUseCase(mealRepository)

    @Provides
    fun provideMealProductSearchViewModelFactory(
        getDailyConsumptionMealItemsUseCase: GetDailyConsumptionMealItemsUseCase,
        searchProductsUseCase: SearchProductsUseCase
    ) = MealProductSearchViewModelFactory(
        getDailyConsumptionMealItemsUseCaseProvider = { getDailyConsumptionMealItemsUseCase },
        searchProductsUseCaseProvider = { searchProductsUseCase }
    )

}