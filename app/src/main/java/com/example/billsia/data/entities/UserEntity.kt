package com.example.billsia.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val cedula: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val direccion: String,
    val telefono: String
)
