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
    @Query("SELECT * FROM datasets")
    fun getAllDatasets(): Flow<List<Dataset>>

    @Query("SELECT * FROM datasets WHERE userId = :userId")
    fun getDatasetsByUser(userId: Int): Flow<List<Dataset>>

    @Query("SELECT * FROM datasets WHERE id = :id")
    fun getDatasetByIdNow(id: Int): Dataset?  // Untuk load 1x secara langsung

    @Query("SELECT * FROM datasets WHERE id = :id")
    fun observeDatasetById(id: Int): Flow<Dataset?> // Untuk pemantauan berkelanjutan

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dataset: Dataset)

    @Query("SELECT * FROM datasets WHERE id = :id LIMIT 1")
    fun getDatasetByIdFlow(id: Int): Flow<Dataset?>

    @Update
    suspend fun update(dataset: Dataset)

    @Delete
    suspend fun delete(dataset: Dataset)
}
