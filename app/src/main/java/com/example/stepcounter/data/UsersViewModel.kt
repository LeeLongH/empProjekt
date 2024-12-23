package com.example.stepcounter.data

import androidx.lifecycle.ViewModel

class UsersViewModel(private val repo: UserRepo): ViewModel() {
    fun getUsers() = repo.getAllUsers()
}

private fun UserRepo.getAllUsers(): Any {
    return TODO("Provide the return value")
}
