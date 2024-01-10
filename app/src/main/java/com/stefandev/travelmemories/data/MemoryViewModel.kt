package com.stefandev.travelmemories.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoryViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<Memory>>
    private val repository: MemoryRepository

    init {
        val memoryDao = MemoryDatabase.getDatabase(application).memoryDao()
        repository = MemoryRepository(memoryDao)
        readAllData = repository.readAllData
    }

    fun addMemory(memory: Memory, onMemoryAdded: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMemory(memory)
            launch(Dispatchers.Main) {
                onMemoryAdded() // Call the passed callback function
            }
        }
    }

    fun deleteAllMemories(onCompleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllMemories()
            launch(Dispatchers.Main) {
                onCompleted()
            }
        }
    }
}