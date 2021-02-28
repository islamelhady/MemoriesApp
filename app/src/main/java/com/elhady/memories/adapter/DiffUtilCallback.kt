package com.elhady.memories.adapter

import androidx.recyclerview.widget.DiffUtil
import com.elhady.memories.model.Memories

/**
 * Created by islam elhady on 28-Feb-21.
 */
class DiffUtilCallback : DiffUtil.ItemCallback<Memories>() {

    override fun areItemsTheSame(oldItem: Memories, newItem: Memories): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Memories, newItem: Memories): Boolean {
        return oldItem.id == newItem.id
    }
}