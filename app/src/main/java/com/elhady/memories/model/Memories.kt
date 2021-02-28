package com.elhady.memories.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Created by islam elhady on 28-Feb-21.
 */
@Entity
data class Memories(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val color: Int = -1,
    val imagePath: String?
) : Serializable