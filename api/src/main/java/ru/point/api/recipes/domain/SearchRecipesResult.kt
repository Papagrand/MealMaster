package ru.point.api.recipes.domain

sealed class SearchProductResult {
    object Success : SearchProductResult()
    data class Failure(val message: String) : SearchProductResult()
}