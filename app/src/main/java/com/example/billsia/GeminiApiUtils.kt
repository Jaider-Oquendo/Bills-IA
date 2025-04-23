package com.example.billsia

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.auth.oauth2.AccessToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

object GeminiApiUtils {
    private const val TAG = "GeminiApiUtils"
    suspend fun getGeminiResponse(prompt: String, context: Context): String {
        Log.d(TAG, "Iniciando solicitud a Gemini API...")

        val client = OkHttpClient()

        val url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash-lite-001:generateContent?key=AIzaSyDc-YAMW1FR3xfvYMz5oIe3R-vjx3-ug2k" // solo API key

        val json = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user") // agregado el rol
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        Log.d(TAG, "Cuerpo JSON: $json")

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build() // sin Authorization

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d(TAG, "Código de respuesta: ${response.code}")
            Log.d(TAG, "Respuesta completa: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                val firstText = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                firstText ?: "Sin respuesta."
            } else {
                "Error en la respuesta de Gemini: ${response.message}"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error durante la solicitud HTTP: ${e.message}", e)
            "Error al hacer la solicitud: ${e.message}"
        }
    }
}

