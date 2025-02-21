package com.example.porocilolovec.data

import androidx.lifecycle.ViewModel

class UsersViewModel(private val userRepo: OfflineUserRepository) : ViewModel() { // ✅ Use OfflineUserRepository
    suspend fun getUsers() = userRepo.getAllUsers()
}
