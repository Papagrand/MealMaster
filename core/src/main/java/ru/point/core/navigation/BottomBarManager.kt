package ru.point.core.navigation

import androidx.lifecycle.LifecycleObserver

interface BottomBarManager : LifecycleObserver {
    fun hide()
    fun show()
}