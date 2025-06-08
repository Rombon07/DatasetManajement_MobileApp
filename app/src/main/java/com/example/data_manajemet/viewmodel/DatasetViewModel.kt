package com.example.data_manajemet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.data.DatasetDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DatasetViewModel(private val datasetDao: DatasetDao) : ViewModel() {

    private val _datasetList = MutableStateFlow<List<Dataset>>(emptyList())
    val datasetList: StateFlow<List<Dataset>> = _datasetList

    private val _userDatasetList = MutableStateFlow<List<Dataset>>(emptyList())
    val userDatasetList: StateFlow<List<Dataset>> = _userDatasetList

    private val _allDatasets = MutableStateFlow<List<Dataset>>(emptyList())
    val allDatasets = _allDatasets.asStateFlow()

    // Untuk dataset detail berdasarkan ID
    private val _selectedDataset = MutableStateFlow<Dataset?>(null)
    val selectedDataset: StateFlow<Dataset?> = _selectedDataset.asStateFlow()

    init {
        loadAllDatasets()
    }

    fun loadAllDatasets() {
        viewModelScope.launch {
            datasetDao.getAllDatasets().collectLatest {
                _datasetList.value = it
                _allDatasets.value = it
            }
        }
    }

    fun loadDatasetsByUser(userId: Int) {
        viewModelScope.launch {
            datasetDao.getDatasetsByUser(userId).collectLatest { datasets ->
                _userDatasetList.value = datasets
            }
        }
    }

    fun addDataset(dataset: Dataset) {
        viewModelScope.launch {
            datasetDao.insert(dataset)
            loadAllDatasets()
            loadDatasetsByUser(dataset.userId)
        }
    }

    fun updateDataset(dataset: Dataset) {
        viewModelScope.launch {
            datasetDao.update(dataset)
            loadAllDatasets()
            loadDatasetsByUser(dataset.userId)
        }
    }

    fun deleteDataset(dataset: Dataset) {
        viewModelScope.launch {
            datasetDao.delete(dataset)
            loadAllDatasets()
            loadDatasetsByUser(dataset.userId)
        }
    }

    // Ambil dataset berdasarkan ID dari Room (langsung, satu kali ambil)
    fun loadDatasetById(id: Int) {
        viewModelScope.launch {
            val dataset = datasetDao.getDatasetByIdNow(id)
            _selectedDataset.value = dataset
        }
    }

    // Jika ingin ambil terus-menerus via Flow dari Room
    fun getDatasetByIdFlow(id: Int): Flow<Dataset?> {
        return datasetDao.getDatasetByIdFlow(id)
    }

    // (Opsional) jika kamu masih butuh function synchronus
    fun getDatasetById(id: Int): Dataset? {
        return _allDatasets.value.find { it.id == id }
    }
}
