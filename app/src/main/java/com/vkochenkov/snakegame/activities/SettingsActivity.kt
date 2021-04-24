package com.vkochenkov.snakegame.activities

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vkochenkov.App
import com.vkochenkov.DataStore
import com.vkochenkov.snakegame.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var seekBar: SeekBar
    private var toast: Toast? = null
    private var dataStore: DataStore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        dataStore = App.instance!!.dataStore

        seekBar = findViewById(R.id.sb_settings_speed)
        seekBar.progress = dataStore!!.getSpeed()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                dataStore!!.setSpeed(seekBar.progress)
                toast?.cancel()
                toast = Toast.makeText(
                    this@SettingsActivity,
                    "Speed is ${dataStore!!.getSpeed()} ms",
                    Toast.LENGTH_SHORT
                )
                toast?.show()

            }
        })
    }
}