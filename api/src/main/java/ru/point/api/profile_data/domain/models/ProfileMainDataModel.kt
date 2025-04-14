package ru.point.api.profile_data.domain.models

import kotlinx.serialization.Serializable


data class ProfileDataModel<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class ProfileMainDataModel(
    val userProfileId: String,
    val name: String,
    val secName: String?,
    val height: Double,
    val weight: Double,
    val age: Int,
    val gender: Boolean,
    val activityLevel: String,
    val goal: String,
    val goalWeight: Double,
    val goalTimeStart: String,
    val goalTimeEnd: String,
    val profilePicture: String,
)
