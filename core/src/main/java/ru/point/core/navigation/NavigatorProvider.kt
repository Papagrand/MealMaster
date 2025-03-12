package ru.point.core.navigation

import androidx.navigation.NavController

interface NavigatorProvider {
    fun getNavigator(navController: NavController): Navigator
}