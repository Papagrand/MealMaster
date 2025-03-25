package ru.point.api.profile_creating.data

data class OnboardingProfileData(
    val firstName: String = "",
    val lastName: String = "",
    val height: Float = 0f,
    val weight: Float = 0f,
    val age: Int = 0,
    val gender: Boolean = true,
    val activityLevel: Int = 0,
    val goal: Int = 0,
    val targetWeight: Float = 0f,
    val dietEndDate: String = ""
)
