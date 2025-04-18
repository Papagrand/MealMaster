package ru.point.setting_searched_product.di

import dagger.Module
import dagger.Provides
import ru.point.api.daily_consumption_and_product.data.DailyConsumptionRepositoryImpl
import ru.point.api.daily_consumption_and_product.data.DailyConsumptionService
import ru.point.api.daily_consumption_and_product.domain.DailyConsumptionRepository
import ru.point.setting_searched_product.domain.GetProductInformationUseCase
import ru.point.setting_searched_product.ui.SettingSearchedProductViewModelFactory

@Module
object SettingSearchedProductModule {

    @Provides
    fun provideDailyConsumptionRepository(dailyConsumptionService: DailyConsumptionService) : DailyConsumptionRepository{
        return DailyConsumptionRepositoryImpl(dailyConsumptionService)
    }

    @Provides
    fun provideGetProductInformationUseCase(dailyConsumptionRepository: DailyConsumptionRepository) =
        GetProductInformationUseCase(dailyConsumptionRepository)

    @Provides
    fun provideSettingSearchedProductViewModelFactory(
        getProductInformationUseCase: GetProductInformationUseCase
    ) = SettingSearchedProductViewModelFactory(
        getProductInformationUseCaseProvider = { getProductInformationUseCase }
    )
}