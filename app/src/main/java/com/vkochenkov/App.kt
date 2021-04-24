package com.vkochenkov

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class App : Application() {

    var dataStore: DataStore? = null

    companion object {
        var instance: App? = null
    }

    override fun onCreate() {
        super.onCreate()
        dataStore = DataStore(applicationContext)
        instance = this
    }
}
