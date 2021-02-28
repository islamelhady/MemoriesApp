package com.elhady.memories.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.elhady.memories.model.Memories

/**
 * Created by islam elhady on 28-Feb-21.
 */
@Database(
    entities = [Memories::class],
    version = 1,
    exportSchema = false
)
abstract class MemoriesDatabase : RoomDatabase() {

    abstract fun getMemoriesDao(): Dao

    companion object {

        @Volatile
        private var instance: MemoriesDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            MemoriesDatabase::class.java,
            "note_database"
        ).build()
    }

}