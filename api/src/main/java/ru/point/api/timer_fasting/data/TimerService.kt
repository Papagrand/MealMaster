package ru.point.api.timer_fasting.data

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

interface TimerService {
    @GET("/fasting")
    suspend fun getUserFasting(
        @Query("userId") userId: String
    ): Response<UserFastingResponse<UserFastingInfoResponse>>

    @GET("/fasting/getFastingScenarioInfo")
    suspend fun getFastingScenarioById(
        @Query("fastingId") fastingId: String
    ): Response<UserFastingResponse<ScenarioResponse>>


    @PATCH("/fasting/updateUserFastingInformation")
    suspend fun updateUserFastingInfo(@Body request: UpdateUserFastingRequest): UpdateUserFastingResponse

    @PATCH("/fasting/updateUserPickedScenario")
    suspend fun updateUserPickedScenario(@Body request: UpdateUserPickedScenarioRequest): UpdateUserPickedScenarioResponse
}

@Serializable
data class UserFastingResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String?
)

@Serializable
data class UserFastingInfoResponse(
    val userFastingId: String,
    val userId: String,
    val pickedScenarioId: String,
    val status: String,
    val startTime: String?,
    val endTime: String?,
    val eatingWhileFasting: Boolean,
    val isActive: Boolean,
    val lastUpdate: String?
)


@Serializable
data class ScenarioResponse(
    val scenarioFasting: Int,
    val scenarioEating: Int,
    val scenarioDescription: String,
    val scenarioName: String,
    val scenarioNotice: String?
)

@Serializable
data class UpdateUserPickedScenarioRequest(
    val userId: String,
    val pickedScenarioId: String
)

@Serializable
data class UpdateUserPickedScenarioResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class UpdateUserFastingRequest(
    val userFastingId: String,
    val userId: String,
    val status: String,
    val startTimeMillis: Long?,
    val endTimeMillis: Long?,
    val eatingWhileFast: Boolean,
    val isActive: Boolean,
    val lastUpdateMillis: Long
)

@Serializable
data class UpdateUserFastingResponse(
    val success: Boolean,
    val message: String
)

fun createTimerService(apiBaseUrl: String = "http://192.168.1.101:8080"): TimerService {
    val okHttpClient = OkHttpClient.Builder()
        .build()

    val json = Json { ignoreUnknownKeys = true }
    val contentType = "application/json".toMediaType()

    val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    return retrofit.create(TimerService::class.java)
}

