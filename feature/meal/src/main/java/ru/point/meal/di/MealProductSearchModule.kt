package ru.point.meal.di

import dagger.Module
import dagger.Provides
import ru.point.api.meal.data.MealRepositoryImpl
import ru.point.api.meal.data.MealService
import ru.point.api.meal.domain.MealRepository
import ru.point.meal.domain.CalculateItemDataUseCase
import ru.point.meal.domain.DeleteItemFromMealUseCase
import ru.point.meal.domain.GetDailyConsumptionMealItemsUseCase
import ru.point.meal.domain.SearchProductsUseCase
import ru.point.meal.domain.UpdateItemMealUseCase
import ru.point.meal.ui.MealProductSearchViewModelFactory
import ru.point.meal.ui.UpdateDeleteItemViewModelFactory


@Module
object MealProductSearchModule {

    @Provides
    fun provideProfileDataRepository(mealService: MealService): MealRepository {
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

    @Provides
    fun provideUpdateItemMealUseCase(mealRepository: MealRepository) =
        UpdateItemMealUseCase(mealRepository)

    @Provides
    fun provideCalculateItemDataUseCase() =
        CalculateItemDataUseCase()

    @Provides
    fun deleteItemFromMealUseCase(mealRepository: MealRepository) =
        DeleteItemFromMealUseCase(mealRepository)

    @Provides
    fun provideUpdateDeleteItemViewModelFactory(
        updateItemMealUseCase: UpdateItemMealUseCase,
        calculateItemDataUseCase: CalculateItemDataUseCase,
        deleteItemFromMealUseCase: DeleteItemFromMealUseCase
    ) = UpdateDeleteItemViewModelFactory(
        updateItemMealUseCaseProvider = { updateItemMealUseCase },
        calculateItemDataUseCaseProvider = { calculateItemDataUseCase },
        deleteItemFromMealUseCaseProvider = { deleteItemFromMealUseCase }
    )

}