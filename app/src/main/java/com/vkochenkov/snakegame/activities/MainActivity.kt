package com.vkochenkov.snakegame.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.vkochenkov.snakegame.R

class MainActivity : AppCompatActivity() {

    private lateinit var btnPlay: Button
    private lateinit var btnExit: Button
    private lateinit var btnShare: Button
    private lateinit var btnSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFields()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        btnPlay.setOnClickListener {
            val intent = Intent(this@MainActivity, SnakeGameScreenActivity::class.java)
            startActivity(intent)
        }
        btnSettings.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
        btnShare.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link_text))
            startActivity(Intent.createChooser(intent, null))
        }
        btnExit.setOnClickListener{ finish() }
    }

    private fun initFields() {
        btnPlay = findViewById(R.id.btn_play)
        btnExit = findViewById(R.id.btn_exit)
        btnShare = findViewById(R.id.btn_share)
        btnSettings = findViewById(R.id.btn_settings)
    }
}