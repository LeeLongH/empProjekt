package com.example.porocilolovec.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UsersViewModel(private val userRepo: OfflineRepo) : ViewModel() { // ✅ Use OfflineUserRepository
    suspend fun getUsers() = userRepo.getAllUsers()

    fun addFriend(userId1: Int, userId2: Int) {
        viewModelScope.launch {
            userRepo.addFriend(userId1, userId2)
        }
    }
}
