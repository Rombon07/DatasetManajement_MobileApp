package com.example.data_manajemet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data_manajemet.data.DatasetDao
import com.example.data_manajemet.repository.DatasetRepository

class DatasetViewModelFactory(
    private val dao: DatasetDao,
    private val repository: DatasetRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatasetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatasetViewModel(dao, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
