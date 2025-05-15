package com.example.billsia.data.dao

import androidx.room.*
import com.example.billsia.data.entities.PersonaNatural

@Dao
interface PersonaNaturalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(persona: PersonaNatural)

    @Query("SELECT * FROM PersonaNatural WHERE cedula = :cedula LIMIT 1")
    suspend fun buscarPorCedula(cedula: String): PersonaNatural?
}
