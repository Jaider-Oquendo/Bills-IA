package com.example.billsia.api

import com.example.billsia.models.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v2/everything")
    fun getNoticias(
        @Query("q") query: String = "finanzas OR tributarios", // Búsqueda de noticias sobre finanzas o tributación
        @Query("language") language: String = "es", // Obtener noticias en español
        @Query("apiKey") apiKey: String = "b9695b29a6fd4639b2941400fa16549f" // Tu clave API
    ): Call<ApiResponse>

    companion object {
        private const val BASE_URL = "https://newsapi.org/"

        fun create(): ApiService {
            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
