package com.example.billsia.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.billsia.data.entities.Mensaje

@Dao
interface MensajeDao {
    @Insert
    suspend fun insertarMensaje(mensaje: Mensaje)

    @Query("SELECT * FROM mensajes ORDER BY timestamp ASC")
    suspend fun obtenerMensajes(): List<Mensaje>

    @Delete
    suspend fun eliminarMensaje(mensaje: Mensaje)
}
