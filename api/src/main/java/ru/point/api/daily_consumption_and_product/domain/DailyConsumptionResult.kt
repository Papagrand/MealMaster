package ru.point.api.daily_consumption_and_product.domain

sealed class DailyConsumptionResult {
    object Success : DailyConsumptionResult()
    data class Failure(val message: String) : DailyConsumptionResult()
}