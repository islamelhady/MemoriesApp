package com.elhady.memories.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elhady.memories.MemoriesActivity

/**
 * Created by islam elhady on 26-Feb-21.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this@SplashActivity, MemoriesActivity::class.java)
        startActivity(intent)
        finish()
    }
}