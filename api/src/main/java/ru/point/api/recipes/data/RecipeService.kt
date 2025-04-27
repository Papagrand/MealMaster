package ru.point.api.recipes.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService {
    @GET("/recipes/getRecipeData")
    suspend fun getRecipeData(
        @Query("recipeId") recipeId: String
    ): Response<RecipeResponse<RecipeResponseMain>>

    @GET("/recipes/search")
    suspend fun searchRecipes(
        @Query("recipeName") recipeName: String,
        @Query("isVegan") isVegan: Boolean,
        @Query("cookingTime") cookingTime: Int,
        @Query("difficulty") difficulty: Int,
        @Query("maxCalories") maxCalories: Double,
        @Query("page") page: Int,
        @Query("pagesize") pagesize: Int,
    ) : Response<SearchedRecipesResponse<RecipeItemResponse>>

    @GET("/recipes/getMealIds")
    suspend fun getMealIds(
        @Query("userId") userId: String
    ): Response<MealIdsResponse<MealIdsInfo>>
}

@Serializable
data class MealIdsResponse<T>(
    val success: Boolean,
    val data: List<T>? = null,
    val message: String? = null
)

@Serializable
data class MealIdsInfo(
    val mealId: String,
    val mealType: String
)

@Serializable
data class SearchedRecipesResponse<T>(
    val success: Boolean,
    val data: List<T>? = null,
    val message: String? = null
)

@Serializable
data class RecipeItemResponse(
    val recipeId: String,
    val recipeProductId: String,
    val recipeName: String,
    val recipeBackdrop: String,
    val recipeCookingTime: Int,
    val recipeDifficulty: Int,
    val recipeIsVegan: Boolean,
    val recipeServingSize: Double,
    val recipeCalories: Double,
    val recipeProtein: Double,
    val recipeFat: Double,
    val recipeCarbohydrate: Double
)

@Serializable
data class RecipeResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class RecipeResponseMain(
    val recipeId: String,
    val recipeName: String,
    val recipeBackdrop: String,
    val recipeProductCalories: Double,
    val recipeProductServingSizeDefault: Double,
    val recipeProductProtein: Double,
    val recipeProductFat: Double,
    val recipeProductCarbohydrates: Double,
    val recipeProductDietaryFiber: Double,
    val recipeProductSugars: Double,
    val recipeProductStarchDextrins: Double,
    val recipeProductPotassium: Double,
    val recipeProductCalcium: Double,
    val recipeProductSilicon: Double,
    val recipeProductMagnesium: Double,
    val recipeProductSodium: Double,
    val recipeProductSulfur: Double,
    val recipeProductPhosphorus: Double,
    val recipeProductChlorine: Double,
    val recipeProductIron: Double,
    val recipeProductZinc: Double,
    val recipeProductOmega3: Double,
    val recipeProductOmega6: Double,
    val recipeProductVitaminA: Double,
    val recipeProductVitaminB1: Double,
    val recipeProductVitaminB2: Double,
    val recipeProductVitaminB4: Double,
    val recipeProductVitaminC: Double,
    val recipeProductVitaminD: Double,
    val recipeIsVegan: Boolean,
    val recipeDifficulty: Int,
    val recipeCookingTime: Int,
    val recipeIngredients: List<RecipeIngredientsResponse>,
    val recipeSteps: List<RecipeStepsResponse>,
    val recipeImages: List<RecipeImagesResponse>
)

@Serializable
data class RecipeIngredientsResponse(
    val ingredientName: String,
    val ingredientGrams: Double
)

@Serializable
data class RecipeStepsResponse(
    val stepNumber: Int,
    val stepDescription: String
)

@Serializable
data class RecipeImagesResponse(
    val stepNumber: Int,
    val stepImage: String
)

fun createRecipeService(
    apiBaseUrl: String = "http://192.168.1.101:8080"
): RecipeService {
    val okHttpClient = OkHttpClient.Builder().build()

    val json = Json { ignoreUnknownKeys = true }
    val contentType = "application/json".toMediaType()

    val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    return retrofit.create(RecipeService::class.java)
}