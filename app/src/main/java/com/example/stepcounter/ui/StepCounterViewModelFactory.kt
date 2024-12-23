package com.example.stepcounter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stepcounter.data.UserDAO

class StepCounterViewModelFactory(
    private val userDAO: UserDAO
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepCounterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StepCounterViewModel(userDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
