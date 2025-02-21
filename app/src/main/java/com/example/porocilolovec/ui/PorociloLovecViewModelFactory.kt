package com.example.porocilolovec.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.porocilolovec.data.UserDao

class PorociloLovecViewModelFactory(
    private val userDAO: UserDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PorociloLovecViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PorociloLovecViewModel(userDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
