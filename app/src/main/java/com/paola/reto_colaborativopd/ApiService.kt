package com.paola.reto_colaborativopd

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// Paso 4: data classes para DummyJSON
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val accessToken: String,
    val refreshToken: String
)

data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String
)

// Paso 5: endpoints para DummyJSON
interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<UserResponse>
}

// Paso 6: cliente Retrofit
object RetrofitClient {
    private const val BASE_URL = "https://TU_URL_BASE/" // TODO: debe terminar en "/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}