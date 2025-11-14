package com.interswitch.iswtillsdksample

import android.app.Application
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the Prefs class
        Prefs.Builder().setContext(this)
    }
}