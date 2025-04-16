package com.example.billsia.data.repository

import androidx.lifecycle.LiveData
import com.example.billsia.data.dao.UserDao
import com.example.billsia.data.entities.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(userEntity: UserEntity): Long {
        return userDao.insertUser(userEntity)
    }

    suspend fun updateUser(userEntity: UserEntity): Int {
        return userDao.updateUser(userEntity)
    }

    fun getUserByCedula(cedula: String): LiveData<UserEntity?> {
        return userDao.getUserByCedula(cedula)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

}

