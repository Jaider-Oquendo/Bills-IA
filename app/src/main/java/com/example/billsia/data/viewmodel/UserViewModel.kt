package com.example.billsia.data.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.billsia.data.AppDatabase
import com.example.billsia.data.entities.UserEntity
import com.example.billsia.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    private val _userByEmail = MutableLiveData<UserEntity?>()
    val userByEmail: LiveData<UserEntity?> get() = _userByEmail

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

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

    fun getUserByCedula(cedula: String): LiveData<UserEntity?> {
        return repository.getUserByCedula(cedula)
    }

    fun fetchUserByEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByEmail(email)
            _userByEmail.postValue(user)
        }
    }
}
