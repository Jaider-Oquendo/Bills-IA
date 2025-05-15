package com.example.billsia.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersonaNatural(
    @PrimaryKey val cedula: String,
    val ingresos: Double,
    val gastos: Double,
    val ahorros: Double,
    val deudas: Double
)
