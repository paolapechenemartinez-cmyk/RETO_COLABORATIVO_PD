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

// Paso 4: data classes (TODO: ajusta los nombres de los campos a tu API)
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)
data class UserResponse(val nombre: String, val correo: String)

// Paso 5: endpoints (TODO: ajusta las rutas)
interface ApiService {
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("usuario")
    suspend fun getUsuario(@Header("Authorization") auth: String): Response<UserResponse>
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