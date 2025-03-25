package ru.point.api.profile_creating.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface ProfileService {
    @POST("/user/create_profile")
    suspend fun createProfile(@Body request: CreateProfileRequest): CreateProfileResponse
}

@Serializable
data class CreateProfileRequest(
    val userProfileId: String,
    val name: String,
    val secName: String,
    val height: Double,
    val weight: Double,
    val age: Int,
    val gender: Boolean,
    val activityLevel: Int,
    val goal: Int,
    val goalWeight: Double,
    val goalTimeEnd: String
)

@Serializable
data class CreateProfileResponse(
    val success: Boolean,
    val message: String? = null
)

fun createProfileService(apiBaseUrl: String = "http://192.168.1.101:8080"): ProfileService {
    val okHttpClient = OkHttpClient.Builder()
        .build()

    val json = Json { ignoreUnknownKeys = true }
    val contentType = "application/json".toMediaType()

    val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    return retrofit.create(ProfileService::class.java)
}