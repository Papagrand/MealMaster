package ru.point.api.meal.data

import kotlinx.serialization.Serializable

@Serializable
data class ItemInMealDataModel (
    val itemId : String,
    val currentServingSize: Double,
    val currentCalories: Double
)