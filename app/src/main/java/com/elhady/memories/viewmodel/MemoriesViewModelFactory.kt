package com.elhady.memories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elhady.memories.repository.MemoriesRepository

/**
 * Created by islam elhady on 28-Feb-21.
 */
@Suppress("UNCHECKED_CAST")
class MemoriesViewModelFactory(private val repository: MemoriesRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MemoriesViewModel(repository) as T
    }
}