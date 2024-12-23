package com.example.stepcounter.data

import com.example.stepcounter.ui.User

interface UserRepo {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllUsers(): List<User>
    fun getUserByProfession(): List<User>

}