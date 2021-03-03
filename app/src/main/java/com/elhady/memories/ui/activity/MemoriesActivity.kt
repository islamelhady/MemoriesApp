package com.elhady.memories.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.elhady.memories.database.MemoriesDatabase
import com.elhady.memories.databinding.ActivityMemoriesBinding
import com.elhady.memories.repository.MemoriesRepository
import com.elhady.memories.utils.shortToast
import com.elhady.memories.viewmodel.MemoriesViewModel
import com.elhady.memories.viewmodel.MemoriesViewModelFactory

/**
 * Created by islam elhady on 25-Feb-21.
 */
class MemoriesActivity : AppCompatActivity() {

    lateinit var memoriesViewModel: MemoriesViewModel
    private lateinit var binding: ActivityMemoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoriesBinding.inflate(layoutInflater)
        try {
            setContentView(binding.root)
            val memoriesRepository = MemoriesRepository(MemoriesDatabase(this))
            val noteViewModelProviderFactory = MemoriesViewModelFactory(memoriesRepository)
            memoriesViewModel = ViewModelProvider(
                this,
                noteViewModelProviderFactory
            )[MemoriesViewModel::class.java]
        } catch (e: Exception) {
            shortToast("error occurred")
        }
    }
}