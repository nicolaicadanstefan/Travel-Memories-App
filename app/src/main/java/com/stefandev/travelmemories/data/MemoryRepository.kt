package com.stefandev.travelmemories.data

import androidx.lifecycle.LiveData

class MemoryRepository(private val memoryDao: MemoryDao) {
    val readAllData: LiveData<List<Memory>> = memoryDao.readAllData()
    suspend fun addMemory(memory: Memory){
        memoryDao.addMemory(memory)
    }
}