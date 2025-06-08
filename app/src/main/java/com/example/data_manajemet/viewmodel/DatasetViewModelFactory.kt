package com.example.data_manajemet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data_manajemet.data.DatasetDao

class DatasetViewModelFactory(private val dao: DatasetDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatasetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatasetViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
