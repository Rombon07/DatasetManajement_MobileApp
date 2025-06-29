package com.example.data_manajemet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data_manajemet.repository.RequestRepository
import com.example.data_manajemet.repository.DatasetRepository

class RequestListViewModelFactory(
    private val requestRepository: RequestRepository,
    private val datasetRepository: DatasetRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestListViewModel::class.java)) {
            return RequestListViewModel(
                requestRepository = requestRepository,
                datasetRepository = datasetRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
