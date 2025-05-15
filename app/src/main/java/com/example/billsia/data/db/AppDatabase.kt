package com.example.billsia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.billsia.data.dao.MensajeDao
import com.example.billsia.data.dao.UserDao
import com.example.billsia.data.dao.UsuarioDao
import com.example.billsia.data.entities.Mensaje
import com.example.billsia.data.entities.UserEntity
import com.example.billsia.data.entities.Usuario
import com.example.billsia.data.dao.PersonaNaturalDao
import com.example.billsia.data.entities.PersonaNatural

@Database(
    entities = [UserEntity::class, Mensaje::class, Usuario::class, PersonaNatural::class],
    version = 4, // aumenta versión si ya tienes otra
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun personaNaturalDao(): PersonaNaturalDao

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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
