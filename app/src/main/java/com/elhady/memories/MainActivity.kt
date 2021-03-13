package com.elhady.memories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.elhady.memories.database.MemoriesDatabase
import com.elhady.memories.databinding.ActivityMainBinding
import com.elhady.memories.repository.MemoriesRepository
import com.elhady.memories.utils.shortToast
import com.elhady.memories.viewmodel.MemoriesViewModel
import com.elhady.memories.viewmodel.MemoriesViewModelFactory

/**
 * Created by islam elhady on 25-Feb-21.
 */
class MainActivity : AppCompatActivity() {

    lateinit var memoriesViewModel: MemoriesViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        try {

            setContentView(binding.root)
            val memoriesRepository = MemoriesRepository(MemoriesDatabase(this))
            val noteViewModelProviderFactory = MemoriesViewModelFactory(memoriesRepository)
            memoriesViewModel = ViewModelProvider(
                this, noteViewModelProviderFactory
            ).get(MemoriesViewModel::class.java)

        } catch (e: Exception) {
            shortToast("error occurred")
        }
    }
}