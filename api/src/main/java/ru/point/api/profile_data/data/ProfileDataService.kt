package ru.point.api.profile_data.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface ProfileDataService {
    @GET("/user/profileData")
    suspend fun getMainProfileData(
        @Query("userProfileId") userProfileId: String
    ): Response<ProfileDataResponse<ProfileMainDataResponse>>

    @PATCH("/user/update_profile_data")
    suspend fun updateProfileInformation( @Body request: UpdateProfileRequest ) : UpdateProfileResponse
}

@Serializable
data class ProfileDataResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class ProfileMainDataResponse(
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

@Serializable
data class UpdateProfileRequest(
    val userProfileId: String,
    val name: String,
    val secName: String?,
    val age: Int,
    val gender: Boolean,
    val activityLevel: Int,
    val goal: Int,
    val goalWeight: Double,
    val goalTimeEnd: String
)

@Serializable
data class UpdateProfileResponse(
    val success: Boolean,
    val message: String? = null
)


fun createProfileDataService(apiBaseUrl: String = "http://192.168.1.101:8080"): ProfileDataService {
    val okHttpClient = OkHttpClient.Builder()
        .build()

    val json = Json { ignoreUnknownKeys = true }
    val contentType = "application/json".toMediaType()

    val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    return retrofit.create(ProfileDataService::class.java)
}