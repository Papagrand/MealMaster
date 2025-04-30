package ru.point.api.timer_fasting.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import ru.point.api.meal.data.MealService

interface TimerService {
    @GET("/fasting")
    suspend fun getUserFasting(
        @Query("userId") userId: String
    ): Response<UserFastingResponse<UserFastingInfoResponse>>

    @GET("/fasting/currentFastingScenario")
    suspend fun getCurrentFastingScenario(
        @Query("fastingId") fastingId: String
    ): Response<UserFastingResponse<ScenarioResponse>>
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
data class ScenarioResponse (
    val scenarioFasting: Int,
    val scenarioEating: Int,
    val scenarioDescription: String,
    val scenarioName: String,
    val scenarioNotice: String?
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

