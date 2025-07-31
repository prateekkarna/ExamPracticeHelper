package com.abhyasa

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExamPractiseHelperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the database to trigger predefined session insertion
        com.abhyasa.data.database.PracticeDatabase.getInstance(applicationContext)
    }
}
