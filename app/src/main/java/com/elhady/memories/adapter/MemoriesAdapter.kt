package com.elhady.memories.adapter

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elhady.memories.R
import com.elhady.memories.databinding.ItemMemoriesBinding
import com.elhady.memories.model.Memories
import com.elhady.memories.ui.fragments.MemoriesFragmentDirections
import com.elhady.memories.utils.hideKeyboard
import com.elhady.memories.utils.loadHiRezThumbnail
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak
import java.io.File

/**
 * Created by islam elhady on 28-Feb-21.
 */
class MemoriesAdapter :
    androidx.recyclerview.widget.ListAdapter<Memories, MemoriesAdapter.MemoriesViewHolder>(
        DiffUtilCallback()
    ) {

    inner class MemoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentBinding = ItemMemoriesBinding.bind(itemView)
        val title: MaterialTextView = contentBinding.memoriesItemTitle
        val content: TextView = contentBinding.memoriesContentItemTitle
        val date: MaterialTextView = contentBinding.memoriesDate
        val image: ImageView = contentBinding.itemMemoriesImage
        val parent: MaterialCardView = contentBinding.memoriesItemLayoutParent
        val markWon = Markwon.builder(itemView.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(
                        SoftLineBreak::class.java
                    ) { visitor, _ -> visitor.forceNewLine() }
                }
            })
            .build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoriesViewHolder {
        return MemoriesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_memories, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MemoriesViewHolder, position: Int) {

        getItem(position).let { memories ->

            holder.apply {
                parent.transitionName = "recyclerView_${memories.id}"

                title.text = memories.title
                markWon.setMarkdown(content, memories.content)
                date.text = memories.date
                if (memories.imagePath != null) {
                    image.visibility = View.VISIBLE
                    val uri = Uri.fromFile(File(memories.imagePath))
                    if (File(memories.imagePath).exists())
                        itemView.context.loadHiRezThumbnail(uri, image)
                } else {
                    Glide.with(itemView).clear(image)
                    image.isVisible = false
                }

                parent.setCardBackgroundColor(memories.color)

                itemView.setOnClickListener {
                    val action =
                        MemoriesFragmentDirections.actionMemoriesFragmentToMemoriesContentFragment()
                            .setMemories(memories)
                    val extras = FragmentNavigatorExtras(parent to "recyclerView_${memories.id}")
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action, extras)
                }
                content.setOnClickListener {
                    val action =
                        MemoriesFragmentDirections.actionMemoriesFragmentToMemoriesContentFragment()
                            .setMemories(memories)
                    val extras = FragmentNavigatorExtras(parent to "recyclerView_${memories.id}")
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action, extras)
                }
            }
        }
    }

}