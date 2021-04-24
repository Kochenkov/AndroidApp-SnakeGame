package com.vkochenkov

import android.content.Context

class DataStore(val context: Context) {

    companion object {
        val SNAKE_GAME_PREFERENCES = "SNAKE_GAME_PREFERENCES"
        val BEST_SCORE = "BEST_SCORE"
        val SNAKE_SPEED = "SNAKE_SPEED"
        val snakeSpeedDefaultValue = 500
    }

    fun setSpeed(speed: Int) {
        val preferences = context.getSharedPreferences(SNAKE_GAME_PREFERENCES, 0)
        preferences.edit().putInt(SNAKE_SPEED, speed).apply()
    }

    fun getSpeed(): Int {
        val preferences = context.getSharedPreferences(SNAKE_GAME_PREFERENCES, 0)
        return preferences.getInt(SNAKE_SPEED, snakeSpeedDefaultValue)
    }

    fun setBestScore(score: Int) {
        val preferences = context.getSharedPreferences(SNAKE_GAME_PREFERENCES, 0)
        preferences.edit().putInt(BEST_SCORE, score).apply()
    }

    fun getBestScore(defaultValue: Int): Int {
        val preferences = context.getSharedPreferences(SNAKE_GAME_PREFERENCES, 0)
        return preferences.getInt(BEST_SCORE, defaultValue)
    }
}