package com.example.billsia.data.dao

import androidx.room.*
import com.example.billsia.data.entities.EmpresaEntity


@Dao
interface EmpresaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmpresa(empresa: EmpresaEntity)

    @Query("SELECT * FROM empresa WHERE nit = :nit LIMIT 1")
    suspend fun getEmpresaByNit(nit: String): EmpresaEntity?
}
