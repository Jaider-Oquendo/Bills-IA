package com.example.billsia.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mensajes")
data class Mensaje(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val texto: String,
    val esUsuario: Boolean,  // Para saber si el mensaje es del usuario o de la API
    val timestamp: Long = System.currentTimeMillis()  // Para ordenar los mensajes por tiempo
)
