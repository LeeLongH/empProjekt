package com.example.stepcounter.data

import androidx.lifecycle.ViewModel

class UsersViewModel(private val repo: UserRepo): ViewModel() {
    fun getUsers() = repo.getAllUsers()
    fun getUsersByProfession(profession: String) = repo.getUsersByProfession(profession)
}

private fun UserRepo.getAllUsers(): Any {
    return TODO("Provide the return value")
}
