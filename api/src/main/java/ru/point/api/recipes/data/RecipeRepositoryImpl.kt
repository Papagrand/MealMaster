package ru.point.api.recipes.data

import ru.point.api.recipes.domain.RecipeRepository
import ru.point.api.recipes.domain.models.FullRecipeData
import ru.point.api.recipes.domain.models.FullResponseRecipeData
import ru.point.api.recipes.domain.models.IngredientData
import ru.point.api.recipes.domain.models.RecipeItemModel
import ru.point.api.recipes.domain.models.RecipeStepData
import ru.point.api.recipes.domain.models.SearchedRecipesSuccessModel
import ru.point.api.recipes.domain.models.StepImageData

class RecipeRepositoryImpl(
    private val recipeService: RecipeService
) : RecipeRepository {
    override suspend fun getFullRecipeData(recipeId: String): Result<FullResponseRecipeData<FullRecipeData>> {
        return try {
            val response = recipeService.getRecipeData(recipeId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val domainModel = FullRecipeData(
                        recipeId = body.data.recipeId,
                        recipeName = body.data.recipeName,
                        recipeBackdrop = body.data.recipeBackdrop,
                        recipeProductCalories = body.data.recipeProductCalories,
                        recipeProductServingSizeDefault = body.data.recipeProductServingSizeDefault,
                        recipeProductProtein = body.data.recipeProductProtein,
                        recipeProductFat = body.data.recipeProductFat,
                        recipeProductCarbohydrates = body.data.recipeProductCarbohydrates,
                        recipeProductDietaryFiber = body.data.recipeProductDietaryFiber,
                        recipeProductSugars = body.data.recipeProductSugars,
                        recipeProductStarchDextrins = body.data.recipeProductStarchDextrins,
                        recipeProductPotassium = body.data.recipeProductPotassium,
                        recipeProductCalcium = body.data.recipeProductCalcium,
                        recipeProductSilicon = body.data.recipeProductSilicon,
                        recipeProductMagnesium = body.data.recipeProductMagnesium,
                        recipeProductSodium = body.data.recipeProductSodium,
                        recipeProductSulfur = body.data.recipeProductSulfur,
                        recipeProductPhosphorus = body.data.recipeProductPhosphorus,
                        recipeProductChlorine = body.data.recipeProductChlorine,
                        recipeProductIron = body.data.recipeProductIron,
                        recipeProductZinc = body.data.recipeProductZinc,
                        recipeProductOmega3 = body.data.recipeProductOmega3,
                        recipeProductOmega6 = body.data.recipeProductOmega6,
                        recipeProductVitaminA = body.data.recipeProductVitaminA,
                        recipeProductVitaminB1 = body.data.recipeProductVitaminB1,
                        recipeProductVitaminB2 = body.data.recipeProductVitaminB2,
                        recipeProductVitaminB4 = body.data.recipeProductVitaminB4,
                        recipeProductVitaminC = body.data.recipeProductVitaminC,
                        recipeProductVitaminD = body.data.recipeProductVitaminD,
                        recipeIsVegan = body.data.recipeIsVegan,
                        recipeDifficulty = body.data.recipeDifficulty,
                        recipeCookingTime = body.data.recipeCookingTime,
                        recipeIngredients = body.data.recipeIngredients.map {
                            IngredientData(
                                ingredientName = it.ingredientName,
                                ingredientGrams = it.ingredientGrams
                            )
                        },
                        // Преобразуем шаги
                        recipeSteps = body.data.recipeSteps.map {
                            RecipeStepData(
                                stepNumber = it.stepNumber,
                                stepDescription = it.stepDescription
                            )
                        },
                        // Преобразуем изображения шагов
                        recipeImages = body.data.recipeImages.map {
                            StepImageData(
                                stepNumber = it.stepNumber,
                                stepImage = it.stepImage
                            )
                        }
                    )
                    // Оборачиваем domainModel в FullResponseRecipeData
                    Result.success(FullResponseRecipeData(success = true, data = domainModel))
                } else {
                    Result.failure(Throwable(body?.message ?: "Recipe not found"))
                }
            } else {
                Result.failure(Throwable("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchRecipes(
        recipeName: String,
        isVegan: Boolean?,
        cookingTime: Int?,
        difficulty: Int?,
        maxCalories: Double?,
        page: Int?,
        pagesize: Int?
    ): Result<SearchedRecipesSuccessModel<RecipeItemModel>> {
        return try {
            val response = recipeService.searchRecipes(
                recipeName,
                isVegan == true,
                cookingTime ?: 999,
                difficulty ?: 5,
                maxCalories ?: 1000.0,
                page ?: 1,
                pagesize ?: 10
            )

            val body = response.body()
            when(response.code()){
                200 -> {
                    if (body != null && body.success && body.data != null){
                        val domainList: List<RecipeItemModel> = body.data.map { dto ->
                            RecipeItemModel(
                                recipeId = dto.recipeId,
                                recipeName = dto.recipeName,
                                recipeBackdrop = dto.recipeBackdrop,
                                recipeCookingTime = dto.recipeCookingTime,
                                recipeDifficulty = dto.recipeDifficulty,
                                recipeIsVegan = dto.recipeIsVegan,
                                recipeServingSize = dto.recipeServingSize,
                                recipeCalories = dto.recipeCalories,
                                recipeProtein = dto.recipeProtein,
                                recipeFat = dto.recipeFat,
                                recipeCarbohydrate = dto.recipeCarbohydrate
                            )
                        }
                        Result.success(
                            SearchedRecipesSuccessModel(
                                success = true,
                                data = domainList
                            )
                        )
                    } else {
                        Result.failure(Throwable(body?.message ?: "Unexpected fault"))
                    }
                }

                404 -> {
                    Result.failure(Throwable(body?.message ?: "Not Found"))
                }

                else -> {
                    Result.failure(Throwable(body?.message ?: "Unexpected error"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
