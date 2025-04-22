package ru.point.setting_searched_product.domain

import ru.point.api.daily_consumption_and_product.domain.models.ProductDataModel
import kotlin.math.pow
import kotlin.math.round

class CalculateProductDataUseCase {
    operator fun invoke(
        original: ProductDataModel,
        newServingSize: Double? = null,
        newCalories: Double? = null
    ): ProductDataModel {
        // решаем, по чему масштабируем: либо по весу, либо по калориям
        val factor = when {
            newServingSize != null -> newServingSize / original.productServingSizeDefault
            newCalories != null -> newCalories / original.productCalories
            else -> 1.0
        }

        // новая порция (гр) и новая калорийность (ккал)
        val updatedServingSize = when {
            newServingSize != null -> newServingSize
            newCalories != null -> original.productServingSizeDefault * factor
            else -> original.productServingSizeDefault
        }
        val updatedCalories = when {
            newCalories != null -> newCalories
            newServingSize != null -> original.productCalories * factor
            else -> original.productCalories
        }

        return original.copy(
            // если задали newServingSize — обновляем его, иначе оставляем старый
            productServingSizeDefault = updatedServingSize.round(1),
            productCalories = updatedCalories.round(1),
            productProtein = (original.productProtein * factor).round(1),
            productFat = (original.productFat * factor).round(1),
            productCarbohydrates = (original.productCarbohydrates * factor).round(1),
            productDietaryFiber = (original.productDietaryFiber * factor).round(1),
            productSugars =( original.productSugars * factor).round(1),
            productStarchDextrins = (original.productStarchDextrins * factor).round(1),
            productPotassium = (original.productPotassium * factor).round(1),
            productCalcium = (original.productCalcium * factor).round(1),
            productSilicon = (original.productSilicon * factor).round(1),
            productMagnesium = (original.productMagnesium * factor).round(1),
            productSodium = (original.productSodium * factor).round(1),
            productSulfur = (original.productSulfur * factor).round(1),
            productPhosphorus = (original.productPhosphorus * factor).round(1),
            productChlorine = (original.productChlorine * factor).round(1),
            productIron = (original.productIron * factor).round(1),
            productZinc = (original.productZinc * factor).round(1),
            productOmega3 = (original.productOmega3 * factor).round(1),
            productOmega6 = (original.productOmega6 * factor).round(1),
            productVitaminA = (original.productVitaminA * factor).round(1),
            productVitaminB1 = (original.productVitaminB1 * factor).round(1),
            productVitaminB2 = (original.productVitaminB2 * factor).round(1),
            productVitaminB4 = (original.productVitaminB4 * factor).round(1),
            productVitaminC = (original.productVitaminC * factor).round(1),
            productVitaminD = (original.productVitaminD * factor).round(1),
        )
    }

    private fun Double.round(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return round(this * factor) / factor
    }
}
