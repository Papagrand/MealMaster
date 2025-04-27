package ru.point.api.daily_consumption_and_product.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DailyConsumptionService {
    @GET("/user/daily_consumption")
    suspend fun getDailyConsumption(
        @Query("userId") userId: String,
        @Query("date") date: String
    ): Response<DailyConsumptionSuccessResponse<DailyConsumptionDataResponse>>

    @GET("/products/getById")
    suspend fun getProductById(
        @Query("productId") productId: String
    ): Response<ProductSuccessResponse<ProductDataResponse>>

    @POST("/user/daily_consumption/addFoodItem")
    suspend fun addProductToMeal(
        @Body request: AddProductToMealRequest
    ): Response<AddProductToMealResponse>

}

@Serializable
data class DailyConsumptionSuccessResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class DailyConsumptionDataResponse(
    val consumptionId: String,
    val userId: String,
    val date: String,
    val totalCalories: Double,
    val totalBreakfastCalories: Double,
    val totalLunchCalories: Double,
    val totalDinnerCalories: Double,
    val totalSnackCalories: Double,
    val totalProteins: Double,
    val totalCarbohydrates: Double,
    val totalFats: Double,
    val totalDietaryFiber: Double,
    val totalSugars: Double,
    val totalStarchDextrins: Double,
    val totalPotassium: Double,
    val totalCalcium: Double,
    val totalSilicon: Double,
    val totalMagnesium: Double,
    val totalSodium: Double,
    val totalSulfur: Double,
    val totalPhosphorus: Double,
    val totalChlorine: Double,
    val totalIron: Double,
    val totalZinc: Double,
    val totalOmega3: Double,
    val totalOmega6: Double,
    val totalVitaminA: Double,
    val totalVitaminB1: Double,
    val totalVitaminB2: Double,
    val totalVitaminB4: Double,
    val totalVitaminC: Double,
    val totalVitaminD: Double
)

@Serializable
data class ProductSuccessResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class ProductDataResponse(
    val productId: String,
    val productName: String,
    val productCalories: Double,
    val productServingSizeDefault: Double,
    val productProtein: Double,
    val productFat: Double,
    val productCarbohydrates: Double,
    val productDietaryFiber: Double,
    val productSugars: Double,
    val productStarchDextrins: Double,
    val productPotassium: Double,
    val productCalcium: Double,
    val productSilicon: Double,
    val productMagnesium: Double,
    val productSodium: Double,
    val productSulfur: Double,
    val productPhosphorus: Double,
    val productChlorine: Double,
    val productIron: Double,
    val productZinc: Double,
    val productOmega3: Double,
    val productOmega6: Double,
    val productVitaminA: Double,
    val productVitaminB1: Double,
    val productVitaminB2: Double,
    val productVitaminB4: Double,
    val productVitaminC: Double,
    val productVitaminD: Double,
    val productIsVegan: Boolean,
    val productBackdrop: String,
    val approved: Boolean,
)

@Serializable
data class AddProductToMealRequest(
    val mealId: String,
    val productId: String,
    val servingSize: Double
)

@Serializable
data class AddProductToMealResponse(
    val success: Boolean,
    val message: String? = null
)

fun createDailyConsumptionService(apiBaseUrl: String = "http://192.168.1.101:8080"): DailyConsumptionService {
    val okHttpClient = OkHttpClient.Builder()
        .build()

    val json = Json { ignoreUnknownKeys = true }
    val contentType = "application/json".toMediaType()

    val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    return retrofit.create(DailyConsumptionService::class.java)
}