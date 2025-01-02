package com.example.stepcounter.data

import com.example.stepcounter.ui.User
import kotlinx.coroutines.flow.Flow

interface UserRepo {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllUsers(): Flow<List<User>>
    fun getUsersByProfession(string: String): Flow<List<User>>

}