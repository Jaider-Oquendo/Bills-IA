package com.example.billsia.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val cedula: String,
    val ingreso: Double,
    val patrimonio: Double,
    val consumo: Double,
    val compras: Double
)
