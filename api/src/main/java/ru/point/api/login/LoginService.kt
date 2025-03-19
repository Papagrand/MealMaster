package ru.point.api.login

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

interface LoginService {

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("/login/checkLogin")
    suspend fun checkLoginAuth(@Query("login") login: String): Response<LoginCheckResponseAuth>

    @GET("/user/profileExist")
    suspend fun checkProfileExist(@Query("userProfileId") userProfileId:String): Response<ProfileExistCheckResponse>

    @GET("/user/isAuth")
    suspend fun checkAuthUser(
        @Query("userId") userId: String,
        @Query("deviceId") deviceId: String
    ): Response<CheckAuthUserResponse>
}


@Serializable
data class CheckAuthUserRequest(
    val userId: String,
    val deviceId: String
)

@Serializable
data class CheckAuthUserResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class LoginCheckResponseAuth(
    val available: Boolean,
    val message: String? = null
)

@Serializable
data class ProfileExistCheckResponse(
    val available: Boolean,
    val message: String? = null
)

@Serializable
data class LoginRequest(
    val login: String,
    val password: String,
    val deviceId: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String? = null
)

fun createLoginService(apiBaseUrl: String = "http://192.168.1.101:8080"): LoginService {
    val okHttpClient = OkHttpClient.Builder()
        .build()

    val json = Json { ignoreUnknownKeys = true }
    val contentType = "application/json".toMediaType()

    val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    return retrofit.create(LoginService::class.java)
}