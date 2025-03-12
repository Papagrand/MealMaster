package ru.point.api.registration

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface RegistrationService {
    @POST("/register")
    suspend fun register(@Body request: RegistrationRequest): RegistrationResponse

    @GET("/register/checkEmail")
    suspend fun checkEmail(@Query("email") email: String): Response<EmailCheckResponse>

    @GET("/register/checkLogin")
    suspend fun checkLogin(@Query("login") login: String): Response<LoginCheckResponse>
}

@Serializable
data class RegistrationRequest(
    val login: String,
    val email: String,
    val password: String
)

@Serializable
data class RegistrationResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class EmailCheckResponse(
    val available: Boolean,
    val message: String? = null
)

@Serializable
data class LoginCheckResponse(
    val available: Boolean,
    val message: String? = null
)

fun createRegistrationService(apiBaseUrl: String = "http://192.168.1.101:8080"): RegistrationService {
    val okHttpClient = OkHttpClient.Builder()
        .build()

    val json = Json { ignoreUnknownKeys = true }
    val contentType = "application/json".toMediaType()

    val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    return retrofit.create(RegistrationService::class.java)
}