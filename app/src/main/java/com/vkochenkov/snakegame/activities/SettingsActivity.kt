package com.vkochenkov.snakegame.activities

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vkochenkov.Data
import com.vkochenkov.snakegame.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var seekBar: SeekBar
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        seekBar = findViewById(R.id.sb_settings_speed)
        seekBar.progress = Data.speedValue
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Data.speedValue = seekBar.progress
                toast?.cancel()
                toast = Toast.makeText(
                    this@SettingsActivity,
                    "Speed is ${Data.speedValue} ms",
                    Toast.LENGTH_SHORT
                )
                toast?.show()

            }
        })
    }
}