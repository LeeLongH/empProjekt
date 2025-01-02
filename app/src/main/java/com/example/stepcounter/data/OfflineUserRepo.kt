package com.example.stepcounter.data

import com.example.stepcounter.ui.User
import com.example.stepcounter.data.UserDAO
import com.example.stepcounter.data.UserRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OfflineUserRepo(private val userDAO: UserDAO) : UserRepo {
    // Funkcija za pridobitev vseh uporabnikov
    override fun getAllUsers(): Flow<List<User>> {
        return flow {
            // Uporabimo suspend funkcijo znotraj coroutine
            emit(userDAO.getAllUsers())
        }
    }

    override fun getUsersByProfession(profession: String): Flow<List<User>> {
        // Implementacija pridobivanja uporabnikov glede na profesijo
        return flow {
            emit(userDAO.getUsersByProfession(profession))
        }
    }
}
