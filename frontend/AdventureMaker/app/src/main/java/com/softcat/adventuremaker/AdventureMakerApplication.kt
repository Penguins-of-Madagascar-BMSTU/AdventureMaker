package com.softcat.adventuremaker

import android.app.Application
import com.google.firebase.FirebaseApp

class AdventureMakerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}