package com.example.stepcounter.data

import com.example.stepcounter.ui.User

class OfflineUserRepo(private val userDAO: UserDAO) : UserRepo {
    override fun getAllUsers(): List<User> {
        TODO("Not yet implemented")
    }
    override fun getUserByProfession(): List<User> {
        TODO("Not yet implemented")
    }
}
/*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OfflineUserRepo(private val userDAO: UserDAO) : UserRepo {
    override suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        userDAO.getAllUsers()
    }
}*/
