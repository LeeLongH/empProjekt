package com.example.porocilolovec.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.porocilolovec.data.OfflineRepo
import com.example.porocilolovec.data.UserDao
import android.content.Context


class PorociloLovecViewModelFactory(
    private val offlineRepo: OfflineRepo,
    private val context: Context // Add context here
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PorociloLovecViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PorociloLovecViewModel(offlineRepo, context) as T // Pass context to the ViewModel
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
