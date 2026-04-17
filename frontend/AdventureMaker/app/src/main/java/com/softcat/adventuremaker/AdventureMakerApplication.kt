package com.softcat.adventuremaker

import android.app.Application
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.FirebaseApp
import com.softcat.adventuremaker.di.dataModule
import com.softcat.adventuremaker.di.repositoryModule
import com.softcat.adventuremaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AdventureMakerApplication: Application() {
// HERE IS AI TEST
    val dataStore by preferencesDataStore(
        name = DATASTORE_NAME
    )

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AdventureMakerApplication)
            modules(viewModelModule, repositoryModule, dataModule)
        }
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(applicationContext)
        }
    }

    companion object {
        const val DATASTORE_NAME = "adventure_maker_datastore"
    }
}