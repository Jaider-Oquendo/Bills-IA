package com.example.billsia.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "empresa")
data class EmpresaEntity(
    @PrimaryKey val nit: String,
    val razonSocial: String,
    val sector: String,
    val utilidadOperacional: Double,
    val depreciaciones: Double,
    val ventas: Double,
    val inventarios: Double,
    val cuentasPorCobrar: Double,
    val proveedores: Double,
    val impuestos: Double,
    val intereses: Double,
    val dividendos: Double,
    val deudaFinanciera: Double,
    val tasaImpuestos: Double
)
