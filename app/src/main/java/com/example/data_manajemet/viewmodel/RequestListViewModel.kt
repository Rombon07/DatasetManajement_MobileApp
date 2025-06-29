package com.example.data_manajemet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_manajemet.model.CombinedItem
import com.example.data_manajemet.model.DatasetUploadRequest
import com.example.data_manajemet.model.RequestListItem
import com.example.data_manajemet.repository.RequestRepository
import com.example.data_manajemet.repository.DatasetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RequestListViewModel(
    private val requestRepository: RequestRepository,
    private val datasetRepository: DatasetRepository
) : ViewModel() {

    private val _combinedList = MutableStateFlow<List<CombinedItem>>(emptyList())
    val combinedList: StateFlow<List<CombinedItem>> = _combinedList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    /**
     * Memuat semua data Request + Dataset lokal dan digabung
     */
    fun loadAllData() {
        viewModelScope.launch {
            try {
                val requests = requestRepository.getRequestList()
                val datasets = datasetRepository.getLocalDatasets()

                val combined = mutableListOf<CombinedItem>()

                // Tambah semua request dulu
                combined.addAll(requests.map { CombinedItem.Request(it) })

                // Tambah semua dataset lokal
                combined.addAll(datasets.map { CombinedItem.Dataset(it) })

                // Urutkan kalau perlu, contoh: terbaru dulu
                _combinedList.value = combined.sortedByDescending {
                    when (it) {
                        is CombinedItem.Request -> it.item.timestamp
                        is CombinedItem.Dataset -> it.item.uploaded_at
                    }
                }

                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            }
        }
    }

    /**
     * Konfirmasi pemilihan dataset ke request tertentu
     */
    fun konfirmasiPilihDataset(requestId: Int, datasetId: Int) {
        viewModelScope.launch {
            try {
                val response = requestRepository.konfirmasiPilihDataset(requestId, datasetId)
                if (response.isSuccessful) {
                    _successMessage.value = "Dataset berhasil dikaitkan."
                    _errorMessage.value = null
                    // Refresh data agar up-to-date
                    loadAllData()
                } else {
                    _errorMessage.value = "Gagal mengaitkan dataset: ${response.code()}"
                    _successMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _successMessage.value = null
            }
        }
    }


    fun uploadDataset(dataset: DatasetUploadRequest) {
        viewModelScope.launch {
            try {
                val response = datasetRepository.uploadDataset(dataset)
                // Simpan ID server (response.id) ke Room
                _successMessage.value = "Dataset berhasil diupload ke server."
            } catch (e: Exception) {
                _errorMessage.value = "Gagal upload: ${e.message}"
            }
        }
    }


}
