package com.example.porocilolovec.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.porocilolovec.data.UserDAO

class PorociloLovecViewModelFactory(
    private val userDAO: UserDAO
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PorociloLovecViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PorociloLovecViewModel(userDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
