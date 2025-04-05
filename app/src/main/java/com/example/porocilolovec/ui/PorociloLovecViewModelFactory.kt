package com.example.porocilolovec.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context


class PorociloLovecViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PorociloLovecViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PorociloLovecViewModel() as T // No context needed anymore
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
