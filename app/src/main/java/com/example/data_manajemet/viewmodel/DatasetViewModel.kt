package com.example.data_manajemet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.data.DatasetDao
import com.example.data_manajemet.repository.DatasetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DatasetViewModel(
    private val datasetDao: DatasetDao,
    private val repository: DatasetRepository
) : ViewModel() {

    private val _datasetList = MutableStateFlow<List<Dataset>>(emptyList())
    val datasetList: StateFlow<List<Dataset>> = _datasetList.asStateFlow()

    private val _userDatasetList = MutableStateFlow<List<Dataset>>(emptyList())
    val userDatasetList: StateFlow<List<Dataset>> = _userDatasetList.asStateFlow()

    private val _allDatasets = MutableStateFlow<List<Dataset>>(emptyList())
    val allDatasets: StateFlow<List<Dataset>> = _allDatasets.asStateFlow()

    private val _selectedDataset = MutableStateFlow<Dataset?>(null)
    val selectedDataset: StateFlow<Dataset?> = _selectedDataset.asStateFlow()

    init {
        // Saat ViewModel dibuat, langsung collect semua dataset dari Room
        loadAllDatasets()

        // Juga jalankan sinkronisasi API
        syncDatasets()
    }

    /**
     * Collect live update semua dataset dari Room
     */
    fun loadAllDatasets() {
        viewModelScope.launch {
            datasetDao.getAllDatasets()
                .collectLatest { datasets ->
                    _datasetList.value = datasets
                    _allDatasets.value = datasets
                }
        }
    }

    /**
     * Ambil dataset milik user tertentu
     */
    fun loadDatasetsByUser(userId: Int) {
        viewModelScope.launch {
            datasetDao.getDatasetsByUser(userId)
                .collectLatest { datasets ->
                    _userDatasetList.value = datasets
                }
        }
    }

    /**
     * Tambah dataset baru ke Room
     */
    fun addDataset(dataset: Dataset) {
        viewModelScope.launch {
            datasetDao.insert(dataset)
        }
    }

    /**
     * Update dataset
     */
    fun updateDataset(dataset: Dataset) {
        viewModelScope.launch {
            datasetDao.update(dataset)
        }
    }

    /**
     * Delete dataset
     */
    fun deleteDataset(dataset: Dataset) {
        viewModelScope.launch {
            datasetDao.delete(dataset)
        }
    }

    /**
     * Ambil satu dataset (langsung)
     */
    fun loadDatasetById(id: Int) {
        viewModelScope.launch {
            val dataset = datasetDao.getDatasetByIdNow(id)
            _selectedDataset.value = dataset
        }
    }

    /**
     * Observasi dataset secara terus-menerus
     */
    fun getDatasetByIdFlow(id: Int): Flow<Dataset?> {
        return datasetDao.getDatasetByIdFlow(id)
    }

    /**
     * Ambil dataset dari cache StateFlow
     */
    fun getDatasetById(id: Int): Dataset? {
        return _allDatasets.value.find { it.id == id }
    }

    /**
     * Sinkronisasi dataset dari API ke Room
     */
    fun syncDatasets() {
        viewModelScope.launch {
            try {
                repository.syncDatasets()
                // Tidak perlu panggil loadAllDatasets() di sini
                // Karena sudah auto-collect pada init
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


