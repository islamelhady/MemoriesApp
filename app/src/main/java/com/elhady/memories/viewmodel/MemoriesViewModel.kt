package com.elhady.memories.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elhady.memories.model.Memories
import com.elhady.memories.repository.MemoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by islam elhady on 28-Feb-21.
 */
class MemoriesViewModel(private val repository: MemoriesRepository) : ViewModel() {


    private var imagePath: String? = null

    fun saveImagePath(path: String?) {
        imagePath = path
    }

    fun setImagePath(): String? {
        if (imagePath != null)
            return imagePath
        return null
    }

    fun getAllMemories(): LiveData<List<Memories>> {
        return repository.getAllMemories()
    }

    fun searchMemories(query: String): LiveData<List<Memories>> {
        return repository.searchMemories(query)
    }

    fun saveMemories(newMemory: Memories) = viewModelScope.launch(Dispatchers.IO) {
            repository.addMemories(newMemory)
        }



    fun updateMemories(existingMemories: Memories) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateMemories(existingMemories)
    }

    fun deleteMemories(existingMemories: Memories) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteMemories(existingMemories)
    }

    override fun onCleared() {
        imagePath = null
        super.onCleared()
    }

}