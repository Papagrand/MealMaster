package ru.point.api.profile_data.data

import ru.point.core.enums.ActivityLevel
import ru.point.core.enums.UserGoal

data class UpdatedProfileData (
    val firstName: String = "",
    val lastName: String = "",
    val age: Int = 0,
    val gender: Boolean = true,
    val activityLevel: String,
    val goal: String,
    val targetWeight: Double = 0.0,
    val dietEndDate: String = ""
)