package com.example.porocilolovec.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.porocilolovec.data.OfflineRepo
import com.example.porocilolovec.data.UserDao

class PorociloLovecViewModelFactory(
    private val offlineRepo: OfflineRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PorociloLovecViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PorociloLovecViewModel(offlineRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
