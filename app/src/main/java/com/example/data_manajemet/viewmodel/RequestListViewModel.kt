package com.example.data_manajemet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_manajemet.data.remote.DatasetRequestItem
import com.example.data_manajemet.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RequestListViewModel : ViewModel() {

    private val _datasetRequests = MutableStateFlow<List<DatasetRequestItem>>(emptyList())
    val datasetRequests: StateFlow<List<DatasetRequestItem>> = _datasetRequests

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchDatasetRequests()
    }

    private fun fetchDatasetRequests() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getDatasetRequests()
                if (response.isSuccessful && response.body()?.success == true) {
                    _datasetRequests.value = response.body()?.data ?: emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat data"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }
}
