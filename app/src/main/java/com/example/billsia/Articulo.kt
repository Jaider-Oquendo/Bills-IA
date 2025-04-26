package com.example.billsia

data class Articulo(
    val id: Int = 0, // Agregamos valores por defecto para Firebase
    val titulo: String = "",
    val resumen: String = "",
    val url: String = ""
)
