package ru.point.recipe_information.domain

import ru.point.api.recipes.domain.models.FullRecipeData
import kotlin.math.pow

class CalculateRecipeDataUseCase {

    operator fun invoke(
        original: FullRecipeData,
        newServingSize: Double? = null,
        newCalories: Double? = null
    ): FullRecipeData {
        val factor = when {
            newServingSize != null -> newServingSize / original.recipeProductServingSizeDefault
            newCalories != null -> newCalories / original.recipeProductCalories
            else -> 1.0
        }

        val updatedServingSize = when {
            newServingSize != null -> newServingSize
            newCalories != null -> original.recipeProductServingSizeDefault * factor
            else -> original.recipeProductServingSizeDefault
        }
        val updatedCalories = when {
            newCalories != null -> newCalories
            newServingSize != null -> original.recipeProductCalories * factor
            else -> original.recipeProductCalories
        }

        return original.copy(
            recipeProductCalories = updatedCalories.round(1),
            recipeProductServingSizeDefault = updatedServingSize.round(1),
            recipeProductProtein = (original.recipeProductProtein * factor).round(1),
            recipeProductFat = (original.recipeProductFat * factor).round(1),
            recipeProductCarbohydrates = (original.recipeProductCarbohydrates * factor).round(1),
            recipeProductDietaryFiber = (original.recipeProductDietaryFiber * factor).round(1),
            recipeProductSugars = (original.recipeProductSugars * factor).round(1),
            recipeProductStarchDextrins = (original.recipeProductStarchDextrins * factor).round(1),
            recipeProductPotassium = (original.recipeProductPotassium * factor).round(1),
            recipeProductCalcium = (original.recipeProductCalcium * factor).round(1),
            recipeProductSilicon = (original.recipeProductSilicon * factor).round(1),
            recipeProductMagnesium = (original.recipeProductMagnesium * factor).round(1),
            recipeProductSodium = (original.recipeProductSodium * factor).round(1),
            recipeProductSulfur = (original.recipeProductSulfur * factor).round(1),
            recipeProductPhosphorus = (original.recipeProductPhosphorus * factor).round(1),
            recipeProductChlorine = (original.recipeProductChlorine * factor).round(1),
            recipeProductIron = (original.recipeProductIron * factor).round(1),
            recipeProductZinc = (original.recipeProductZinc * factor).round(1),
            recipeProductOmega3 = (original.recipeProductOmega3 * factor).round(1),
            recipeProductOmega6 = (original.recipeProductOmega6 * factor).round(1),
            recipeProductVitaminA = (original.recipeProductVitaminA * factor).round(1),
            recipeProductVitaminB1 = (original.recipeProductVitaminB1 * factor).round(1),
            recipeProductVitaminB2 = (original.recipeProductVitaminB2 * factor).round(1),
            recipeProductVitaminB4 = (original.recipeProductVitaminB4 * factor).round(1),
            recipeProductVitaminC = (original.recipeProductVitaminC * factor).round(1),
            recipeProductVitaminD = (original.recipeProductVitaminD * factor).round(1),
        )
    }

    /*
    * recipeProductCalories = updatedCalories.round(1),
            recipeProductServingSizeDefault = ,
            recipeProductProtein = (original.recipeProductProtein).round(1),
            recipeProductFat = (original.recipeProductFat * factor).round(1),
            recipeProductCarbohydrates = (original.recipeProductCarbohydrates * factor).round(1),
            recipeProductDietaryFiber = (original.recipeProductDietaryFiber * factor).round(1),
            recipeProductSugars = (original.recipeProductSugars * factor).round(1),
            recipeProductStarchDextrins = (original.recipeProductStarchDextrins * factor).round(1),
            recipeProductPotassium = (original.recipeProductPotassium * factor).round(1),
            recipeProductCalcium = (original.recipeProductCalcium * factor).round(1),
            recipeProductSilicon = (original.recipeProductSilicon * factor).round(1),
            recipeProductMagnesium = (original.recipeProductMagnesium * factor).round(1),
            recipeProductSodium = (original.recipeProductSodium * factor).round(1),
            recipeProductSulfur = (original.recipeProductSulfur * factor).round(1),
            recipeProductPhosphorus = (original.recipeProductPhosphorus * factor).round(1),
            recipeProductChlorine = (original.recipeProductChlorine * factor).round(1),
            recipeProductIron = (original.recipeProductIron * factor).round(1),
            recipeProductZinc = (original.recipeProductZinc * factor).round(1),
            recipeProductOmega3 = (original.recipeProductOmega3 * factor).round(1),
            recipeProductOmega6 = (original.recipeProductOmega6 * factor).round(1),
            recipeProductVitaminA = (original.recipeProductVitaminA * factor).round(1),
            recipeProductVitaminB1 = (original.recipeProductVitaminB1 * factor).round(1),
            recipeProductVitaminB2 = (original.recipeProductVitaminB2 * factor).round(1),
            recipeProductVitaminB4 = (original.recipeProductVitaminB4 * factor).round(1),
            recipeProductVitaminC = (original.recipeProductVitaminC * factor).round(1),
            recipeProductVitaminD = (original.recipeProductVitaminD * factor).round(1),
    *
    * */

    private fun Double.round(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return kotlin.math.round(this * factor) / factor
    }

}