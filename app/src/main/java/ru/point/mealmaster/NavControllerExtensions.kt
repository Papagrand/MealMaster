package ru.point.mealmaster

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController

fun NavController.navigateSafe(@IdRes actionId: Int, args: Bundle? = null) {
    currentDestination?.getAction(actionId)?.let {
        navigate(actionId, args)
    }
}