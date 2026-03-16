package com.softcat.adventuremaker

import android.app.Application
import com.google.firebase.FirebaseApp
import com.softcat.adventuremaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AdventureMakerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        startKoin {
            androidContext(this@AdventureMakerApplication)
            modules(viewModelModule)
        }
    }
}