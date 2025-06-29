package com.example.data_manajemet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DatasetDao {

    // Ambil semua dataset
    @Query("SELECT * FROM datasets")
    fun getAllDatasets(): Flow<List<Dataset>>

    // Ambil dataset milik user tertentu
    @Query("SELECT * FROM datasets WHERE userId = :userId")
    fun getDatasetsByUser(userId: Int): Flow<List<Dataset>>

    // Ambil 1 dataset secara langsung (bukan Flow)
    @Query("SELECT * FROM datasets WHERE id = :id")
    fun getDatasetByIdNow(id: Int): Dataset?

    // Observasi dataset secara berkelanjutan
    @Query("SELECT * FROM datasets WHERE id = :id")
    fun getDatasetByIdFlow(id: Int): Flow<Dataset?>

    @Query("SELECT * FROM datasets")
    suspend fun getAllDatasetsOnce(): List<Dataset>

    // Alias lain observasi dataset
    @Query("SELECT * FROM datasets WHERE id = :id")
    fun observeDatasetById(id: Int): Flow<Dataset?>

    // Masukkan 1 dataset
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dataset: Dataset)

    // Masukkan banyak dataset sekaligus
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(datasets: List<Dataset>)

    // Update dataset
    @Update
    suspend fun update(dataset: Dataset)

    // Hapus dataset
    @Delete
    suspend fun delete(dataset: Dataset)

    // Hapus semua dataset
    @Query("DELETE FROM datasets")
    suspend fun clearAll()
}