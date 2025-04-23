package com.example.billsia.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.billsia.data.db.AppDatabase
import com.example.billsia.data.entities.UserEntity
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.example.billsia.data.repository.UserRepository
import androidx.lifecycle.MutableLiveData


class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val repository: UserRepository = UserRepository(userDao)

    private val _userByCedula = MutableLiveData<UserEntity?>()
    val userByCedula: LiveData<UserEntity?> get() = _userByCedula

    private val _userByEmail = MutableLiveData<UserEntity?>()
    val userByEmail: LiveData<UserEntity?> get() = _userByEmail

    fun insertUser(user: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(user)
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }

    fun fetchUserByCedula(cedula: String): LiveData<UserEntity?> {
        return repository.getUserByCedula(cedula)
    }


    fun fetchUserByEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByEmail(email)
            _userByEmail.postValue(user)
        }
    }
    // LiveData para observar usuarios por cédula
    fun getUserByCedula(cedula: String): LiveData<UserEntity?> {
        return repository.getUserByCedula(cedula)
    }

}



