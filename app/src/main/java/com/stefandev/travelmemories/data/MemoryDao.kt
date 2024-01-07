package com.stefandev.travelmemories.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MemoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // If a memory is already added, just ignore it
    suspend fun addMemory(memory: Memory)

    @Query("SELECT * FROM memory_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Memory>>
}