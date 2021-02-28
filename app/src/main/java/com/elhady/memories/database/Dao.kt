package com.elhady.memories.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.elhady.memories.model.Memories

/**
 * Created by islam elhady on 28-Feb-21.
 */
@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMemories(memories: Memories)

    @Update
    suspend fun updateMemories(memories: Memories)

    @Query("SELECT * FROM Memories ORDER BY id DESC")
    fun getAllMemories(): LiveData<List<Memories>>

    @Query("SELECT * FROM Memories WHERE title LIKE :query OR content LIKE :query OR date LIKE :query ORDER BY id DESC")
    fun searchMemories(query: String): LiveData<List<Memories>>

    @Delete
    suspend fun deleteMemories(memories: Memories)
}