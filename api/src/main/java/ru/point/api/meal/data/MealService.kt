package ru.point.api.meal.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query
import ru.point.api.meal.domain.models.FoodItemModel

interface MealService {
    @GET("/user/daily_consumption/meal")
    suspend fun getMealInformation(
        @Query("dailyConsumptionId") dailyConsumptionId: String,
        @Query("mealType") mealType: String
    ) : Response<MealSuccessResponse<MealDataResponse>>

    @GET("/products/search")
    suspend fun searchProducts(
        @Query("productName") productName: String,
        @Query("isVegan") isVegan: Boolean,
        @Query("sortCalories") sortCalories: String,
        @Query("page") page: Int,
        @Query("pagesize") pagesize: Int,
    ) : Response<SearchedProductsResponse<ProductItemResponse>>

    @PATCH("/user/daily_consumption/updateFoodItem")
    suspend fun updateServingSizeOrCalories(
        @Body request: UpdateMealItemServingSizeCaloriesRequest
    ): UpdateDeleteMealItemResponse

    @DELETE("/user/daily_consumption/deleteFoodItem")
    suspend fun deleteItemFromMeal(
        @Query("itemId") itemId: String
    ): UpdateDeleteMealItemResponse
}

@Serializable
data class DeleteMealItemRequest(
    val itemId: String
)


@Serializable
data class UpdateMealItemServingSizeCaloriesRequest(
    val itemId: String,
    val servingSize: Double
)

@Serializable
data class UpdateDeleteMealItemResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class MealSuccessResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class MealDataResponse(
    val mealId: String,
    val sumMealCarbohydrates: Double,
    val sumMealProteins: Double,
    val sumMealFats: Double,
    val sumMealCalories: Double,
    val mealItemsList: List<FoodItemModel>
)

@Serializable
data class SearchedProductsResponse<T>(
    val success: Boolean,
    val data: List<T>? = null,
    val message: String? = null
)

@Serializable
data class ProductItemResponse (
    val productId: String,
    val productName: String,
    val productCalories: Double,
    val productServingSizeDefault: Double,
    val productProtein: Double,
    val productFat: Double,
    val productCarbohydrates: Double,
    val productIsVegan: Boolean,
    val productBackdrop: String
)

fun createMealService(apiBaseUrl: String = "http://192.168.1.101:8080"): MealService {
    val okHttpClient = OkHttpClient.Builder()
        .build()

    val json = Json { ignoreUnknownKeys = true }
    val contentType = "application/json".toMediaType()

    val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    return retrofit.create(MealService::class.java)
}