package com.example.porocilolovec.data

import com.example.porocilolovec.ui.User
import kotlinx.coroutines.flow.Flow

interface UserRepo {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllUsers(): Flow<List<User>>
    fun getUsersByProfession(string: String): Flow<List<User>>

}