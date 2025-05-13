package com.example.billsia.data.dao

import androidx.room.*
import com.example.billsia.data.entities.Usuario

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE cedula = :cedula LIMIT 1")
    suspend fun obtenerUsuarioPorCedula(cedula: String): Usuario?

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)
}
