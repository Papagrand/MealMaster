package ru.point.api.meal.domain

sealed class SearchProductResult {
    object Success : SearchProductResult()
    data class Failure(val message: String) : SearchProductResult()
}