package com.stefandev.travelmemories.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "memory_table")
data class Memory (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val memoryName: String,
    val date: Date,
    val location: String,
    val details: String,
    val favorite: Boolean
)