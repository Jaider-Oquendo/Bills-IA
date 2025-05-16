package com.example.billsia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.billsia.data.dao.MensajeDao
import com.example.billsia.data.dao.UserDao
import com.example.billsia.data.dao.UsuarioDao
import com.example.billsia.data.dao.PersonaNaturalDao
import com.example.billsia.data.dao.EmpresaDao
import com.example.billsia.data.entities.Mensaje
import com.example.billsia.data.entities.UserEntity
import com.example.billsia.data.entities.Usuario
import com.example.billsia.data.entities.PersonaNatural
import com.example.billsia.data.entities.EmpresaEntity

@Database(
    entities = [
        UserEntity::class,
        Mensaje::class,
        Usuario::class,
        PersonaNatural::class,
        EmpresaEntity::class       // Aquí agregas la entidad EmpresaEntity
    ],
    version = 5, // sube la versión para que Room reconozca el cambio
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun personaNaturalDao(): PersonaNaturalDao

    abstract fun empresaDao(): EmpresaDao  // Agrega el DAO de empresa

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Cuidado: destruye datos si cambia versión y no migras
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
