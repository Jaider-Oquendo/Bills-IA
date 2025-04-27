package com.example.billsia.models

data class ApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Noticia>
)


