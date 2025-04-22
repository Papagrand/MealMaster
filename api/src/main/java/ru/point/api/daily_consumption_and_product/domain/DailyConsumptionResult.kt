package ru.point.api.daily_consumption_and_product.domain

sealed class DailyConsumptionResult {
    object Success : DailyConsumptionResult()
    data class Failure(val message: String) : DailyConsumptionResult()
}

sealed class AddProductResult {
    object Success : AddProductResult()
    data class Failure(val message: String) : AddProductResult()
}