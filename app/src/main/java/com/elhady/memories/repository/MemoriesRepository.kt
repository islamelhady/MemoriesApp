package com.elhady.memories.repository

import com.elhady.memories.database.MemoriesDatabase
import com.elhady.memories.model.Memories

/**
 * Created by islam elhady on 28-Feb-21.
 */
class MemoriesRepository(private val database: MemoriesDatabase) {

    fun getMemories() =
        database.getMemoriesDao().getAllMemories()

    fun searchMemories(query: String) =
        database.getMemoriesDao().searchMemories(query)

    suspend fun addMemories(memories: Memories) =
        database.getMemoriesDao().addMemories(memories)

    suspend fun updateMemories(memories: Memories) =
        database.getMemoriesDao().updateMemories(memories)

}