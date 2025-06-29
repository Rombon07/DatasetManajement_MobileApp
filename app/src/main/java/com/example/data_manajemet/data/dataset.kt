package com.example.data_manajemet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "datasets")
data class Dataset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val coverUri: String?,
    val datasetFileUri: String?,
    val uploadDate: String,
    val status: String = "New",
    val userId: Int
)


