package ru.point.meal.domain

import ru.point.api.meal.data.ItemInMealDataModel
import kotlin.math.pow
import kotlin.math.round

class CalculateItemDataUseCase {
    operator fun invoke(
        currentData: ItemInMealDataModel,
        newServingSize: Double? = null,
        newCalories: Double? = null
    ): ItemInMealDataModel {
        val factor = when {
            newServingSize != null -> newServingSize / currentData.currentServingSize
            newCalories != null -> newCalories / currentData.currentCalories
            else -> 1.0
        }

        val updatedServingSize = when {
            newServingSize != null -> newServingSize
            newCalories != null -> currentData.currentServingSize * factor
            else -> currentData.currentServingSize
        }
        val updatedCalories = when {
            newCalories != null -> newCalories
            newServingSize != null -> currentData.currentCalories * factor
            else -> currentData.currentCalories
        }

        return currentData.copy(
            currentServingSize = updatedServingSize.round(1),
            currentCalories = updatedCalories.round(1),
        )
    }

    private fun Double.round(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return round(this * factor) / factor
    }
}
