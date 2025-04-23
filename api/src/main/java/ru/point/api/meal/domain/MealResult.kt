package ru.point.api.meal.domain

sealed class MealResult {
    object Success : MealResult()
    data class Failure(val message: String) : MealResult()
}

sealed class UpdateMealItemResult{
    object Success : UpdateMealItemResult()
    data class Failure(val message: String) : UpdateMealItemResult()
}