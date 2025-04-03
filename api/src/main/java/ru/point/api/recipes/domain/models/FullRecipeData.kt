package ru.point.api.recipes.domain.models

data class FullResponseRecipeData<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)


data class FullRecipeData(
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
    val recipeIngredients: List<IngredientData>,
    val recipeSteps: List<RecipeStepData>,
    val recipeImages: List<StepImageData>
)

data class IngredientData(
    val ingredientName: String,
    val ingredientGrams: Double
)

data class RecipeStepData(
    val stepNumber: Int,
    val stepDescription: String
)

data class StepImageData(
    val stepNumber: Int,
    val stepImage: String
)