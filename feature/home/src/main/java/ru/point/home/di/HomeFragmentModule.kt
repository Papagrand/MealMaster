package ru.point.home.di

import dagger.Module
import dagger.Provides
import ru.point.api.daily_consumption_and_product.data.DailyConsumptionRepositoryImpl
import ru.point.api.daily_consumption_and_product.data.DailyConsumptionService
import ru.point.api.daily_consumption_and_product.domain.DailyConsumptionRepository
import ru.point.api.profile_data.data.ProfileDataRepositoryImpl
import ru.point.api.profile_data.data.ProfileDataService
import ru.point.api.profile_data.domain.ProfileDataRepository
import ru.point.home.domain.GetDailyConsumptionUseCase
import ru.point.home.domain.GetMainMaxNutrientsDataUseCase
import ru.point.home.ui.HomeProgressViewModelFactory

@Module
object HomeFragmentModule {

    @Provides
    fun provideProfileDataRepository(profileDataService: ProfileDataService): ProfileDataRepository{
        return ProfileDataRepositoryImpl(profileDataService)
    }

    @Provides
    fun provideGetDailyConsumptionRepository(dailyConsumptionService: DailyConsumptionService): DailyConsumptionRepository{
        return DailyConsumptionRepositoryImpl(dailyConsumptionService)
    }

    @Provides
    fun provideGetMainNutrientsDataUseCase(profileDataRepository: ProfileDataRepository) =
        GetMainMaxNutrientsDataUseCase(profileDataRepository)

    @Provides
    fun provideGetDailyConsumptionUseCase(dailyConsumptionRepository: DailyConsumptionRepository) =
        GetDailyConsumptionUseCase(dailyConsumptionRepository)

    @Provides
    fun provideHomeProgressViewModelFactory(
        getMainMaxNutrientsDataUseCase: GetMainMaxNutrientsDataUseCase,
        getDailyConsumptionUseCase: GetDailyConsumptionUseCase
    ) = HomeProgressViewModelFactory(
        getMainMaxNutrientsDataUseCaseProvider = { getMainMaxNutrientsDataUseCase },
        getDailyConsumptionUseCaseProvider = { getDailyConsumptionUseCase }
    )

}