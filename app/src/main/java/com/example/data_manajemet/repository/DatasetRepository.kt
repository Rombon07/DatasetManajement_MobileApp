package com.example.data_manajemet.repository

import com.example.data_manajemet.data.Dataset
import com.example.data_manajemet.data.DatasetDao
import com.example.data_manajemet.model.DatasetItem
import com.example.data_manajemet.model.DatasetUploadRequest
import com.example.data_manajemet.network.DatasetApiService

class DatasetRepository(
    private val apiService: DatasetApiService,
    private val datasetDao: DatasetDao
) {

    /**
     * Sinkronisasi dataset dari server ke Room
     */
    suspend fun syncDatasets() {
        val remoteDatasets = apiService.getDatasets()
        val entities = remoteDatasets.map {
            Dataset(
                id = 0, // biarkan Room autoGenerate
                name = it.name,
                description = it.description,
                coverUri = it.cover_image,
                datasetFileUri = it.data_file,
                uploadDate = it.uploaded_at,
                status = it.status,
                userId = it.owner
            )
        }

        datasetDao.clearAll()
        datasetDao.insertAll(entities)
    }

    /**
     * Flow untuk observe data lokal
     */
    fun getAllDatasets() = datasetDao.getAllDatasets()

    /**
     * One-shot load semua dataset lokal
     */
    suspend fun getLocalDatasets(): List<DatasetItem> {
        val entities = datasetDao.getAllDatasetsOnce()
        return entities.map {
            DatasetItem(
                id = it.id,
                name = it.name,
                description = it.description,
                cover_image = it.coverUri ?: "",
                data_file = it.datasetFileUri ?: "",
                uploaded_at = it.uploadDate,
                status = it.status,
                owner = it.userId
            )
        }
    }

    /**
     * Upload dataset baru ke server
     */
    suspend fun uploadDataset(dataset: DatasetUploadRequest) =
        apiService.uploadDataset(dataset)
}
